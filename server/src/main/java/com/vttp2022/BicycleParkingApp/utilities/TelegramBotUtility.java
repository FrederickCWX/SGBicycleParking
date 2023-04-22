package com.vttp2022.BicycleParkingApp.utilities;

import java.util.List;

import org.slf4j.*;

import com.vttp2022.BicycleParkingApp.models.parking.Value;

public class TelegramBotUtility {

  private static final Logger logger = LoggerFactory.getLogger(TelegramBotUtility.class);

  public boolean checkEmailExist(String email) {
    boolean emailFound = false;

    logger.info(email+" found >>> "+emailFound);
    return emailFound;
  }

  public String reservationMsg(List<Value> reservations) {

    if(reservations.size() == 0)
      return "You have no bicycle parking reservation";

    StringBuilder sb = new StringBuilder();
    sb.append("You have ");
    sb.append(reservations.size());
    sb.append("bicycle parking reservation(s).\n");

    for(int i=0; i<reservations.size(); i++){
      sb.append("\n Date/Time: ");
      sb.append("\nDescription: ");
      sb.append(reservations.get(i).getDescription());
      sb.append("\nRack Type: ");
      sb.append(reservations.get(i).getRackType());
      sb.append("\nSheltered Bay: ");
      sb.append(reservations.get(i).getShelter());
      sb.append("\n");
    }

    String message = sb.toString();

    return message;
  }

  
}
