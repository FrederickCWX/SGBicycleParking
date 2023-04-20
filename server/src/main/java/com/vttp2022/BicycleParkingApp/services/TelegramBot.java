package com.vttp2022.BicycleParkingApp.services;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vttp2022.BicycleParkingApp.models.parking.Parkings;
import com.vttp2022.BicycleParkingApp.models.parking.Query;
import com.vttp2022.BicycleParkingApp.models.parking.Value;
import com.vttp2022.BicycleParkingApp.models.postal.Postal;
import com.vttp2022.BicycleParkingApp.models.postal.Results;
import com.vttp2022.BicycleParkingApp.repositories.UserParkingRepository;
import com.vttp2022.BicycleParkingApp.utilities.SortByDistance;

@Service
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    @Override
    public void onUpdateReceived(Update update) {
        logger.info(String.format("(Telegram Bot) Message from %s >>> %s",update.getMessage().getFrom().getFirstName(),update.getMessage().getText()));
        
        String command = update.getMessage().getText();

        if(command.equals("/help")) {
            String message = "Enter a postal code to search for the nearest bicycle parking bays (in increments of 50metres) of the postal code!";
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        if(isPostal(command) == true) {
            String message = searchBP(command);

            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "BicycleParkingBot";
    }

    @Override
    public String getBotToken() {
        return "6143365163:AAEKyL6zfGPS_54MSEn8SfDV2gfi9udFv1U";
    }

    public boolean isPostal(String str) {
        if(str.length() != 6) return false;

        for(char c: str.toCharArray()){
            if(!Character.isDigit(c)) return false;
        }
        return true;
    }

    public String searchBP(String postal) {
        String message = "";
        Double km = 0.05;
        Integer metres = 50;
        while(true) {
            Query q = new Query();
            Optional<Postal> optPostal = PostalAPIService.getPostalDetails(Integer.valueOf(postal));

            if(Postal.getFound() == 0) {
                message = "Please enter a valid postal code";
                break;
            }
            List<Value> val = new LinkedList<>();

            List<Results> results = Postal.getResults();
            if(results.size() >= 1){
                q.setLat(results.get(0).getLatitude());
                q.setLng(results.get(0).getLongitude());
            }
            while(true){
                q.setRadius(km);
                Optional<Parkings> optParking = ParkingAPIService.findParking(q);
                // logger.info("Optional Parking - "+optParking.toString());
                // if(optParking.isEmpty()) {
                //     message = "There are no bicycle parking bays located within 150m of Singapore "+postal;
                //     break;
                // }

                Collections.sort(Parkings.getValue(), new SortByDistance());
                val = Parkings.getValue();
                if(val.size()>0) break;
                km = km+0.05;
                metres = metres+50;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("There are ");
            sb.append(val.size());
            sb.append(" bicycle parking bay(s) located within ");
            sb.append(metres);
            sb.append("m of Singapore ");
            sb.append(postal);
            sb.append(":");
            for(Value v: val) {
                sb.append("\n\nDescription: ");
                sb.append(v.getDescription());
                sb.append("\nRack Type: ");
                sb.append(v.getRackType());
                sb.append("\nRack Count: ");
                sb.append(v.getRackCount());
                sb.append("\nSheltered Bay: ");
                sb.append(v.getShelter());
            }
            message = sb.toString();
            break;
        }
        return message;
    }
}
