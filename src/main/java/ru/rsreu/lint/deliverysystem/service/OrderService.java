package ru.rsreu.lint.deliverysystem.service;

import ru.rsreu.lint.deliverysystem.web.dto.OrderDTO;

import java.util.List;

public interface OrderService {

    OrderDTO create(OrderDTO order);

    List<OrderDTO> findAll();

    List<OrderDTO> findCourierOrders(Long courierId);

    OrderDTO assignOrder(Long orderId, Long courierId);

    OrderDTO updateOrderStatus(Long orderId);
}
