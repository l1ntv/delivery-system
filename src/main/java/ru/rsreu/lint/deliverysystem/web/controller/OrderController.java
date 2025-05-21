package ru.rsreu.lint.deliverysystem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rsreu.lint.deliverysystem.model.Order;
import ru.rsreu.lint.deliverysystem.service.ClientService;
import ru.rsreu.lint.deliverysystem.service.OrderService;
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
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO orderDTO) {
        clientService.validateClientExists(orderDTO.getClientId());
        Order order = orderMapper.toEntity(orderDTO);
        order = orderService.create(order);
        OrderDTO dto = orderMapper.toDto(order);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        List<OrderDTO> dtos = orderMapper.toDtoList(orders);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtos);
    }

    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<OrderDTO>> getCourierOrders(@PathVariable Long courierId) {
        List<Order> orders = orderService.findCourierOrders(courierId);
        List<OrderDTO> dtos = orderMapper.toDtoList(orders);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtos);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<OrderDTO> assignOrder(@PathVariable Long id, @RequestBody Long courierId) {
        Order order = orderService.assignOrder(id, courierId);
        OrderDTO dto = orderMapper.toDto(order);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long id) {
        Order order = orderService.updateOrderStatus(id);
        OrderDTO dto = orderMapper.toDto(order);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }
}
