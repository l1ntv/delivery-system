package ru.rsreu.lint.deliverysystem.service;

import ru.rsreu.lint.deliverysystem.model.User;

import java.util.List;

public interface ClientService {

    User create(User user);

    List<User> findAll();

    User findById(Long id);
}
