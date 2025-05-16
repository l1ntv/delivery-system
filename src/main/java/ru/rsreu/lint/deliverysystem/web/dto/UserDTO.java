package ru.rsreu.lint.deliverysystem.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;

@Data
@Builder
public class UserDTO {

    @NotNull(message = "Login must be not null.")
    @Length(min = 1,
            max = 255,
            message = "Login length must be between {min} and {max}.")
    private String login;

    @NotNull(message = "Password must be not null.")
    @Length(min = 1,
            max = 255,
            message = "Password length must be between {min} and {max}.")
    private String password;

    @NotNull(message = "User role must be not null.")
    private UserRole role;
}
