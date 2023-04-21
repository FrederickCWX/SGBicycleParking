package com.vttp2022.BicycleParkingApp.models.mysql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;

public class Bookings {

  private Integer bookingId;
  private String email;
  private String bookingDate;
  private String image;
  private String description;
  private String rackType;
  private Integer rackCount;
  private String sheltered;

  public Integer getBookingId() { return bookingId; }
  public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getBookingDate() { return bookingDate; }
  public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

  public String getImage() { return image; }
  public void setImage(String image) { this.image = image; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public String getRackType() { return rackType; }
  public void setRackType(String rackType) { this.rackType = rackType; }
  
  public Integer getRackCount() { return rackCount; }
  public void setRackCount(Integer rackCount) { this.rackCount = rackCount; }

  public String getSheltered() { return sheltered; }
  public void setSheltered(String sheltered) { this.sheltered = sheltered; }

  public static Bookings create(SqlRowSet rs) {
    Bookings b = new Bookings();

    b.setBookingId(rs.getInt("booking_id"));
    b.setEmail(rs.getString("email"));
    b.setBookingDate(rs.getString("booking_date"));
    b.setImage(rs.getString("image"));
    b.setDescription(rs.getString("description"));
    b.setRackType(rs.getString("rack_type"));
    b.setRackCount(rs.getInt("rack_count"));
    b.setSheltered(rs.getString("sheltered"));

    return b;
  }

  public static Bookings createJson(String json) throws IOException {
    Bookings b = new Bookings();

    try(InputStream in = new ByteArrayInputStream(json.getBytes())) {
      JsonReader jr = Json.createReader(in);
      JsonObject jo = jr.readObject();

      JsonString jsEmail = jo.getJsonString("email");
      b.email = jsEmail.getString();
      JsonString jsDate = jo.getJsonString("date");
      b.bookingDate = jsDate.getString();
      JsonString jsImg = jo.getJsonString("image");
      b.image = jsImg.getString();
      JsonString jsDesp = jo.getJsonString("description");
      b.description = jsDesp.getString();
      JsonString jsType = jo.getJsonString("rackType");
      b.rackType = jsType.getString();
      JsonNumber jnCount = jo.getJsonNumber("rackCount");
      b.rackCount = jnCount.intValue();
      JsonString jsShelter = jo.getJsonString("sheltered");
      b.sheltered = jsShelter.getString();

    }
    return b;
  }

  public static Bookings createTeleJson(JsonObject jo) throws IOException {
    Bookings b = new Bookings();

      JsonString jsEmail = jo.getJsonString("email");
      b.email = jsEmail.getString();
      JsonString jsDate = jo.getJsonString("bookingDate");
      b.bookingDate = jsDate.getString();
      JsonString jsImg = jo.getJsonString("image");
      b.image = jsImg.getString();
      JsonString jsDesp = jo.getJsonString("description");
      b.description = jsDesp.getString();
      JsonString jsType = jo.getJsonString("rackType");
      b.rackType = jsType.getString();
      JsonNumber jnCount = jo.getJsonNumber("rackCount");
      b.rackCount = jnCount.intValue();
      JsonString jsShelter = jo.getJsonString("sheltered");
      b.sheltered = jsShelter.getString();
    
    return b;
  }

  public JsonObject toBookings() {
    return Json.createObjectBuilder()
        .add("bookingId", bookingId)
        .add("email", email)
        .add("bookingDate", bookingDate)
        .add("image", image)
        .add("description", description)
        .add("rackType", rackType)
        .add("rackCount", rackCount)
        .add("sheltered", sheltered)
        .build();
  }

}
