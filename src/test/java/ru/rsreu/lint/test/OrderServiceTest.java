package ru.rsreu.lint.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rsreu.lint.deliverysystem.model.Order;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.OrderStatus;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;
import ru.rsreu.lint.deliverysystem.model.exception.OrderNotFoundException;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.model.exception.UserNotFoundException;
import ru.rsreu.lint.deliverysystem.repository.OrderRepository;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;
import ru.rsreu.lint.deliverysystem.service.OrderServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void create_ShouldSetCreatedStatusAndSaveOrder() {
        Order order = Order.builder()
                .description("Test order")
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.create(order);

        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void findAll_ShouldReturnAllOrders() {
        Order firstOrder = Order.builder()
                .description("Order 1")
                .address(null)
                .address(null)
                .status(OrderStatus.CREATED)
                .client(null)
                .courier(null)
                .build();
        firstOrder.setId(1L);

        Order secondOrder = Order.builder()
                .description("Order 2")
                .address(null)
                .address(null)
                .status(OrderStatus.DELIVERED)
                .client(null)
                .courier(null)
                .build();
        secondOrder.setId(2L);

        List<Order> orders = List.of(
                firstOrder,
                secondOrder
        );

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void findCourierOrders_ShouldReturnOrdersByCourierId() {
        User courier = User.builder()
                .login("Test login")
                .role(UserRole.COURIER)
                .build();
        courier.setId(1L);

        Order order = Order.builder()
                .description("Order for courier")
                .address(null)
                .address(null)
                .status(OrderStatus.IN_PROGRESS)
                .client(null)
                .courier(courier)
                .build();
        order.setId(1L);
        List<Order> orders = List.of(order);

        when(userRepository.findById(1L)).thenReturn(Optional.of(courier));
        when(orderRepository.findAllByCourier_Id(courier.getId())).thenReturn(orders);

        List<Order> result = orderService.findCourierOrders(courier.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAllByCourier_Id(courier.getId());
    }

    @Test
    void assignOrder_ShouldAssignCourierToOrder() {
        User courier = User.builder()
                .login("courier")
                .role(UserRole.COURIER)
                .build();
        courier.setId(1L);

        Order order = Order.builder()
                .description("Order to assign")
                .status(OrderStatus.CREATED)
                .build();
        order.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(courier));
        when(orderRepository.findAllByCourierAndStatus(courier, OrderStatus.IN_PROGRESS)).thenReturn(List.of());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.assignOrder(1L, 1L);

        assertNotNull(result);
        assertEquals(courier, result.getCourier());
        assertEquals(OrderStatus.IN_PROGRESS, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void assignOrder_CourierNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> orderService.assignOrder(1L, 1L));
        verify(orderRepository, never()).findById(anyLong());
    }

    @Test
    void assignOrder_OrderAlreadyAssigned_ShouldThrowException() {
        User courier = User.builder()
                .role(UserRole.COURIER)
                .build();
        courier.setId(1L);

        Order order = Order.builder()
                .description("Assigned order")
                .status(OrderStatus.CREATED)
                .courier(courier)
                .build();
        order.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(courier));
        when(orderRepository.findAllByCourierAndStatus(courier, OrderStatus.IN_PROGRESS)).thenReturn(List.of());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(ResourceConflictException.class, () -> orderService.assignOrder(1L, 1L));
    }

    @Test
    void assignOrder_CourierHasTooManyOrders_ShouldThrowException() {
        User courier = User.builder()
                .role(UserRole.COURIER)
                .build();
        courier.setId(1L);

        Order order = Order.builder()
                .description("Order to assign")
                .status(OrderStatus.CREATED)
                .build();
        order.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(courier));
        when(orderRepository.findAllByCourierAndStatus(courier, OrderStatus.IN_PROGRESS))
                .thenReturn(List.of(new Order(), new Order(), new Order()));

        assertThrows(ResourceConflictException.class, () -> orderService.assignOrder(1L, 1L));
    }

    @Test
    void updateOrderStatus_FromCreated_ShouldSetInProgress() {
        Order order = Order.builder()
                .description("Order created")
                .status(OrderStatus.CREATED)
                .build();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.updateOrderStatus(1L);

        assertEquals(OrderStatus.IN_PROGRESS, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrderStatus_FromInProgress_ShouldSetDelivered() {
        Order order = Order.builder()
                .description("Order in progress")
                .status(OrderStatus.IN_PROGRESS)
                .build();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.updateOrderStatus(1L);

        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrderStatus_OrderNotFound_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderStatus(1L));
    }
}
