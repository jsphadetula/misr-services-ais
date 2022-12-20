package com.misr.ais.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "lands")
public class Land extends BaseEntity {

  private String name;
  private Double area;

  @JsonManagedReference
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "land")
  private List<LandCoordinate> landCoordinates;

  @OneToOne(optional = true)
  private LandConfiguration landConfiguration;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getArea() {
    return this.area;
  }

  public void setArea(Double area) {
    this.area = area;
  }

  public List<LandCoordinate> getLandCoordinates() {
    return this.landCoordinates;
  }

  public void setLandCoordinates(List<LandCoordinate> landCoordinates) {
    this.landCoordinates = landCoordinates;
  }

  public LandConfiguration getLandConfiguration() {
    return this.landConfiguration;
  }

  public void setLandConfiguration(LandConfiguration landConfiguration) {
    this.landConfiguration = landConfiguration;
  }

}
