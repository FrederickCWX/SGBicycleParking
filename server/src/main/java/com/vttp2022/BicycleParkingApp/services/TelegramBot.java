package com.vttp2022.BicycleParkingApp.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vttp2022.BicycleParkingApp.models.mysql.Bookings;
import com.vttp2022.BicycleParkingApp.models.parking.Parkings;
import com.vttp2022.BicycleParkingApp.models.parking.Query;
import com.vttp2022.BicycleParkingApp.models.parking.Value;
import com.vttp2022.BicycleParkingApp.models.postal.Postal;
import com.vttp2022.BicycleParkingApp.models.postal.Results;
import com.vttp2022.BicycleParkingApp.utilities.SortByDistance;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class TelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    @Override
    public void onUpdateReceived(Update update) {
        logger.info(String.format("(Telegram Bot) Message from %s >>> %s",update.getMessage().getFrom().getFirstName(),update.getMessage().getText()));

        //String username = update.getMessage().getFrom().getFirstName();
        
        String command = update.getMessage().getText().toLowerCase();

        if(command.equals("/start")) {
            String message = "Welcome to SG Bicycle Parking Telegram Bot. \n\nEnter a postal code to search for the nearest bicycle parking bays (in increments of 50metres) of the postal code using the following command format:\nsearch<space>*Postal Code*\n\nOR\n\nEnter your email address to view your current bookings using the following command format:\nbooking<space>*Email*";

            sendMessage(message, update);
            
        }else if(command.equals("/help")) {
            String message = "Search for the nearest bicycle parking bays:\nsearch<space>*Postal Code*\n\nOR\n\nView your current bookings:\nbooking<space>*Email*";

            sendMessage(message, update);

        }else if(command.startsWith("search")) {
            String message = "";
            String postal = "";
            String[] split = command.split(" ");
            if(split.length != 2) message = "Invalid search format, use /help to learn how to use SG Bicycle Parking Bot.";
            else postal = split[1];
            
            if(postal != "") {
                if(isPostal(postal) == false) message = "Invalid postal, use /help to learn how to use SG Bicycle Parking Bot.";
                else if(isPostal(postal) == true) message = searchBP(postal);
            }

            sendMessage(message, update);

        }else if(command.startsWith("booking")) {
            String message = "";
            String email = "";
            String[] split = command.split(" ");

            if(split.length != 2) message = "Invalid booking format, use /help to learn how to use SG Bicycle Parking Bot.";
            else email = split[1];

            if(email != "") {
                if(!email.contains("@")) message = "Invalid email, use /help to learn how to use SG Bicycle Parking Bot.";
                else message = searchBookings(email);
            }

            sendMessage(message, update);

        }else {
            String message = "Invalid reply, use /help to learn how to use SG Bicycle Parking Bot.";

            sendMessage(message, update);
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

    public void sendMessage(String message, Update update) {
        SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
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

    public String searchBookings(String email){
        String message = "";
        String response = BookingsAPIService.getBookings(email);
        if(response.equals("Invalid email")) return response;

        try {
            List<Bookings> bList = new ArrayList<>();

            JsonReader jr = Json.createReader(new StringReader(response));
            JsonArray ja = jr.readArray();

            if(ja != null) {
                for(Object jv: ja){
                    JsonObject jo = (JsonObject) jv;
                    bList.add(Bookings.createTeleJson(jo));
                }
            }else return "You have no bookings";

            StringBuilder sb = new StringBuilder();
            sb.append("You have ");
            sb.append(bList.size());
            sb.append(" bicycle parking bay booking(s):");
            for(Bookings b: bList) {
                sb.append("\n\nDate: ");
                sb.append(b.getBookingDate());
                sb.append("\nDescription: ");
                sb.append(b.getDescription());
                sb.append("\nRack Type: ");
                sb.append(b.getRackType());
                sb.append("\nRack Count: ");
                sb.append(b.getRackCount());
                sb.append("\nSheltered Bay: ");
                sb.append(b.getSheltered());
            }
            message = sb.toString();
        } catch(Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return message;
    }
}
