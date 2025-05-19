package ru.rsreu.lint.deliverysystem.service;

import ru.rsreu.lint.deliverysystem.model.Order;

import java.util.List;

public interface OrderService {

    Order create(Order order);

    List<Order> findAll();

    List<Order> findCourierOrders(Long courierId);

    Order assignOrder(Long orderId, Long courierId);

    Order updateOrderStatus(Long orderId);
}
