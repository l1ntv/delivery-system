package ru.rsreu.lint.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;
import ru.rsreu.lint.deliverysystem.service.CourierServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class CourierServiceTest {

    @InjectMocks
    private CourierServiceImpl courierService;

    @Mock
    private UserRepository userRepository;

    @Test
    void create_CourierWithUniqueLogin_ShouldSaveUser() {
        User user = User.builder()
                .login("testCourier")
                .build();

        when(userRepository.findByLogin("testCourier")).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = courierService.create(user);

        assertNotNull(createdUser);
        assertEquals(UserRole.COURIER, createdUser.getRole());
        verify(userRepository, times(1)).findByLogin("testCourier");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void create_CourierWithExistingLogin_ShouldThrowException() {
        User user = User.builder()
                .login("testCourier")
                .build();

        when(userRepository.findByLogin("testCourier")).thenReturn(user);

        assertThrows(ResourceConflictException.class, () -> courierService.create(user));
        verify(userRepository, times(1)).findByLogin("testCourier");
        verify(userRepository, never()).save(user);
    }

    @Test
    void findAll_CouriersExist_ShouldReturnListOfCouriers() {
        User firstUser = User.builder()
                .login("firstUser")
                .role(UserRole.CLIENT)
                .build();
        firstUser.setId(1L);

        User secondUser = User.builder()
                .login("user2")
                .role(UserRole.CLIENT)
                .build();
        secondUser.setId(2L);

        List<User> userList = Arrays.asList(firstUser, secondUser);
        when(userRepository.findAllByRole(UserRole.COURIER)).thenReturn(userList);

        List<User> result = courierService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(firstUser));
        assertTrue(result.contains(secondUser));

        verify(userRepository, times(1)).findAllByRole(UserRole.COURIER);
    }

    @Test
    void findAll_NoCouriersExist_ShouldReturnEmptyList() {
        when(userRepository.findAllByRole(UserRole.COURIER)).thenReturn(List.of());

        List<User> result = courierService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findAllByRole(UserRole.COURIER);
    }
}
