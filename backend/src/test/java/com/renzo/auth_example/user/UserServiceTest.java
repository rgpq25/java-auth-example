package com.renzo.auth_example.user;

import com.renzo.auth_example.user.dto.UserResponse;
import com.renzo.auth_example.user.exceptions.UserNotFoundException;
import com.renzo.auth_example.user.mappers.UserMapper;
import com.renzo.auth_example.user.models.User;
import com.renzo.auth_example.user.repositories.UserRepository;
import com.renzo.auth_example.user.services.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    @BeforeAll
    public static void init() {     // Used to set up certain reused objects or db mock connections
        System.out.println("BeforeAll");
    }

    @BeforeEach
    public void initEachTest() {    // Used to prepare dynamic objects before each test
        System.out.println("BeforeEach");
    }

    @Test
    public void createUser_shouldAddUserSuccessfully() {
        // given
        UserCreateRequest request = new UserCreateRequest("Renzo", "Pinto", "renzo@example.com", "12345");
        User entity = new User("Renzo", "Pinto", "renzo@example.com", "12345");
        UserResponse response = new UserResponse(1L, "Renzo", "Pinto", "renzo@example.com");

        Mockito.when(userMapper.toEntity(request)).thenReturn(entity);
        Mockito.when(userRepository.save(entity)).thenReturn(entity);
        Mockito.when(userMapper.toResponse(entity)).thenReturn(response);

        // when
        UserResponse result = userService.createUser(request);

        // assertions
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.id(), response.id());
    }

    @Test
    public void deleteUser_shouldSetUserInactiveAndSave() {
        // given
        Long id = 2L;
        User existingUser = new User();
        existingUser.setUserId(id);
        existingUser.setActive(true);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        // when
        userService.deleteUser(id);

        // then
        Assertions.assertFalse(existingUser.getActive());

        // repository interactions
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
        Mockito.verify(userRepository, Mockito.times(1)).save(existingUser);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {
        // given
        Long id = 999L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when + then
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));

        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @AfterAll
    public static void destroy() {
        System.out.println("AfterAll");
    }

    @AfterEach
    public void cleanup() {
        System.out.println("AfterEach");
    }

}
