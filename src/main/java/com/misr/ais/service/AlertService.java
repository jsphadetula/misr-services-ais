package com.misr.ais.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.misr.ais.model.Land;

@Service
public class AlertService {

  private final Logger log = LoggerFactory.getLogger(getClass());

  public void notify(Land land) {
    log.info("Land {} with area {} could not be irrigated", land.getName(), land.getArea());
  }

}
