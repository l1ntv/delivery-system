package ru.rsreu.lint.deliverysystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rsreu.lint.deliverysystem.model.Order;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.OrderStatus;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByCourier_Id(Long courierId);

    List<Order> findAllByCourierAndStatus(User courier, OrderStatus status);
}
