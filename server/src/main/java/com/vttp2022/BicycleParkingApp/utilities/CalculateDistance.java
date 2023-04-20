package com.vttp2022.BicycleParkingApp.utilities;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CalculateDistance {

  private static final DecimalFormat df = new DecimalFormat("#.##");

  public static String getDistance(BigDecimal latitude1, BigDecimal latitude2, BigDecimal longitude1, BigDecimal longitude2){
    
    double earthRadius = 6371;
    
    double lat1 = Math.toRadians(latitude1.doubleValue());
    double lat2 = Math.toRadians(latitude2.doubleValue());
    double lng1 = Math.toRadians(longitude1.doubleValue());
    double lng2 = Math.toRadians(longitude2.doubleValue());

    double dLat = lat2 - lat1;
    double dLng = lng2 - lng1;

    double x = Math.pow(Math.sin(dLat / 2), 2)
        + Math.cos(lat1) * Math.cos(lat2)
        * Math.pow(Math.sin(dLng / 2), 2);

    double y = 2 * Math.asin(Math.sqrt(x));

    double distanceKm = y * earthRadius;
    double distanceMetre = distanceKm * 1000;

    return df.format(distanceMetre);
    
  }
  
}
