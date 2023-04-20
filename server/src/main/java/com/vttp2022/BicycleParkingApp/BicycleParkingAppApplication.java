package com.vttp2022.BicycleParkingApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.vttp2022.BicycleParkingApp.services.TelegramBot;

@SpringBootApplication
public class BicycleParkingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BicycleParkingAppApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void telegramBot(){
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new TelegramBot());
		} catch (TelegramApiException e) {
				e.printStackTrace();
		}
	}
}
