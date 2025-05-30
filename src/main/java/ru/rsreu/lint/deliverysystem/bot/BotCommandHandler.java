package ru.rsreu.lint.deliverysystem.bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.rsreu.lint.deliverysystem.web.dto.OrderDTO;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;

import java.util.List;

public class BotCommandHandler {

    private final APIClient apiClient;
    private final DeliverySystemTelegramBot bot;

    public BotCommandHandler(APIClient apiClient, DeliverySystemTelegramBot bot) {
        this.apiClient = apiClient;
        this.bot = bot;
    }

    public void handleCommand(Update update, String command, Long chatId) {
        switch (command) {
            case "/start":
                bot.sendText(chatId, "Добро пожаловать в Delivery System Bot!");
                sendMenu(chatId);
                break;

            case "/clients":
                try {
                    List<UserDTO> clients = apiClient.getAllClients();
                    bot.sendText(chatId, formatClients(clients));
                } catch (Exception e) {
                    bot.sendText(chatId, "Ошибка: " + e.getMessage());
                }
                break;

            case "/clientinfo":
                bot.sendText(chatId, "Введите ID клиента:");
                bot.getUserInputs().put(chatId, new InputState());
                bot.getUserInputs().get(chatId).setState(StatesEnum.CLIENT_INFO);
                break;

            case "/createclient":
                bot.sendText(chatId, "Введите логин клиента:");
                bot.getUserInputs().put(chatId, new InputState());
                bot.getUserInputs().get(chatId).setState(StatesEnum.CREATING_CLIENT);
                break;

            case "/couriers":
                try {
                    List<UserDTO> couriers = apiClient.getAllCouriers();
                    bot.sendText(chatId, formatCouriers(couriers));
                } catch (Exception e) {
                    bot.sendText(chatId, "Ошибка: " + e.getMessage());
                }
                break;

            case "/createcourier":
                bot.sendText(chatId, "Введите логин курьера:");
                bot.getUserInputs().put(chatId, new InputState());
                bot.getUserInputs().get(chatId).setState(StatesEnum.CREATING_COURIER);
                break;

            case "/orders":
                try {
                    List<OrderDTO> orders = apiClient.getAllOrders();
                    bot.sendText(chatId, formatOrders(orders));
                } catch (Exception e) {
                    bot.sendText(chatId, "Ошибка: " + e.getMessage());
                }
                break;

            case "/createorder":
                bot.sendText(chatId, "Введите ID клиента:");
                bot.getUserInputs().put(chatId, new InputState());
                bot.getUserInputs().get(chatId).setState(StatesEnum.CREATING_ORDER_LOGIN);
                break;

            case "/courierorders":
                bot.sendText(chatId, "Введите ID курьера:");
                bot.getUserInputs().put(chatId, new InputState());
                bot.getUserInputs().get(chatId).setState(StatesEnum.GETTING_COURIER_ORDERS);
                break;

            case "/assignorder":
                bot.sendText(chatId, "Введите ID заказа:");
                bot.getUserInputs().put(chatId, new InputState());
                bot.getUserInputs().get(chatId).setState(StatesEnum.ASSIGNING_COURIER_ID);
                break;

            default:
                bot.sendText(chatId, "Неизвестная команда.");
                sendMenu(chatId);
                break;
        }
    }

    public void handleInput(Update update, Long chatId) {
        InputState state = bot.getUserInputs().get(chatId);
        if (state == null) return;

        String text = update.getMessage().getText();

        try {
            switch (state.getState()) {
                case CREATING_CLIENT:
                    state.put("login", text);
                    bot.sendText(chatId, "Укажите роль (CLIENT / COURIER):");
                    state.setState(StatesEnum.CREATING_CLIENT_ROLE);
                    break;

                case CREATING_CLIENT_ROLE:
                    ru.rsreu.lint.deliverysystem.model.enums.UserRole role = ru.rsreu.lint.deliverysystem.model.enums.UserRole.valueOf(text.toUpperCase());
                    state.put("role", role);
                    UserDTO createdClient = apiClient.createClient(state.buildUserDTO());
                    if (createdClient != null) {
                        bot.sendText(chatId, "Клиент создан:\n" + createdClient);
                    } else {
                        bot.sendText(chatId, "Ошибка при создании клиента.");
                    }
                    bot.getUserInputs().remove(chatId);
                    break;

                case CREATING_COURIER:
                    state.put("login", text);
                    bot.sendText(chatId, "Укажите роль (CLIENT / COURIER):");
                    state.setState(StatesEnum.CREATING_COURIER_ROLE);
                    break;

                case CREATING_COURIER_ROLE:
                    ru.rsreu.lint.deliverysystem.model.enums.UserRole courierRole = ru.rsreu.lint.deliverysystem.model.enums.UserRole.valueOf(text.toUpperCase());
                    state.put("role", courierRole);
                    UserDTO createdCourier = apiClient.createCourier(state.buildUserDTO());
                    if (createdCourier != null) {
                        bot.sendText(chatId, "Курьер создан:\n" + createdCourier);
                    } else {
                        bot.sendText(chatId, "Ошибка при создании курьера.");
                    }
                    bot.getUserInputs().remove(chatId);
                    break;

                case CREATING_ORDER_LOGIN:
                    state.put("clientId", Long.parseLong(text));
                    bot.sendText(chatId, "Введите описание:");
                    state.setState(StatesEnum.CREATING_ORDER_DESCRIPTION);
                    break;

                case CREATING_ORDER_DESCRIPTION:
                    state.put("description", text);
                    bot.sendText(chatId, "Введите улицу:");
                    state.setState(StatesEnum.CREATING_ORDER_STREET);
                    break;

                case CREATING_ORDER_STREET:
                    state.put("street", text);
                    bot.sendText(chatId, "Введите город:");
                    state.setState(StatesEnum.CREATING_ORDER_CITY);
                    break;

                case CREATING_ORDER_CITY:
                    state.put("city", text);
                    bot.sendText(chatId, "Введите индекс:");
                    state.setState(StatesEnum.CREATING_ORDER_POSTAL_CODE);
                    break;

                case CREATING_ORDER_POSTAL_CODE:
                    state.put("postalCode", text);
                    bot.sendText(chatId, "Введите страну:");
                    state.setState(StatesEnum.CREATING_ORDER_COUNTRY);
                    break;

                case CREATING_ORDER_COUNTRY:
                    state.put("country", text);
                    OrderDTO createdOrder = apiClient.createOrder(state.buildOrderDTO());
                    if (createdOrder != null) {
                        bot.sendText(chatId, "Заказ создан:\n" + createdOrder);
                    } else {
                        bot.sendText(chatId, "Ошибка при создании заказа.");
                    }
                    bot.getUserInputs().remove(chatId);
                    break;

                case ASSIGNING_COURIER_ID:
                    Long orderId = Long.parseLong(text);
                    bot.sendText(chatId, "Введите ID курьера:");
                    state.put("orderId", orderId);
                    state.setState(StatesEnum.ASSIGNING_COURIER_ID_ENTERED);
                    break;

                case ASSIGNING_COURIER_ID_ENTERED:
                    Long courierId = Long.parseLong(text);
                    OrderDTO assignedOrder = apiClient.assignOrder((Long) state.get("orderId"), courierId);
                    if (assignedOrder != null) {
                        bot.sendText(chatId, "Заказ назначен:\n" + assignedOrder);
                    } else {
                        bot.sendText(chatId, "Ошибка при назначении курьера на заказ.");
                    }
                    bot.getUserInputs().remove(chatId);
                    break;

                case CLIENT_INFO:
                    Long clientId = Long.parseLong(text);
                    UserDTO client = apiClient.getClientById(clientId);
                    if (client != null) {
                        bot.sendText(chatId, client.toString());
                    } else {
                        bot.sendText(chatId, "Клиент с ID " + clientId + " не найден.");
                    }
                    bot.getUserInputs().remove(chatId);
                    break;

                case GETTING_COURIER_ORDERS:
                    Long courierIdForOrders = Long.parseLong(text);
                    List<OrderDTO> orders = apiClient.getCourierOrders(courierIdForOrders);
                    if (orders != null && !orders.isEmpty()) {
                        bot.sendText(chatId, formatOrders(orders));
                    } else {
                        bot.sendText(chatId, "У курьера с ID " + courierIdForOrders + " нет заказов.");
                    }
                    bot.getUserInputs().remove(chatId);
                    break;
            }
        } catch (Exception e) {
            bot.sendText(chatId, "Ошибка: " + e.getMessage());
            bot.getUserInputs().remove(chatId);
        }
    }

    private void sendMenu(Long chatId) {
        StringBuilder menu = new StringBuilder("Доступные команды:\n");
        menu.append("/clients — все клиенты\n");
        menu.append("/clientinfo — информация о клиенте по ID\n");
        menu.append("/createclient — создать клиента\n");
        menu.append("/couriers — все курьеры\n");
        menu.append("/createcourier — создать курьера\n");
        menu.append("/orders — все заказы\n");
        menu.append("/createorder — создать заказ\n");
        menu.append("/courierorders — заказы курьера\n");
        menu.append("/assignorder — назначить курьера на заказ\n");

        bot.sendText(chatId, menu.toString());
    }

    private String formatClients(List<UserDTO> clients) {
        if (clients == null || clients.isEmpty()) {
            return "Список клиентов пуст.";
        }

        StringBuilder sb = new StringBuilder("Список клиентов:\n");
        for (UserDTO c : clients) {
            sb.append("- ").append(c).append("\n");
        }
        return sb.toString();
    }

    private String formatCouriers(List<UserDTO> couriers) {
        if (couriers == null || couriers.isEmpty()) {
            return "Список курьеров пуст.";
        }

        StringBuilder sb = new StringBuilder("Список курьеров:\n");
        for (UserDTO c : couriers) {
            sb.append("- ").append(c).append("\n");
        }
        return sb.toString();
    }

    private String formatOrders(List<OrderDTO> orders) {
        if (orders == null || orders.isEmpty()) {
            return "Список заказов пуст.";
        }

        StringBuilder sb = new StringBuilder("Список заказов:\n");
        for (OrderDTO o : orders) {
            sb.append("- ").append(o).append("\n");
        }
        return sb.toString();
    }
}
