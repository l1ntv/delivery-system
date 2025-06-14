package ru.rsreu.lint.deliverysystem.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.rsreu.lint.deliverysystem.model.enums.OrderStatus;

@Entity(name = "orders")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Order extends AbstractEntity {

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    private User client;

    @ManyToOne
    private User courier;

    @Embedded
    private Address address;
}
