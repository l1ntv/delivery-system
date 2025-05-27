package ru.rsreu.lint.deliverysystem.service;

import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;

import java.util.List;

public interface ClientService {

    UserDTO create(UserDTO user);

    List<UserDTO> findAll();

    UserDTO findById(Long id);

    void validateClientExists(Long id);
}
