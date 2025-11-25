package com.renzo.auth_example.user;

import com.renzo.auth_example.auth.dto.UserRegisterRequest;
import com.renzo.auth_example.user.dto.UserResponse;
import com.renzo.auth_example.user.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> findByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);
        UserResponse userResponse = userMapper.toResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest user
    ) {
        UserResponse updatedUser = userService.updateUser(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
