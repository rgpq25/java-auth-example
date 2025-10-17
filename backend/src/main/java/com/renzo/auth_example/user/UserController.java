package com.renzo.auth_example.user;

import com.renzo.auth_example.user.dto.UserRequest;
import com.renzo.auth_example.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
        UserResponse user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("")
    public ResponseEntity<UserResponse> save(@Valid @RequestBody UserRequest user) {
        UserResponse savedUser = userService.createUser(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.id())
                .toUri();
        return ResponseEntity.created(location).body(savedUser);
    }
}
