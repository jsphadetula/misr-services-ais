package com.misr.ais.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "irrigation_slots")
public class IrrigationSlot extends BaseEntity {

  private Integer retryCount;
  private LocalDateTime schedule;

  @Enumerated(EnumType.STRING)
  private SlotStatus slotStatus;

  @ManyToOne
  private Land land;

  public Integer getRetryCount() {
    return this.retryCount;
  }

  public void setRetryCount(Integer retryCount) {
    this.retryCount = retryCount;
  }

  public LocalDateTime getSchedule() {
    return this.schedule;
  }

  public void setSchedule(LocalDateTime schedule) {
    this.schedule = schedule;
  }

  public SlotStatus getSlotStatus() {
    return this.slotStatus;
  }

  public void setSlotStatus(SlotStatus slotStatus) {
    this.slotStatus = slotStatus;
  }

  public Land getLand() {
    return this.land;
  }

  public void setLand(Land land) {
    this.land = land;
  }

}
