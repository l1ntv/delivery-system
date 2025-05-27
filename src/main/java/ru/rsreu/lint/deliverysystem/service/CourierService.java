package ru.rsreu.lint.deliverysystem.service;

import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;

import java.util.List;

public interface CourierService {

    UserDTO create(UserDTO user);

    List<UserDTO> findAll();
}
