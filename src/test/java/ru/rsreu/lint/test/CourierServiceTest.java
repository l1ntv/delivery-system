package ru.rsreu.lint.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;
import ru.rsreu.lint.deliverysystem.service.CourierServiceImpl;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;
import ru.rsreu.lint.deliverysystem.web.mapper.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CourierServiceTest {

    @InjectMocks
    private CourierServiceImpl courierService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private User user;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .login("courieruser")
                .role(UserRole.COURIER)
                .build();

        userDTO = UserDTO.builder()
                .login("courieruser")
                .role(UserRole.COURIER)
                .build();
    }

    @Test
    void testSuccessCreateCourier() {
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.findByLogin("courieruser")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);

        UserDTO result = courierService.create(userDTO);

        assertNotNull(result);
        assertEquals("courieruser", result.getLogin());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateCourierWithExistingLogin() {
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.findByLogin("courieruser")).thenReturn(user);

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> courierService.create(userDTO)
        );

        assertEquals("Courier with this login already exists.", exception.getMessage());
    }

    @Test
    void testFindAllCouriers() {
        List<User> couriers = List.of(user);
        List<UserDTO> dtoList = List.of(userDTO);

        when(userRepository.findAllByRole(UserRole.COURIER)).thenReturn(couriers);
        when(userMapper.toDtoList(couriers)).thenReturn(dtoList);

        List<UserDTO> result = courierService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("courieruser", result.get(0).getLogin());
    }
}
