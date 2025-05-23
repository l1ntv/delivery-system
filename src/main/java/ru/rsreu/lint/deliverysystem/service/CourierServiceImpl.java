package ru.rsreu.lint.deliverysystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rsreu.lint.deliverysystem.aop.Loggable;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourierServiceImpl implements CourierService {

    private final UserRepository userRepository;

    @Override
    @Loggable
    @Transactional
    public User create(User user) {
        String login = user.getLogin();
        if (userRepository.findByLogin(login) != null) {
            throw new ResourceConflictException("Courier with this login already exists.");
        }

        UserRole clientRole = UserRole.COURIER;
        user.setRole(clientRole);
        return userRepository.save(user);
    }

    @Override
    @Loggable
    public List<User> findAll() {
        return userRepository.findAllByRole(UserRole.COURIER);
    }
}
