package com.vttp2022.BicycleParkingApp.models.parking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.*;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Parkings {
  private static final Logger logger = LoggerFactory.getLogger(Parkings.class);

  private String metadata;
  private static List<Value> details = new ArrayList<>();

  private static String info;

  public String getMetadata(){
    return metadata;
  }
  public void setMetadata(String metadata){
    this.metadata = metadata;
  }

  public static List<Value> getValue() {
    return details;
  }
  public void setValue(List<Value> details) {
    Parkings.details = details;
  }

  public static String getInfo(){
    return info;
  }
  public static void setInfo(String stringInfo){
    info = stringInfo;
  }

  public static Parkings createJson(String json) throws IOException{
    Parkings p = new Parkings();

    try(InputStream in = new ByteArrayInputStream(json.getBytes())){
      JsonReader jr = Json.createReader(in);
      JsonObject jo = jr.readObject();
      JsonArray ja = jo.getJsonArray("value");

      if(ja != null){
        List<Value> requested = new ArrayList<>();
        for(Object jv: ja){
          JsonObject joValue = (JsonObject) jv;
          requested.add(Value.createJson(joValue));
        }

        Parkings.details = requested;
      }
    }
    return p;
  }
  
}
