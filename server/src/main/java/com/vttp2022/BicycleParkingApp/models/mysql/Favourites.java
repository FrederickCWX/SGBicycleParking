package com.vttp2022.BicycleParkingApp.models.mysql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;

public class Favourites extends Coord{

  private String parkingId;
  private String image;
  private String description;
  private String rackType;
  private Integer rackCount;
  private String sheltered;
  private Coord coord;

  public String getParkingId() { return parkingId; }
  public void setParkingId(String parkingId) { this.parkingId = parkingId; }

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

  public Coord getCoord() { return coord; }
  public void setCoord( Coord coord) { this.coord = coord; }

  public static Favourites create(SqlRowSet rs) {
    Favourites f = new Favourites();

    f.setParkingId(rs.getString("parking_id"));
    f.setImage(rs.getString("image"));
    f.setDescription(rs.getString("description"));
    f.setRackType(rs.getString("rack_type"));
    f.setRackCount(rs.getInt("rack_count"));
    f.setSheltered(rs.getString("sheltered"));

    return f;
  }

  public static Favourites createJson(String json) throws IOException {
    Favourites f = new Favourites();
    Coord c = new Coord();

    try(InputStream in = new ByteArrayInputStream(json.getBytes())) {
      JsonReader jr = Json.createReader(in);
      JsonObject jo = jr.readObject();

      JsonString jsId = jo.getJsonString("parkingId");
      f.parkingId = jsId.getString();
      JsonString jsImg = jo.getJsonString("image");
      f.image = jsImg.getString();
      JsonString jsDesp = jo.getJsonString("description");
      f.description = jsDesp.getString();
      JsonString jsType = jo.getJsonString("rackType");
      f.rackType = jsType.getString();
      JsonNumber jnCount = jo.getJsonNumber("rackCount");
      f.rackCount = jnCount.intValue();
      JsonString jsShelter = jo.getJsonString("sheltered");
      f.sheltered = jsShelter.getString();

      JsonNumber jnLat = jo.getJsonNumber("lat");
      c.lat = jnLat.bigDecimalValue();
      JsonNumber jnLng = jo.getJsonNumber("lng");
      c.lng = jnLng.bigDecimalValue();
      f.coord = c;
    }

    return f;
  }

  public JsonObject toFavourites() {
    return Json.createObjectBuilder()
        .add("parkingId", parkingId)
        .add("image", image)
        .add("description", description)
        .add("rackType", rackType)
        .add("rackCount", rackCount)
        .add("sheltered", sheltered)
        .build();
  }
  
}
