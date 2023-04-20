package com.vttp2022.BicycleParkingApp.utilities;

import java.util.List;
import java.util.Optional;

import com.vttp2022.BicycleParkingApp.models.postal.Postal;
import com.vttp2022.BicycleParkingApp.models.postal.Results;
import com.vttp2022.BicycleParkingApp.services.PostalAPIService;

public class CleanString {

  public static String cleanString(String string){
    
    if(string.contains("-")){
      String[] postal = string.split("-");
      int postalCode = Integer.valueOf(postal[0]);
      
      Optional<Postal> optPostal = PostalAPIService.getPostalDetails(postalCode);

      if(optPostal.isEmpty()){
        string = postal[0]+"-"+postal[1];
      }

      List<Results> results = Postal.getResults();
      if(results.size() >= 1){
        StringBuilder sb = new StringBuilder();
        sb.append(results.get(0).getAddress());
        sb.append(" - Rack ");
        sb.append(postal[1]);

        string =  sb.toString();
      }
    }

    String wordList[] = string.split("\\s");  
    String cleanDescription = "";  
    for(String word: wordList){  
        String firstLetter = word.substring(0,1);  
        String nextLetters = word.substring(1);  
        cleanDescription += firstLetter + nextLetters.toLowerCase() + " ";  
    }

    if(cleanDescription.contains("_yb")){
      cleanDescription = cleanDescription.replace("_yb", " (Yellow Box)");
    }

    if(cleanDescription.contains("Hdb_racks")){
      cleanDescription = cleanDescription.replace("Hdb_racks", "HDB Racks");
    }

    return cleanDescription.trim();  
  }  

  public static String cleanName(String name) {
    String[] individual = name.split("\\s");
    StringBuilder sb = new StringBuilder();
    for (String i : individual) {
        if (!i.isEmpty()) {
            sb.append(Character.toUpperCase(i.charAt(0)));
            sb.append(i.substring(1));
            sb.append(" ");
        }
    }
    return sb.toString().trim();
}
  
}
