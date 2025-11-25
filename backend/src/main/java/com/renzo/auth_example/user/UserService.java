package com.renzo.auth_example.user;

import com.renzo.auth_example.auth.dto.UserRegisterRequest;
import com.renzo.auth_example.user.dto.UserResponse;
import com.renzo.auth_example.user.dto.UserUpdateRequest;
import com.renzo.auth_example.user.exceptions.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAllByIsActiveTrue();
        return users.stream().map(userMapper::toResponse).toList();
    }

    public UserResponse findById(Long id) {
        return userRepository.findByUserIdAndIsActiveTrue(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("id", id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }

    public User createUser(UserRegisterRequest userRequest) {
        userRepository.findByEmailAndIsActiveTrue(userRequest.email())
                .ifPresent(u -> {
                    throw new DataIntegrityViolationException("User with email already exists: " + userRequest.email());
                });
        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest userRequest) {
        User existingUser = userRepository.findByUserIdAndIsActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));

        userRepository.findByEmailAndIsActiveTrue(userRequest.email())
            .ifPresent(u -> {
                if (!Objects.equals(u.getUserId(), existingUser.getUserId())) throw new DataIntegrityViolationException("User with email already exists: " + userRequest.email());
            });

        userMapper.updateEntity(existingUser, userRequest);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));
        existingUser.setActive(false);
        userRepository.save(existingUser);
    }
}
