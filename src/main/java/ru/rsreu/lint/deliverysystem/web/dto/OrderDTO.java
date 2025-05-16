package ru.rsreu.lint.deliverysystem.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.rsreu.lint.deliverysystem.model.enums.OrderStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @Length(min = 0,
            max = 255,
            message = "Description length must be between {min} and {max}.")
    private String description;

    @NotNull(message = "Order status must be not null.")
    private OrderStatus status;

    @Length(min = 0,
            max = 255,
            message = "Street length must be between {min} and {max}.")
    private String street;

    @Length(min = 0,
            max = 255,
            message = "City length must be between {min} and {max}.")
    private String city;

    @Length(min = 0,
            max = 255,
            message = "Postal code length must be between {min} and {max}.")
    private String postalCode;

    @Length(min = 0,
            max = 255,
            message = "Country length must be between {min} and {max}.")
    private String country;

}