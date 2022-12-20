package com.misr.ais.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.misr.ais.dto.land.ConfigureLandRequest;
import com.misr.ais.dto.land.LandRequest;
import com.misr.ais.exception.BadRequestException;
import com.misr.ais.exception.ResourceNotFoundException;
import com.misr.ais.helper.SphericalHelper;
import com.misr.ais.helper.SphericalHelper.LatLng;
import com.misr.ais.model.IrrigationSlot;
import com.misr.ais.model.Land;
import com.misr.ais.model.LandConfiguration;
import com.misr.ais.model.LandCoordinate;
import com.misr.ais.model.SlotStatus;
import com.misr.ais.repository.IrrigationSlotRepository;
import com.misr.ais.repository.LandConfigurationRepository;
import com.misr.ais.repository.LandCoordinateRepository;
import com.misr.ais.repository.LandRepository;

import io.vavr.control.Try;

@Service
public class LandService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final AlertService alertService;
  private final LandRepository landRepository;
  private final IrrigationService irrigationService;
  private final TransactionTemplate transactionTemplate;
  private final ValueOperations<String, String> valueOperations;
  private final LandCoordinateRepository landCoordinateRepository;
  private final IrrigationSlotRepository irrigationSlotRepository;
  private final LandConfigurationRepository landConfigurationRepository;

  public LandService( //
      AlertService alertService, //
      LandRepository landRepository, //
      IrrigationService irrigationService, //
      TransactionTemplate transactionTemplate, //
      ValueOperations<String, String> valueOperations, //
      LandCoordinateRepository landCoordinateRepository, //
      IrrigationSlotRepository irrigationSlotRepository, //
      LandConfigurationRepository landConfigurationRepository //
  ) {
    this.alertService = alertService;
    this.landRepository = landRepository;
    this.irrigationService = irrigationService;
    this.transactionTemplate = transactionTemplate;
    this.valueOperations = valueOperations;
    this.landCoordinateRepository = landCoordinateRepository;
    this.irrigationSlotRepository = irrigationSlotRepository;
    this.landConfigurationRepository = landConfigurationRepository;
  }

  public Land add(LandRequest body) {
    var path = body.boundaries().stream() //
        .map(x -> new LatLng(x.latitude(), x.longitude())) //
        .collect(Collectors.toList());
    double area = SphericalHelper.computeSignedArea(path);
    if (area == 0) {
      throw new BadRequestException("Supply valid land coordinates");
    }

    var land = new Land();
    land.setName(body.name());
    land.setArea(area);
    List<LandCoordinate> landCoordinates = buildLandCoordinates(body, land);

    transactionTemplate.execute(transactionStatus -> {
      landRepository.save(land);
      landCoordinateRepository.saveAll(landCoordinates);

      transactionStatus.flush();
      return null;
    });

    land.setLandCoordinates(landCoordinates);
    return land;
  }

  public Page<Land> list(Pageable pageable) {
    return landRepository.findAllPaged(pageable);
  }

  public Land edit(Long id, LandRequest body) {
    Land land = landRepository.findById(id) //
        .orElseThrow(() -> new ResourceNotFoundException("Specified plot of land not found"));

    var path = body.boundaries().stream() //
        .map(x -> new LatLng(x.latitude(), x.longitude())) //
        .collect(Collectors.toList());
    double area = SphericalHelper.computeSignedArea(path);
    if (area == 0) {
      throw new BadRequestException("Supply valid land coordinates");
    }

    land.setName(body.name());
    land.setArea(area);
    List<LandCoordinate> landCoordinates = buildLandCoordinates(body, land);

    transactionTemplate.execute(transactionStatus -> {
      landRepository.save(land);
      landCoordinateRepository.deleteAll(land.getLandCoordinates());
      landCoordinateRepository.saveAll(landCoordinates);

      transactionStatus.flush();
      return null;
    });

    land.setLandCoordinates(landCoordinates);
    return land;
  }

  public Land configure(Long id, ConfigureLandRequest body) {
    Land land = landRepository.findById(id) //
        .orElseThrow(() -> new ResourceNotFoundException("Specified plot of land not found"));

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime next = getNextSchedule(body.cron(), now);

    IrrigationSlot irrigationSlot = buildIrrigationSlot(land, next);
    LandConfiguration landConfiguration = buildLandConfiguration(body, land);
    land.setLandConfiguration(landConfiguration);

    transactionTemplate.execute(transactionStatus -> {
      landRepository.save(land);
      irrigationSlotRepository.save(irrigationSlot);
      landConfigurationRepository.save(landConfiguration);

      transactionStatus.flush();
      return null;
    });

    scheduleNextIrrigation(irrigationSlot, Duration.between(now, next));

    return land;
  }

  public void notifyIrrigationSensor(Long id) {
    var irrigationSlotOptional = irrigationSlotRepository.findById(id);
    if (irrigationSlotOptional.isEmpty()) {
      log.error("Scheduled slot not found");
    }

    var irrigationSlot = irrigationSlotOptional.get();
    Land land = irrigationSlot.getLand();
    LandConfiguration landConfiguration = land.getLandConfiguration();

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime nextSchedule = getNextSchedule(landConfiguration.getCron(), now);
    int retryCount = irrigationSlot.getRetryCount() + 1;

    var irrigationEither = Try.run(() -> irrigationService.irrigateArea(land.getLandCoordinates())).toEither();
    if (irrigationEither.isLeft() && retryCount > landConfiguration.getMaxRetry()) {
      alertService.notify(land);

      irrigationSlot.setSlotStatus(SlotStatus.FAILED);
      IrrigationSlot nextIrrigationSlot = buildIrrigationSlot(land, nextSchedule);
      irrigationSlotRepository.saveAll(List.of(irrigationSlot, nextIrrigationSlot));

      scheduleNextIrrigation(nextIrrigationSlot, Duration.between(now, nextSchedule));
      return;
    }

    var nextRetry = irrigationSlot.getSchedule().plusSeconds(landConfiguration.getRetryIntervalSeconds());
    if (irrigationEither.isLeft() && nextRetry.isAfter(nextSchedule)) {
      irrigationSlot.setSlotStatus(SlotStatus.FAILED);
      IrrigationSlot nextIrrigationSlot = buildIrrigationSlot(land, nextSchedule);
      irrigationSlotRepository.saveAll(List.of(irrigationSlot, nextIrrigationSlot));

      scheduleNextIrrigation(nextIrrigationSlot, Duration.between(now, nextSchedule));
      return;
    }

    if (irrigationEither.isLeft()) {
      irrigationSlot.setRetryCount(retryCount);
      irrigationSlotRepository.save(irrigationSlot);

      scheduleNextIrrigation(irrigationSlot, Duration.between(now, nextRetry));
      return;
    }

    irrigationSlot.setRetryCount(retryCount);
    irrigationSlot.setSlotStatus(SlotStatus.SUCCESSFUL);

    IrrigationSlot nextIrrigationSlot = buildIrrigationSlot(land, nextSchedule);
    irrigationSlotRepository.saveAll(List.of(irrigationSlot, nextIrrigationSlot));

    scheduleNextIrrigation(nextIrrigationSlot, Duration.between(now, nextSchedule));
  }

  private List<LandCoordinate> buildLandCoordinates(LandRequest body, Land land) {
    var landCoordinates = body.boundaries().stream() //
        .map(x -> {
          var landCoordinate = new LandCoordinate();
          landCoordinate.setLand(land);
          landCoordinate.setLatitude(x.latitude());
          landCoordinate.setLongitude(x.longitude());
          return landCoordinate;
        }) //
        .collect(Collectors.toList());
    return landCoordinates;
  }

  private IrrigationSlot buildIrrigationSlot(Land land, LocalDateTime next) {
    var irrigationSlot = new IrrigationSlot();
    irrigationSlot.setRetryCount(0);
    irrigationSlot.setSchedule(next);
    irrigationSlot.setSlotStatus(SlotStatus.PENDING);
    irrigationSlot.setLand(land);
    return irrigationSlot;
  }

  private LandConfiguration buildLandConfiguration(ConfigureLandRequest body, Land land) {
    LandConfiguration landConfiguration = land.getLandConfiguration();
    if (Objects.isNull(landConfiguration)) {
      landConfiguration = new LandConfiguration();
    }
    landConfiguration.setCron(body.cron());
    landConfiguration.setMaxRetry(body.maxRetry());
    landConfiguration.setRetryIntervalSeconds(body.retryIntervalSeconds());
    return landConfiguration;
  }

  private void scheduleNextIrrigation(IrrigationSlot slot, Duration duration) {
    var key = String.format("irrigation-slot:%s:schedule", slot.getId());
    valueOperations.set(key, slot.getSchedule().toString(), duration);
  }

  private LocalDateTime getNextSchedule(String cron, LocalDateTime from) {
    CronExpression cronTrigger = Try.of(() -> CronExpression.parse(cron)) //
        .getOrElseThrow(() -> new BadRequestException("Supply valid cron configuration"));

    return cronTrigger.next(from);
  }

}
