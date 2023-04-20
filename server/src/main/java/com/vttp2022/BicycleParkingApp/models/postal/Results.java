package com.vttp2022.BicycleParkingApp.models.postal;

import java.math.BigDecimal;

//import org.slf4j.*;

import com.vttp2022.BicycleParkingApp.utilities.CleanString;

import jakarta.json.JsonObject;
import jakarta.json.JsonString;

public class Results {
  //private static final Logger logger = LoggerFactory.getLogger(Results.class);

  private String searchVal;
  private BigDecimal latitude;
  private BigDecimal longitude;

  private String address;

  public String getSearchVal() {
    return searchVal;
  }
  public void setSearchVal(String searchVal) {
    this.searchVal = searchVal;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }
  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }
  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  public String getAddress(){
    return address;
  }
  public void setAddress(String address){
    this.address = address;
  }

  public static Results createJson(JsonObject jo){

    Results r = new Results();
    JsonString jsSearchVal = jo.getJsonString("SEARCHVAL");
    r.searchVal = jsSearchVal.getString();
    JsonString jsLatitude = jo.getJsonString("LATITUDE");
    r.latitude = new BigDecimal(jsLatitude.getString());
    JsonString jsLongitude = jo.getJsonString("LONGITUDE");
    r.longitude = new BigDecimal(jsLongitude.getString());
    JsonString jsBlkNumber = jo.getJsonString("BLK_NO");
    JsonString jsRoadName = jo.getJsonString("ROAD_NAME");
    r.address = jsBlkNumber.getString() + " " + CleanString.cleanString(jsRoadName.getString());
    return r;
  }
  
}
