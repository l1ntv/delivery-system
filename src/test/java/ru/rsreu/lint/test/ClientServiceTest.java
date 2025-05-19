package ru.rsreu.lint.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.model.exception.UserNotFoundException;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;
import ru.rsreu.lint.deliverysystem.service.ClientServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private UserRepository userRepository;

    @Test
    void findById_ExistingClient_ShouldReturnUser() {
        Long userId = 1L;
        User user = User.builder()
                .login("testClient")
                .role(UserRole.CLIENT)
                .build();
        user.setId(userId);

        when(userRepository.findByIdAndRole(userId, UserRole.CLIENT)).thenReturn(Optional.of(user));

        User foundUser = clientService.findById(userId);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals("testClient", foundUser.getLogin());
        assertEquals(UserRole.CLIENT, foundUser.getRole());

        verify(userRepository, times(1)).findByIdAndRole(userId, UserRole.CLIENT);
    }

    @Test
    void findById_NonExistingClient_ShouldThrowException() {
        Long userId = 1L;

        when(userRepository.findByIdAndRole(userId, UserRole.CLIENT)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> clientService.findById(userId));
        verify(userRepository, times(1)).findByIdAndRole(userId, UserRole.CLIENT);
    }

    @Test
    void findAll_ClientsExist_ShouldReturnListOfClients() {
        User firstUser = User.builder()
                .login("firstUser")
                .role(UserRole.CLIENT)
                .build();
        firstUser.setId(1L);

        User secondUser = User.builder()
                .login("secondUser")
                .role(UserRole.CLIENT)
                .build();
        secondUser.setId(2L);

        List<User> userList = Arrays.asList(firstUser, secondUser);
        when(userRepository.findAllByRole(UserRole.CLIENT)).thenReturn(userList);

        List<User> result = clientService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(firstUser));
        assertTrue(result.contains(secondUser));

        verify(userRepository, times(1)).findAllByRole(UserRole.CLIENT);
    }

    @Test
    void findAll_NoClientsExist_ShouldReturnEmptyList() {
        when(userRepository.findAllByRole(UserRole.CLIENT)).thenReturn(List.of());

        List<User> result = clientService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findAllByRole(UserRole.CLIENT);
    }

    @Test
    void create_ClientWithUniqueLogin_ShouldSaveUser() {
        User user = User.builder()
                .login("testClient")
                .build();

        when(userRepository.findByLogin("testClient")).thenReturn(null);
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = clientService.create(user);

        assertNotNull(createdUser);
        assertEquals(UserRole.CLIENT, createdUser.getRole());
        verify(userRepository, times(1)).findByLogin("testClient");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void create_ClientWithExistingLogin_ShouldThrowException() {
        User user = User.builder()
                .login("testClient")
                .build();

        when(userRepository.findByLogin("testClient")).thenReturn(user);

        assertThrows(ResourceConflictException.class, () -> clientService.create(user));
        verify(userRepository, times(1)).findByLogin("testClient");
        verify(userRepository, never()).save(user);
    }
}
