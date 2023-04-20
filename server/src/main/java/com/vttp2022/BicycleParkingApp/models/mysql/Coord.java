package com.vttp2022.BicycleParkingApp.models.mysql;

import java.math.BigDecimal;

public class Coord {

  protected BigDecimal lat;
  protected BigDecimal lng;

  public BigDecimal getLat() { return lat; }
  public void setLat(BigDecimal lat) { this.lat = lat; }

  public BigDecimal getLng() { return lng; }
  public void setLng(BigDecimal lng) { this.lng = lng; }
}
