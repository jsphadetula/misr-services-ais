package com.misr.ais.service;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.misr.ais.exception.ServerException;
import com.misr.ais.model.LandCoordinate;

@Service
public class IrrigationService {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final Random random = new Random();

  public void irrigateArea(List<LandCoordinate> path) {
    int rand = random.nextInt(10) + 1;
    if (rand > 7) {
      throw new ServerException("Irrigation service not available", new Exception());
    }

    log.info("Irrigation started successfully {}", path);
  }

}
