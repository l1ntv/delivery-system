package ru.rsreu.lint.deliverysystem.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.rsreu.lint.deliverysystem.model.enums.OrderStatus;

@Entity(name = "orders")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order extends AbstractEntity {

    @Column(name = "description", nullable = false)
    @Length(min = 0, max = 255)
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
