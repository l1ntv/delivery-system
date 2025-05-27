package ru.rsreu.lint.deliverysystem.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;

@Entity(name = "users")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class User extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String login;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
