package ru.rsreu.lint.deliverysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rsreu.lint.deliverysystem.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
