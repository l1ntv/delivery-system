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
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;
import ru.rsreu.lint.deliverysystem.web.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Loggable
    @Transactional
    public UserDTO create(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        String login = user.getLogin();
        if (userRepository.findByLogin(login) != null) {
            throw new ResourceConflictException("Client with this login already exists.");
        }

        UserRole clientRole = UserRole.CLIENT;
        user.setRole(clientRole);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Loggable
    public List<UserDTO> findAll() {
        return userMapper.toDtoList(userRepository.findAllByRole(UserRole.CLIENT));
    }

    @Override
    @Loggable
    public UserDTO findById(Long id) {
        User user = userRepository.findByIdAndRole(id, UserRole.CLIENT)
                .orElseThrow(() -> new UserNotFoundException("Client not found."));
        return userMapper.toDto(user);
    }

    @Override
    @Loggable
    public void validateClientExists(Long id) {
        if (!userRepository.existsByIdAndRole(id, UserRole.CLIENT)) {
            throw new UserNotFoundException("Client not found.");
        }
    }
}
