package ru.rsreu.lint.deliverysystem.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private final UserMapper userMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserDTO userDTO) {
        User client = userMapper.toEntity(userDTO);
        client = clientService.create(client);
        return userMapper.toDto(client);
    }

    @GetMapping
    public List<UserDTO> getAllClients() {
        List<User> users = clientService.findAll();
        return userMapper.toDtoList(users);
    }

    @GetMapping("/{id}")
    public UserDTO getClient(@PathVariable Long id) {
        User user = clientService.findById(id);
        return userMapper.toDto(user);
    }

}
