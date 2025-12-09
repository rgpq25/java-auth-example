package com.renzo.auth_example.user;

import com.renzo.auth_example.user.controllers.UserController;
import com.renzo.auth_example.user.dto.UserResponse;
import com.renzo.auth_example.user.models.User;
import com.renzo.auth_example.user.repositories.UserRepository;
import com.renzo.auth_example.user.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void createUser_shouldCreateUserSuccessfully() {
        // given
        UserCreateRequest request = new UserCreateRequest("Renzo", "Pinto", "renzo@example.com", "12345");
        User entity = new User("Renzo", "Pinto", "renzo@example.com", "12345");
        UserResponse response = new UserResponse(1L, "Renzo", "Pinto", "renzo@example.com");

        Mockito.when(userService.createUser(request)).thenReturn(response);
        String url = "/users";

        // when
//        mockMvc.perform(get(url)).andExpect(HttpStatus.OK);

    }
}
