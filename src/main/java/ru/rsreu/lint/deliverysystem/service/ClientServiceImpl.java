package ru.rsreu.lint.deliverysystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rsreu.lint.deliverysystem.aop.Loggable;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.model.exception.UserNotFoundException;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final UserRepository userRepository;

    @Override
    @Loggable
    @Transactional
    public User create(User user) {
        String login = user.getLogin();
        if (userRepository.findByLogin(login) != null) {
            throw new ResourceConflictException("Client with this login already exists.");
        }

        UserRole clientRole = UserRole.CLIENT;
        user.setRole(clientRole);
        return userRepository.save(user);
    }

    @Override
    @Loggable
    public List<User> findAll() {
        return userRepository.findAllByRole(UserRole.CLIENT);
    }

    @Override
    @Loggable
    public User findById(Long id) {
        return userRepository.findByIdAndRole(id, UserRole.CLIENT)
                .orElseThrow(() -> new UserNotFoundException("Client not found."));
    }

    @Override
    @Loggable
    public void validateClientExists(Long id) {
        if (!userRepository.existsByIdAndRole(id, UserRole.CLIENT)) {
            throw new UserNotFoundException("Client not found.");
        }
    }
}
