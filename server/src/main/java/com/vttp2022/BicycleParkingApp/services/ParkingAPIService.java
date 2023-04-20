package com.vttp2022.BicycleParkingApp.services;

import java.util.Optional;

import org.slf4j.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.vttp2022.BicycleParkingApp.models.parking.Parkings;
import com.vttp2022.BicycleParkingApp.models.parking.Query;

@Service
public class ParkingAPIService {
  private static final Logger logger = LoggerFactory.getLogger(ParkingAPIService.class);

  private static String URL = "http://datamall2.mytransport.sg/ltaodataservice/BicycleParkingv2";

  public static Optional<Parkings> findParking(Query q){
    String apiKey = System.getenv("BICYCLE_PARKING_API_KEY");

    String parkingUrl = UriComponentsBuilder.fromUriString(URL)
      .queryParam("Lat", q.getLat())
      .queryParam("Long", q.getLng())
      .queryParam("Dist", q.getRadius())
      .toUriString();
    //logger.info(parkingUrl);
    RestTemplate template = new RestTemplate();
    ResponseEntity<String> resp = null;

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.set("AccountKey", apiKey);
      HttpEntity request = new HttpEntity(headers);

      resp = template.exchange(parkingUrl, HttpMethod.GET, request, String.class, 1);
      Parkings p = Parkings.createJson(resp.getBody());
      return Optional.of(p);
    } catch (Exception e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
    return Optional.empty();
  }
  
}
