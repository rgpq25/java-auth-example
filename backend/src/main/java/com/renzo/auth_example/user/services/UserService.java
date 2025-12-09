package com.renzo.auth_example.user.services;

import com.renzo.auth_example.auth.dto.UserRegisterRequest;
import com.renzo.auth_example.user.mappers.UserMapper;
import com.renzo.auth_example.user.dto.UserResponse;
import com.renzo.auth_example.user.dto.UserUpdateRequest;
import com.renzo.auth_example.user.exceptions.UserNotFoundException;
import com.renzo.auth_example.user.models.User;
import com.renzo.auth_example.user.repositories.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toResponse).toList();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User createUser(UserRegisterRequest userRequest) {
        userRepository.findByEmail(userRequest.email())
                .ifPresent(u -> {
                    throw new DataIntegrityViolationException("User with email already exists: " + userRequest.email());
                });

        User user = userMapper.toEntity(userRequest);
        return userRepository.save(user);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));

        userRepository.findByEmail(userRequest.email())
            .ifPresent(u -> {
                if (!Objects.equals(u.getId(), existingUser.getId())) throw new DataIntegrityViolationException("User with email already exists: " + userRequest.email());
            });

        userMapper.updateEntity(existingUser, userRequest);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));
        userRepository.deleteById(existingUser.getId());
    }
}
