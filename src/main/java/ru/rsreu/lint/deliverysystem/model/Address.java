package ru.rsreu.lint.deliverysystem.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @NotBlank
    @Size(max = 255)
    private String street;

    @NotBlank
    @Size(max = 255)
    private String city;

    @NotBlank
    @Size(max = 255)
    private String postalCode;

    @NotBlank
    @Size(max = 255)
    private String country;
}
