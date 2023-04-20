package com.vttp2022.BicycleParkingApp.utilities;

import java.util.Comparator;

import com.vttp2022.BicycleParkingApp.models.parking.Value;

public class SortByDistance implements Comparator<Value>{

  public int compare(Value a, Value b){

    if (Double.parseDouble(a.getDistance()) < Double.parseDouble(b.getDistance())) return -1;
    if (Double.parseDouble(a.getDistance()) > Double.parseDouble(b.getDistance())) return 1;
    
    return 0;
  }
  
}
