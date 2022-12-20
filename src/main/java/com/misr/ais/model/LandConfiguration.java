package com.misr.ais.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "land_configurations")
public class LandConfiguration extends BaseEntity {

  private String cron;
  private Integer maxRetry;
  private Integer retryIntervalSeconds;

  public String getCron() {
    return this.cron;
  }

  public void setCron(String cron) {
    this.cron = cron;
  }

  public Integer getMaxRetry() {
    return this.maxRetry;
  }

  public void setMaxRetry(Integer maxRetry) {
    this.maxRetry = maxRetry;
  }

  public Integer getRetryIntervalSeconds() {
    return this.retryIntervalSeconds;
  }

  public void setRetryIntervalSeconds(Integer retryIntervalSeconds) {
    this.retryIntervalSeconds = retryIntervalSeconds;
  }

}
