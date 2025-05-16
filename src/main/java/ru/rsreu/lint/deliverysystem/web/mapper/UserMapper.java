package ru.rsreu.lint.deliverysystem.web.mapper;

import org.mapstruct.Mapper;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserDTO> {
}
