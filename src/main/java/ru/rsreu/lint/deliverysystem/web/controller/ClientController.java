package ru.rsreu.lint.deliverysystem.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.service.ClientService;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;
import ru.rsreu.lint.deliverysystem.web.mapper.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final ClientService clientService;

    private final UserMapper userMapper;

    @PostMapping()
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO userDTO) {
        User client = userMapper.toEntity(userDTO);
        client = clientService.create(client);
        UserDTO dto = userMapper.toDto(client);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllClients() {
        List<User> users = clientService.findAll();
        List<UserDTO> dtos = userMapper.toDtoList(users);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getClient(@PathVariable Long id) {
        User user = clientService.findById(id);
        UserDTO dto = userMapper.toDto(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }
}
