package com.vttp2022.BicycleParkingApp.models.parking;

import java.math.BigDecimal;

public class Query {

  private static BigDecimal lat;
  private static BigDecimal lng;
  private double dist;

  public static BigDecimal getLat() {
    return lat;
  }
  public void setLat(BigDecimal lat) {
    this.lat = lat;
  }

  public static BigDecimal getLng() {
    return lng;
  }
  public void setLng(BigDecimal lng) {
    this.lng = lng;
  }

  public double getRadius() {
    return dist;
  }
  public void setRadius(double dist) {
    this.dist = dist;
  }
  
}
