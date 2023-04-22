package com.vttp2022.BicycleParkingApp.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;

@Service
public class BookingsAPIService {
  private static final Logger logger = LoggerFactory.getLogger(BookingsAPIService.class);

  private static String UserAPI = "https://sg-bicycle-parking.up.railway.app/api/email";

  private static String BookingAPI = "https://sg-bicycle-parking.up.railway.app/api/bookings";


  public static String getBookings(String email) {
    String response = "";
    String userUrl = UriComponentsBuilder.fromUriString(UserAPI)
        .toUriString();
    String bookingsUrl = UriComponentsBuilder.fromUriString(BookingAPI)
        .queryParam("email", email)
        .toUriString();

    RestTemplate template = new RestTemplate();
    ResponseEntity<String> resp = null;

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("email", email);
      HttpEntity request = new HttpEntity(headers);

      resp = template.exchange(userUrl, HttpMethod.GET, request, String.class, 1);
      String status = createJson(resp.getBody());
      if(status.equals("Invalid email")) response = status;
      else {
        try {
            resp = template.exchange(bookingsUrl, HttpMethod.GET, null, String.class, 1);
            logger.info(resp.getBody());
            return resp.getBody();
          } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
          }
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
    return response;
  }

  public static String createJson(String json) throws IOException {
    String str = "";
    try(InputStream in = new ByteArrayInputStream(json.getBytes())) {
      JsonReader jr = Json.createReader(in);
      JsonObject jo = jr.readObject();
      JsonString js = jo.getJsonString("status");
      str = js.getString();
    }
    return str;
  }
  
}
