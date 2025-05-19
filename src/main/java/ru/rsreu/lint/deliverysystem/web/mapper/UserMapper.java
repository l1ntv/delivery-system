package ru.rsreu.lint.deliverysystem.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserDTO> {

    @Mapping(source = "role", target = "role")
    UserDTO toDto(User user);

    @Mapping(source = "role", target = "role")
    User toEntity(UserDTO userDTO);
}
