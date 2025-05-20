package ru.rsreu.lint.deliverysystem.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.rsreu.lint.deliverysystem.model.Order;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.web.dto.OrderDTO;

@Mapper(componentModel = "spring")
public interface OrderMapper extends Mappable<Order, OrderDTO> {

    @Override
    @Mapping(source = "address.street", target = "street")
    @Mapping(source = "address.city", target = "city")
    @Mapping(source = "address.postalCode", target = "postalCode")
    @Mapping(source = "address.country", target = "country")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "courier.id", target = "courierId")
    OrderDTO toDto(Order order);

    @Override
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.postalCode", source = "postalCode")
    @Mapping(target = "address.country", source = "country")
    @Mapping(target = "client", source = "clientId")
    @Mapping(target = "courier", source = "courierId")
    Order toEntity(OrderDTO dto);

    default User map(Long id) {
        if (id == null) return null;
        User user = new User();
        user.setId(id);
        return user;
    }
}
