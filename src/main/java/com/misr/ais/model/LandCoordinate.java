package com.misr.ais.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "land_coordinates")
public class LandCoordinate extends BaseEntity {

  private float latitude;
  private float longitude;

  @ManyToOne
  @JsonBackReference
  private Land land;

  public float getLatitude() {
    return this.latitude;
  }

  public void setLatitude(float latitude) {
    this.latitude = latitude;
  }

  public float getLongitude() {
    return this.longitude;
  }

  public void setLongitude(float longitude) {
    this.longitude = longitude;
  }

  public Land getLand() {
    return this.land;
  }

  public void setLand(Land land) {
    this.land = land;
  }

}
