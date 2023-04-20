package com.vttp2022.BicycleParkingApp.models.parking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Random;

//import org.slf4j.*;

import com.vttp2022.BicycleParkingApp.utilities.CalculateDistance;
import com.vttp2022.BicycleParkingApp.utilities.CleanString;

import jakarta.json.Json;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;

public class Value implements Serializable{
  //private static final Logger logger = LoggerFactory.getLogger(Value.class);

  private String id;
  private String img;
  private String description;
  private BigDecimal lat;
  private BigDecimal lng;
  private String rackType;
  private Integer availability;
  private Integer rackCount;
  private String shelter;

  private String distance;

  public Value(){
    this.id = generateId(8);
  }

  public String getId(){
    return id;
  }
  public void setId(String id){
    this.id = id;
  }

  public String getImg() {
    return img;
  }
  public void setImg(String img) {
    this.img = img;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getLat() {
    return lat;
  }
  public void setLat(BigDecimal lat) {
    this.lat = lat;
  }

  public BigDecimal getLng() {
    return lng;
  }
  public void setLng(BigDecimal lng) {
    this.lng = lng;
  }

  public String getRackType() {
    return rackType;
  }
  public void setRackType(String rackType) {
    this.rackType = rackType;
  }

  public Integer getAvailability() { return availability; }
  public void setAvailability(Integer availability) { this.availability = availability; }

  public Integer getRackCount() {
    return rackCount;
  }
  public void setRackCount(Integer rackCount) {
    this.rackCount = rackCount;
  }

  public String getShelter() {
    return shelter;
  }
  public void setShelter(String shelter) {
    this.shelter = shelter;
  }

  public String getDistance(){
    return distance;
  }
  public void setDistance(String distance){
    this.distance = distance;
  }

  private synchronized String generateId(int numchars) {
    Random r = new Random();
    StringBuilder strBuilder = new StringBuilder();
    while (strBuilder.length() < numchars) {
        strBuilder.append(Integer.toHexString(r.nextInt()));
    }
    return strBuilder.toString().substring(0, numchars);
}

  public static Value createJson(JsonObject jo){
    Value v = new Value();

    JsonString jsDesp = jo.getJsonString("Description");
    v.description = CleanString.cleanString(jsDesp.getString());
    JsonNumber jnLat = jo.getJsonNumber("Latitude");
    v.lat = jnLat.bigDecimalValue();
    JsonNumber jnLng = jo.getJsonNumber("Longitude");
    v.lng = jnLng.bigDecimalValue();
    JsonString jsType = jo.getJsonString("RackType");
    v.rackType = CleanString.cleanString(jsType.getString());
    JsonNumber jnCount = jo.getJsonNumber("RackCount");
    v.rackCount = jnCount.intValue();
    JsonString jsShelter = jo.getJsonString("ShelterIndicator");
    if(jsShelter.getString().equals("Y"))
      v.shelter = "Sheltered";
    else if(jsShelter.getString().equals("N"))
      v.shelter = "Unsheltered";
    else 
      v.shelter = jsShelter.getString();
    v.img = createImgURL(v.lat, v.lng);
    v.distance = CalculateDistance.getDistance(Query.getLat(), v.lat, Query.getLng(), v.lng);

    return v;
  }

  public static Value createFavJson(JsonObject jo){
    Value v = new Value();

    JsonString jsId = jo.getJsonString("id");
    v.id = jsId.getString();
    JsonString jsImg = jo.getJsonString("img");
    v.img = jsImg.getString();
    JsonString jsDesp = jo.getJsonString("description");
    v.description = jsDesp.getString();
    JsonNumber jnLat = jo.getJsonNumber("lat");
    v.lat = jnLat.bigDecimalValue();
    JsonNumber jnLng = jo.getJsonNumber("lng");
    v.lng = jnLng.bigDecimalValue();
    JsonString jsType = jo.getJsonString("rackType");
    v.rackType = jsType.getString();
    JsonNumber jnCount = jo.getJsonNumber("rackCount");
    v.rackCount = jnCount.intValue();
    JsonString jsShelter = jo.getJsonString("shelter");
    v.shelter = jsShelter.getString();

    return v;
  }

  public static String createImgURL(BigDecimal lat, BigDecimal lng){

    StringBuilder sb = new StringBuilder();
    sb.append("https://developers.onemap.sg/commonapi/staticmap/getStaticImage?layerchosen=default&lat=");
    sb.append(String.valueOf(lat));
    sb.append("&lng=");
    sb.append(String.valueOf(lng));
    sb.append("&zoom=17&height=350&width=500&polygons=&lines=&points=[");
    sb.append(String.valueOf(lat));
    sb.append(",");
    sb.append(String.valueOf(lng));
    sb.append(",\"255,0,0\",\"\"]&color=&fillColor=");
    String url = sb.toString();

    return url;
  }

  public JsonObject toJson(){
    JsonObjectBuilder joBuilder = Json.createObjectBuilder();

    joBuilder.add("id", this.id)
        .add("img", this.img)
        .add("description", this.description)
        .add("lat", this.lat)
        .add("lng", this.lng)
        .add("rackType", this.rackType)
        .add("rackCount", this.rackCount)
        .add("shelter", this.shelter);

    return joBuilder.build();
  }

  public JsonObject toResults() {
    return Json.createObjectBuilder()
    .add("parkingId", id)
    .add("image", img)
    .add("description", description)
    .add("distance", distance)
    .add("lat", lat)
    .add("lng", lng)
    .add("rackType", rackType)
    .add("rackCount", rackCount)
    .add("availability", availability)
    .add("sheltered", shelter)
    .build();
        
  }
  
}
