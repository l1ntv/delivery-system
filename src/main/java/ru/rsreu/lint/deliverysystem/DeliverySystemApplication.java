package ru.rsreu.lint.deliverysystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.rsreu.lint.deliverysystem.bot.DeliverySystemTelegramBot;

@SpringBootApplication
public class DeliverySystemApplication {

    public static void main(String[] args) {

        SpringApplication.run(DeliverySystemApplication.class, args);
        new Thread(() -> {
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new DeliverySystemTelegramBot());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
