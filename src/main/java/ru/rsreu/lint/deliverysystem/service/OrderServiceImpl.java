package ru.rsreu.lint.deliverysystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rsreu.lint.deliverysystem.model.Order;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.OrderStatus;
import ru.rsreu.lint.deliverysystem.model.exception.OrderNotFoundException;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.model.exception.UserNotFoundException;
import ru.rsreu.lint.deliverysystem.repository.OrderRepository;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private static final int MAX_ORDERS_NUMBER = 3;

    @Override
    @Transactional
    public Order create(Order order) {
        order.setStatus(OrderStatus.CREATED);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findCourierOrders(Long courierId) {
        userRepository.findById(courierId)
                .orElseThrow(() -> new UserNotFoundException("Courier not found."));
        return orderRepository.findAllByCourier_Id(courierId);
    }

    @Override
    @Transactional
    public Order assignOrder(Long orderId, Long courierId) {
        User courier = userRepository.findById(courierId)
                .orElseThrow(() -> new UserNotFoundException("Courier not found."));
        List<Order> currentOrders = orderRepository.findAllByCourierAndStatus(courier, OrderStatus.IN_PROGRESS);
        if (isNumberOrdersCorrect(currentOrders.size())) {
            throw new ResourceConflictException("Incorrect number of orders.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found."));
        if (order.getCourier() != null) {
            throw new ResourceConflictException("Order is already assigned to courier.");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setCourier(courier);
        orderRepository.save(order);
        return order;
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new UserNotFoundException("Order not found."));

        OrderStatus actualStatus = order.getStatus();
        OrderStatus newStatus = actualStatus.equals(OrderStatus.CREATED)
                ? OrderStatus.IN_PROGRESS
                : OrderStatus.DELIVERED;

        order.setStatus(newStatus);
        orderRepository.save(order);
        return order;
    }

    private boolean isNumberOrdersCorrect(int numberOrders) {
        return numberOrders >= OrderServiceImpl.MAX_ORDERS_NUMBER;
    }
}
