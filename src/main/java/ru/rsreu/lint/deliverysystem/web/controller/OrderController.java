package ru.rsreu.lint.deliverysystem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.rsreu.lint.deliverysystem.model.Order;
import ru.rsreu.lint.deliverysystem.service.ClientService;
import ru.rsreu.lint.deliverysystem.service.OrderService;
import ru.rsreu.lint.deliverysystem.web.dto.AssignCourierDTO;
import ru.rsreu.lint.deliverysystem.web.dto.OrderDTO;
import ru.rsreu.lint.deliverysystem.web.mapper.OrderMapper;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final ClientService clientService;

    private final OrderMapper orderMapper;

    @PostMapping
    public OrderDTO create(@RequestBody OrderDTO orderDTO) {
        clientService.validateClientExists(orderDTO.getClientId());
        Order order = orderMapper.toEntity(orderDTO);
        order = orderService.create(order);
        return orderMapper.toDto(order);
    }

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderService.findAll();
        return orderMapper.toDtoList(orders);
    }

    @GetMapping("/courier/{courierId}")
    public List<OrderDTO> getCourierOrders(@PathVariable Long courierId) {
        List<Order> orders = orderService.findCourierOrders(courierId);
        return orderMapper.toDtoList(orders);
    }

    @PutMapping("/{id}/assign")
    public OrderDTO assignOrder(@PathVariable Long id, @RequestBody AssignCourierDTO dto) {
        Order order = orderService.assignOrder(id, dto.getId());
        return orderMapper.toDto(order);
    }

    @PutMapping("/{id}/status")
    public OrderDTO updateStatus(@PathVariable Long id) {
        Order order = orderService.updateOrderStatus(id);
        return orderMapper.toDto(order);
    }
}
