package com.vttp2022.BicycleParkingApp.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;

@Service
public class WebNotificationService {

  private static final Logger logger = LoggerFactory.getLogger(WebNotificationService.class);

  private static String URL = "https://fcm.googleapis.com/fcm/send";

  public void sendNotification(String token, String service) throws Exception{

    String authorization = System.getenv("FIREBASE_NOTIFICATION_AUTHORIZATION");

    RestTemplate template = new RestTemplate();
    ResponseEntity<String> response = null;

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("Authorization", authorization);

      JsonObject jsonBody = Json.createObjectBuilder().build();
      
      if(service == "favourites") jsonBody = favouritesJO(token);
      else if(service == "bookings") jsonBody = bookingsJO(token);

      StringWriter stringWriter = new StringWriter();
      JsonWriter jsonWriter = Json.createWriter(stringWriter);
      jsonWriter.writeObject(jsonBody);

      String msg = stringWriter.toString();

      HttpEntity request = new HttpEntity<>(msg, headers);

      response = template.exchange(URL, HttpMethod.POST, request, String.class, 1);

      logger.info("Notification Status >>> "+checkSuccess(response.getBody()));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public JsonObject favouritesJO(String token){
    Map<String, String> config = new HashMap<String, String>();

    JsonBuilderFactory factory = Json.createBuilderFactory(config);

    JsonObject jsonBody = factory.createObjectBuilder()
          .add("notification", factory.createObjectBuilder()
              .add("title", "SG Bicycle Parking")
              .add("body", "Location saved successfully to favourites"))
          .add("to", token)
          .build();
    
    return jsonBody;
  }

  public JsonObject bookingsJO(String token){
    Map<String, String> config = new HashMap<String, String>();

    JsonBuilderFactory factory = Json.createBuilderFactory(config);

    JsonObject jsonBody = factory.createObjectBuilder()
          .add("notification", factory.createObjectBuilder()
              .add("title", "SG Bicycle Parking")
              .add("body", "Your booking has been confirmed. An email will be sent to you shortly"))
          .add("to", token)
          .build();
    
    return jsonBody;
  }

  public static String checkSuccess(String json) throws IOException {

    try(InputStream in = new ByteArrayInputStream(json.getBytes())) {
      JsonReader jr = Json.createReader(in);
      JsonObject jo = jr.readObject();

      Integer success = jo.getJsonNumber("success").intValue();
      Integer failure = jo.getJsonNumber("failure").intValue();

      if(success == 1)
        return "success";
      else if(failure == 1)
        return "fail";
    }

    return "fail";
  }
  
}
