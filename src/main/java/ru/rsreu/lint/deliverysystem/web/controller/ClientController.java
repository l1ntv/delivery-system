package ru.rsreu.lint.deliverysystem.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.rsreu.lint.deliverysystem.service.ClientService;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final ClientService clientService;

    @PostMapping()
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO userDTO) {
        UserDTO dto = clientService.create(userDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllClients() {
        List<UserDTO> users = clientService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getClient(@PathVariable Long id) {
        UserDTO dto = clientService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }
}
