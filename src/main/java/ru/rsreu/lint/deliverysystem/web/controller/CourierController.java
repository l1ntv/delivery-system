package ru.rsreu.lint.deliverysystem.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.service.CourierService;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;
import ru.rsreu.lint.deliverysystem.web.mapper.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    private final UserMapper userMapper;

    @PostMapping
    public UserDTO create(@RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        user = courierService.create(user);
        return userMapper.toDto(user);
    }

    @GetMapping
    public List<UserDTO> getAllCouriers() {
        List<User> users = courierService.findAll();
        return userMapper.toDtoList(users);
    }
}
