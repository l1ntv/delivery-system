package ru.rsreu.lint.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.rsreu.lint.deliverysystem.model.User;
import ru.rsreu.lint.deliverysystem.model.enums.UserRole;
import ru.rsreu.lint.deliverysystem.model.exception.ResourceConflictException;
import ru.rsreu.lint.deliverysystem.model.exception.UserNotFoundException;
import ru.rsreu.lint.deliverysystem.repository.UserRepository;
import ru.rsreu.lint.deliverysystem.service.ClientServiceImpl;
import ru.rsreu.lint.deliverysystem.web.dto.UserDTO;
import ru.rsreu.lint.deliverysystem.web.mapper.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientServiceTest {

    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private User user;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        user.setRole(UserRole.CLIENT);

        userDTO = UserDTO.builder()
                .login("testuser")
                .role(UserRole.CLIENT)
                .build();
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.findByLogin("testuser")).thenReturn(null);
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDTO);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = clientService.create(userDTO);

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateUserWithExistingLogin() {
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.findByLogin("testuser")).thenReturn(user);

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> clientService.create(userDTO)
        );

        assertEquals("Client with this login already exists.", exception.getMessage());
    }

    @Test
    void testFindAllClients() {
        List<User> users = List.of(user);
        List<UserDTO> dtos = List.of(
                UserDTO.builder()
                        .login("testuser")
                        .role(UserRole.CLIENT)
                        .build()
        );

        when(userRepository.findAllByRole(UserRole.CLIENT)).thenReturn(users);
        when(userMapper.toDtoList(users)).thenReturn(dtos);

        List<UserDTO> result = clientService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getLogin());
    }

    @Test
    void testFindByIdForExistsClient() {
        when(userRepository.findByIdAndRole(1L, UserRole.CLIENT)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(
                UserDTO.builder()
                        .login("testuser")
                        .role(UserRole.CLIENT)
                        .build()
        );

        UserDTO result = clientService.findById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
    }

    @Test
    void testFindByIdForNotFoundClient() {
        when(userRepository.findByIdAndRole(1L, UserRole.CLIENT)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> clientService.findById(1L)
        );

        assertEquals("Client not found.", exception.getMessage());
    }

    @Test
    void testValidateClientExists() {
        when(userRepository.existsByIdAndRole(1L, UserRole.CLIENT)).thenReturn(true);
        assertDoesNotThrow(() -> clientService.validateClientExists(1L));
    }

    @Test
    void testValidateNotFoundClient() {
        when(userRepository.existsByIdAndRole(1L, UserRole.CLIENT)).thenReturn(false);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> clientService.validateClientExists(1L)
        );

        assertEquals("Client not found.", exception.getMessage());
    }
}
