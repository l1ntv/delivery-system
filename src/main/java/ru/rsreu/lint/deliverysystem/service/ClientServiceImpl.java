package ru.rsreu.lint.deliverysystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.model.exception.UserNotFoundException;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        User possibleUser = userRepository.findByLogin(user.getLogin());
        if (possibleUser != null) {
            throw new ResourceConflictException();
        }

        UserRole clientRole = UserRole.CLIENT;
        user.setRole(clientRole);
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }
}
