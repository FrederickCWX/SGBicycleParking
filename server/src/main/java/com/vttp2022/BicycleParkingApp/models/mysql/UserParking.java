package com.vttp2022.BicycleParkingApp.models.mysql;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class UserParking {

  private Integer id;
  private Integer userDetailsId;
  private String parkingId;

  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }

  public Integer getUserDetailsId() { return userDetailsId; }
  public void setUserDetailsId(Integer userDetailsId) { this.userDetailsId = userDetailsId; }

  public String getParkingId() { return parkingId; }
  public void setParkingId(String parkingId) { this.parkingId = parkingId; }

  public JsonObject toUserParking() {
    return Json.createObjectBuilder()
        .add("id", id)
        .add("userDetailsId", userDetailsId)
        .add("parkingId", parkingId)
        .build();
  }

  
}
