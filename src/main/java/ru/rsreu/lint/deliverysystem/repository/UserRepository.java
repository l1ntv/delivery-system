package ru.rsreu.lint.deliverysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);

    List<User> findAllByRole(UserRole role);

    Optional<User> findByIdAndRole(Long id, UserRole role);
}
