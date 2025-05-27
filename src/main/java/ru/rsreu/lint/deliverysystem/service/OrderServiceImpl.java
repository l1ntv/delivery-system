package ru.rsreu.lint.deliverysystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rsreu.lint.deliverysystem.aop.Loggable;
import ru.rsreu.lint.deliverysystem.model.Order;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.OrderStatus;
import ru.rsreu.lint.deliverysystem.model.exception.OrderNotFoundException;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.model.exception.UserNotFoundException;
import ru.rsreu.lint.deliverysystem.repository.OrderRepository;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;
import ru.rsreu.lint.deliverysystem.web.dto.OrderDTO;
import ru.rsreu.lint.deliverysystem.web.mapper.OrderMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final OrderMapper orderMapper;

    @Value("${settings.max_orders_number}")
    private int MAX_ORDERS_NUMBER;

    @Override
    @Loggable
    @Transactional
    public OrderDTO create(OrderDTO dto) {
        Order order = orderMapper.toEntity(dto);
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    public List<OrderDTO> findAll() {
        return orderMapper.toDtoList(orderRepository.findAll());
    }

    @Override
    @Loggable
    public List<OrderDTO> findCourierOrders(Long courierId) {
        if (!userRepository.existsById(courierId)) {
            throw new UserNotFoundException("Courier not found.");
        }
        return orderMapper.toDtoList(orderRepository.findAllByCourier_Id(courierId));
    }

    @Override
    @Loggable
    @Transactional
    public OrderDTO assignOrder(Long orderId, Long courierId) {
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
        return orderMapper.toDto(order);
    }

    @Override
    @Loggable
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found."));

        OrderStatus actualStatus = order.getStatus();
        OrderStatus newStatus = actualStatus.equals(OrderStatus.CREATED)
                ? OrderStatus.IN_PROGRESS
                : OrderStatus.DELIVERED;

        order.setStatus(newStatus);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    private boolean isNumberOrdersCorrect(int numberOrders) {
        return numberOrders >= MAX_ORDERS_NUMBER;
    }
}
