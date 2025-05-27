package ru.rsreu.lint.deliverysystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rsreu.lint.deliverysystem.aop.Loggable;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;
import ru.rsreu.lint.deliverysystem.web.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourierServiceImpl implements CourierService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Loggable
    @Transactional
    public UserDTO create(UserDTO dto) {
        User user = userMapper.toEntity(dto);
        String login = user.getLogin();
        if (userRepository.findByLogin(login) != null) {
            throw new ResourceConflictException("Courier with this login already exists.");
        }

        UserRole clientRole = UserRole.COURIER;
        user.setRole(clientRole);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Loggable
    public List<UserDTO> findAll() {
        return userMapper.toDtoList(userRepository.findAllByRole(UserRole.COURIER));
    }
}
