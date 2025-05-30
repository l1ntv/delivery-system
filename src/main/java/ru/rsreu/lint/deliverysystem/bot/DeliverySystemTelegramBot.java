package ru.rsreu.lint.deliverysystem.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

public class DeliverySystemTelegramBot extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = System.getenv("TOKEN"); // замените на свой токен
    private static final String BASE_URL = "http://localhost:8080"; // или ngrok URL

    private final APIClient apiClient = new APIClient(BASE_URL);
    private final BotCommandHandler commandHandler = new BotCommandHandler(apiClient, this);
    private final Map<Long, InputState> userInputs = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            if (messageText.startsWith("/")) {
                commandHandler.handleCommand(update, messageText, chatId);
            } else {
                commandHandler.handleInput(update, chatId);
            }
        }
    }

    public void sendText(Long chatId, String text) {
        executeSilently(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build());
    }

    private void executeSilently(SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "delivery-system";
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public Map<Long, InputState> getUserInputs() {
        return userInputs;
    }
}