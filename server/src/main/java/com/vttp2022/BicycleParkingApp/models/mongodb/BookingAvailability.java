package com.vttp2022.BicycleParkingApp.models.mongodb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;

public class BookingAvailability {

  private String image;
  private Integer availability;
  private String date;

  public String getImage() { return image; }
  public void setImage(String image) { this.image = image; }

  public Integer getAvailability() { return availability; }
  public void setAvailability(Integer availability) { this.availability = availability; }

  public String getDate() { return date; }
  public void setDate(String date) {this.date = date; }

  public static BookingAvailability createJson(String json) throws IOException {
    BookingAvailability ba = new BookingAvailability();

    try(InputStream in = new ByteArrayInputStream(json.getBytes())) {
      JsonReader jr = Json.createReader(in);
      JsonObject jo = jr.readObject();

      JsonString jsImg = jo.getJsonString("image");
      ba.image = jsImg.getString();
      JsonString jsDate = jo.getJsonString("bookingDate");
      ba.date = jsDate.getString();
      JsonNumber jnAvailability = jo.getJsonNumber("availability");
      if(jnAvailability != null) ba.availability = jnAvailability.intValue();

    }
    return ba;
  }

  // private String parkingId;
  // private String image;
  // private String description;
  // private String rackType;
  // private Integer availability;
  // private Integer rackCount;
  // private String sheltered;
  // private BigDecimal lat;
  // private BigDecimal lng;

  // public String getParkingId() { return parkingId; }
  // public void setParkingId(String parkingId) { this.parkingId = parkingId; }

  // public String getImage() { return image; }
  // public void setImage(String image) { this.image = image; }

  // public String getDescription() { return description; }
  // public void setDescription(String description) { this.description = description; }

  // public String getRackType() { return rackType; }
  // public void setRackType(String rackType) { this.rackType = rackType; }

  // public Integer getAvailability() { return availability; }
  // public void setAvailability(Integer availability) { this.availability = availability; }

  // public Integer getRackCount() { return rackCount; }
  // public void setRackCount(Integer rackCount) { this.rackCount = rackCount; }

  // public String getSheltered() { return sheltered; }
  // public void setSheltered(String sheltered) { this.sheltered = sheltered; }

  // public BigDecimal getLat() { return lat; }
  // public void setLat( BigDecimal lat) { this.lat = lat; }

  // public BigDecimal getLng() { return lng; }
  // public void setLng( BigDecimal lng) { this.lng = lng; }
  
}
