package ru.rsreu.lint.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import ru.rsreu.lint.deliverysystem.model.Address;
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
import ru.rsreu.lint.deliverysystem.web.dto.OrderDTO;
import ru.rsreu.lint.deliverysystem.web.mapper.OrderMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(orderService, "MAX_ORDERS_NUMBER", 3);
    }

    @Test
    void testSuccessCreateOrder() {
        OrderDTO dto = OrderDTO.builder()
                .description("Test order")
                .status(OrderStatus.CREATED)
                .street("Main St")
                .city("City")
                .postalCode("12345")
                .country("Country")
                .clientId(1L)
                .courierId(null)
                .build();

        Order savedOrder = Order.builder()
                .id(1L)
                .description("Test order")
                .status(OrderStatus.CREATED)
                .address(ru.rsreu.lint.deliverysystem.model.Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        when(orderMapper.toEntity(dto)).thenReturn(savedOrder);
        when(orderRepository.save(savedOrder)).thenReturn(savedOrder);

        OrderDTO expectedDto = OrderDTO.builder()
                .description("Test order")
                .status(OrderStatus.CREATED)
                .street("Main St")
                .city("City")
                .postalCode("12345")
                .country("Country")
                .clientId(1L)
                .courierId(null)
                .build();
        when(orderMapper.toDto(savedOrder)).thenReturn(expectedDto);

        OrderDTO result = orderService.create(dto);

        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertEquals("Main St", result.getStreet());
        assertEquals("City", result.getCity());
        assertEquals("12345", result.getPostalCode());
        assertEquals("Country", result.getCountry());
        verify(orderRepository, times(1)).save(savedOrder);
    }

    @Test
    void testSuccessFindAllOrders() {
        Order order = Order.builder()
                .id(1L)
                .description("Test order")
                .status(OrderStatus.CREATED)
                .address(Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        OrderDTO dto = OrderDTO.builder()
                .description("Test order")
                .status(OrderStatus.CREATED)
                .street("Main St")
                .city("City")
                .postalCode("12345")
                .country("Country")
                .clientId(1L)
                .courierId(null)
                .build();

        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toDtoList(List.of(order))).thenReturn(List.of(dto));

        List<OrderDTO> result = orderService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test order", result.get(0).getDescription());
        assertEquals("Main St", result.get(0).getStreet());
    }

    @Test
    void testFindCourierOrdersForExistsCourier() {
        User courier = User.builder().id(1L).role(UserRole.COURIER).build();
        Order order = Order.builder()
                .id(1L)
                .description("Test order")
                .status(OrderStatus.IN_PROGRESS)
                .courier(courier)
                .address(Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        OrderDTO dto = OrderDTO.builder()
                .description("Test order")
                .status(OrderStatus.IN_PROGRESS)
                .street("Main St")
                .city("City")
                .postalCode("12345")
                .country("Country")
                .clientId(1L)
                .courierId(1L)
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findAllByCourier_Id(1L)).thenReturn(List.of(order));
        when(orderMapper.toDtoList(List.of(order))).thenReturn(List.of(dto));

        List<OrderDTO> result = orderService.findCourierOrders(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Main St", result.get(0).getStreet());
        assertEquals(1L, result.get(0).getCourierId());
    }

    @Test
    void testFindCourierOrdersForNotFoundCourier() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> orderService.findCourierOrders(1L)
        );

        assertEquals("Courier not found.", exception.getMessage());
    }

    @Test
    void testAssignOrderForNotFoundCourier() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> orderService.assignOrder(1L, 1L)
        );

        assertEquals("Courier not found.", exception.getMessage());
    }

    @Test
    void testAssignOrderIfMaxOrdersReached() {
        User courier = User.builder().id(1L).build();
        Order order = Order.builder()
                .id(2L)
                .status(OrderStatus.CREATED)
                .address(Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(courier));
        when(orderRepository.findAllByCourierAndStatus(courier, OrderStatus.IN_PROGRESS))
                .thenReturn(List.of(new Order(), new Order(), new Order()));
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> orderService.assignOrder(2L, 1L)
        );

        assertEquals("Incorrect number of orders.", exception.getMessage());
    }

    @Test
    void testAssignOrderIfOrderAlreadyAssigned() {
        User courier = User.builder().id(1L).build();
        Order assignedOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.IN_PROGRESS)
                .courier(User.builder().id(2L).build())
                .address(Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(courier));
        when(orderRepository.findAllByCourierAndStatus(courier, OrderStatus.IN_PROGRESS)).thenReturn(List.of());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(assignedOrder));

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> orderService.assignOrder(1L, 1L)
        );

        assertEquals("Order is already assigned to courier.", exception.getMessage());
    }

    @Test
    void testSuccessAssignOrder() {
        User courier = User.builder().id(1L).build();
        Order order = Order.builder()
                .id(1L)
                .description("Test order")
                .status(OrderStatus.CREATED)
                .address(Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        Order savedOrder = Order.builder()
                .id(1L)
                .description("Test order")
                .status(OrderStatus.IN_PROGRESS)
                .courier(courier)
                .address(Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        OrderDTO dto = OrderDTO.builder()
                .description("Test order")
                .status(OrderStatus.IN_PROGRESS)
                .street("Main St")
                .city("City")
                .postalCode("12345")
                .country("Country")
                .clientId(1L)
                .courierId(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(courier));
        when(orderRepository.findAllByCourierAndStatus(courier, OrderStatus.IN_PROGRESS)).thenReturn(List.of());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(dto);

        OrderDTO result = orderService.assignOrder(1L, 1L);

        assertNotNull(result);
        assertEquals(OrderStatus.IN_PROGRESS, result.getStatus());
        assertEquals(1L, result.getCourierId());
        assertEquals("Main St", result.getStreet());
    }

    @Test
    void testUpdateOrderStatusFromCreatedToInProgress() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .address(Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        Order updatedOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.IN_PROGRESS)
                .address(Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        OrderDTO dto = OrderDTO.builder()
                .status(OrderStatus.IN_PROGRESS)
                .street("Main St")
                .city("City")
                .postalCode("12345")
                .country("Country")
                .clientId(1L)
                .courierId(1L)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(updatedOrder);
        when(orderMapper.toDto(updatedOrder)).thenReturn(dto);

        OrderDTO result = orderService.updateOrderStatus(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.IN_PROGRESS, result.getStatus());
        assertEquals("Main St", result.getStreet());
    }

    @Test
    void testUpdateOrderStatusFromInProgressToDelivered() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.IN_PROGRESS)
                .address(Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        Order updatedOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.DELIVERED)
                .address(Address.builder()
                        .street("Main St")
                        .city("City")
                        .postalCode("12345")
                        .country("Country")
                        .build())
                .build();

        OrderDTO dto = OrderDTO.builder()
                .status(OrderStatus.DELIVERED)
                .street("Main St")
                .city("City")
                .postalCode("12345")
                .country("Country")
                .clientId(1L)
                .courierId(1L)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(updatedOrder);
        when(orderMapper.toDto(updatedOrder)).thenReturn(dto);

        OrderDTO result = orderService.updateOrderStatus(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        assertEquals("Main St", result.getStreet());
    }

    @Test
    void testUpdateOrderStatusForNotFoundOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.updateOrderStatus(1L)
        );

        assertEquals("Order not found.", exception.getMessage());
    }
}
