package ru.rsreu.lint.deliverysystem.service;

import ru.rsreu.lint.deliverysystem.model.User;

import java.util.List;

public interface CourierService {

    User create(User user);

    List<User> findAll();
}
