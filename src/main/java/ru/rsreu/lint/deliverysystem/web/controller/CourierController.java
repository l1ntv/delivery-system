package ru.rsreu.lint.deliverysystem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rsreu.lint.deliverysystem.service.CourierService;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;

import java.util.List;

@RestController
@RequestMapping("/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO) {
        UserDTO dto = courierService.create(userDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllCouriers() {
        List<UserDTO> users = courierService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }
}
