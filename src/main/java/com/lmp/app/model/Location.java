package com.lmp.app.model;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class Location {
    private String type;
    private double[] coordinates;
    public Location(){

    }
    public String getType() {
      return type;
    }
    public void setType(String type) {
      this.type = type;
    }
    public double[] getCoordinates() {
      return coordinates;
    }
    public void setCoordinates(double[] coordinates) {
      if(coordinates == null || coordinates.length < 2) {
        Assert.notNull(coordinates, "coordinates must not be null or less than 2!");
      }
      if(coordinates[0] > coordinates[1]) {
        double temp = coordinates[0];
        coordinates[0] = coordinates[1];
        coordinates[1] = temp;
      }
      this.coordinates = coordinates;
    }
  }
  