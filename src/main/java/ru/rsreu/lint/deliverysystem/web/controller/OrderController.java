package ru.rsreu.lint.deliverysystem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO orderDTO) {
        clientService.validateClientExists(orderDTO.getClientId());
        OrderDTO dto = orderService.create(orderDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orders);
    }

    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<OrderDTO>> getCourierOrders(@PathVariable Long courierId) {
        List<OrderDTO> orders = orderService.findCourierOrders(courierId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orders);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<OrderDTO> assignOrder(@PathVariable Long id, @RequestBody AssignCourierDTO assignCourierDTO) {
        OrderDTO order = orderService.assignOrder(id, assignCourierDTO.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(order);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long id) {
        OrderDTO order = orderService.updateOrderStatus(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(order);
    }
}
