package ru.rsreu.lint.deliverysystem.bot;

import ru.rsreu.lint.deliverysystem.web.dto.OrderDTO;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;

import java.util.HashMap;
import java.util.Map;

public class InputState {

    private StatesEnum currentState = StatesEnum.NONE;

    private final Map<String, Object> data = new HashMap<>();

    public void setState(StatesEnum state) {
        this.currentState = state;
    }

    public StatesEnum getState() {
        return currentState;
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public UserDTO buildUserDTO() {
        return new UserDTO(
                (String) data.get("login"),
                (ru.rsreu.lint.deliverysystem.model.enums.UserRole) data.get("role")
        );
    }

    public OrderDTO buildOrderDTO() {
        return OrderDTO.builder()
                .description((String) data.get("description"))
                .street((String) data.get("street"))
                .city((String) data.get("city"))
                .postalCode((String) data.get("postalCode"))
                .country((String) data.get("country"))
                .clientId((Long) data.get("clientId"))
                .build();
    }
}
