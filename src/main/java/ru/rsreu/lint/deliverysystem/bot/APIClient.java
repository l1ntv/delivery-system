package ru.rsreu.lint.deliverysystem.bot;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.rsreu.lint.deliverysystem.web.dto.AssignCourierDTO;
import ru.rsreu.lint.deliverysystem.web.dto.OrderDTO;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;

import java.util.Arrays;
import java.util.List;

public class APIClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String baseUrl;

    public APIClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<UserDTO> getAllClients() throws Exception {
        try {
            return Arrays.asList(restTemplate.getForObject(baseUrl + "/clients", UserDTO[].class));
        } catch (Exception e) {
            handleException(e, "Ошибка при получении клиентов");
            return List.of();
        }
    }

    public UserDTO getClientById(Long id) throws Exception {
        String url = baseUrl + "/clients/{id}";
        try {
            return restTemplate.getForObject(url, UserDTO.class, id);
        } catch (Exception e) {
            handleException(e, "Ошибка при получении клиента с ID = " + id);
            return null;
        }
    }

    public UserDTO createClient(UserDTO userDTO) throws Exception {
        try {
            return restTemplate.postForObject(baseUrl + "/clients", userDTO, UserDTO.class);
        } catch (Exception e) {
            handleException(e, "Ошибка при создании клиента");
            return null;
        }
    }

    public List<UserDTO> getAllCouriers() throws Exception {
        try {
            return Arrays.asList(restTemplate.getForObject(baseUrl + "/couriers", UserDTO[].class));
        } catch (Exception e) {
            handleException(e, "Ошибка при получении курьеров");
            return List.of();
        }
    }

    public UserDTO createCourier(UserDTO userDTO) throws Exception {
        try {
            return restTemplate.postForObject(baseUrl + "/couriers", userDTO, UserDTO.class);
        } catch (Exception e) {
            handleException(e, "Ошибка при создании курьера");
            return null;
        }
    }

    public List<OrderDTO> getAllOrders() throws Exception {
        try {
            return Arrays.asList(restTemplate.getForObject(baseUrl + "/orders", OrderDTO[].class));
        } catch (Exception e) {
            handleException(e, "Ошибка при получении заказов");
            return List.of();
        }
    }

    public OrderDTO createOrder(OrderDTO orderDTO) throws Exception {
        try {
            return restTemplate.postForObject(baseUrl + "/orders", orderDTO, OrderDTO.class);
        } catch (Exception e) {
            handleException(e, "Ошибка при создании заказа");
            return null;
        }
    }

    public List<OrderDTO> getCourierOrders(Long courierId) throws Exception {
        try {
            return Arrays.asList(restTemplate.getForObject(baseUrl + "/orders/courier/{id}", OrderDTO[].class, courierId));
        } catch (Exception e) {
            handleException(e, "Ошибка при получении заказов курьера");
            return List.of();
        }
    }

    public OrderDTO assignOrder(Long orderId, Long courierId) throws Exception {
        AssignCourierDTO dto = new AssignCourierDTO();
        dto.setId(courierId);

        try {
            HttpEntity<AssignCourierDTO> request = new HttpEntity<>(dto);
            ResponseEntity<OrderDTO> response = restTemplate.exchange(
                    baseUrl + "/orders/{id}/assign",
                    HttpMethod.PUT,
                    request,
                    OrderDTO.class,
                    orderId
            );

            return response.getBody();

        } catch (Exception e) {
            handleException(e, "Ошибка при назначении курьера на заказ");
            return null;
        }
    }

    public OrderDTO updateOrderStatus(Long orderId) throws Exception {
        String url = baseUrl + "/orders/{id}/status";

        try {
            HttpEntity<Void> entity = new HttpEntity<>(null);
            ResponseEntity<OrderDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    OrderDTO.class,
                    orderId
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
            throw new Exception();
        } catch (Exception e) {
            handleException(e, "Ошибка при изменении статуса заказа");
            return null;
        }
    }

    private void handleException(Exception e, String defaultMessage) throws Exception {
        throw new Exception(defaultMessage + ": " + e.getMessage());
    }
}
