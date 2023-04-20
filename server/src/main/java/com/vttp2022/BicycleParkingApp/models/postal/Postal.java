package com.vttp2022.BicycleParkingApp.models.postal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.*;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Postal {
  private static final Logger logger = LoggerFactory.getLogger(Postal.class);

  private static int found;
  private static List<Results> results = new ArrayList<>();

  public static int getFound() {
    return found;
  }
  public void setFound(int found) {
    this.found = found;
  }

  public static List<Results> getResults() {
    return results;
  }
  public static void setResults(List<Results> results) {
    Postal.results = results;
  }

  public static Postal createJson(String json) throws IOException{
    Postal p = new Postal();

    try(InputStream in = new ByteArrayInputStream(json.getBytes())){

      JsonReader jr = Json.createReader(in);
      JsonObject jo = jr.readObject();

      JsonNumber jnFound = jo.getJsonNumber("found");
      p.found = jnFound.intValue();

      JsonArray ja = jo.getJsonArray("results");

      if(ja != null){
        List<Results> results = new ArrayList<>();
        
        for(Object jv: ja){
          JsonObject joValue = (JsonObject) jv;
          results.add(Results.createJson(joValue));
        }
        Postal.results = results;
      }
    }
    return p;
  }
  
}
