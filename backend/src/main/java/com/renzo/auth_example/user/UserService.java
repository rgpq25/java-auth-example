package com.renzo.auth_example.user;

import com.renzo.auth_example.user.dto.UserCreateRequest;
import com.renzo.auth_example.user.dto.UserResponse;
import com.renzo.auth_example.user.dto.UserUpdateRequest;
import com.renzo.auth_example.user.exceptions.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAllByIsActiveTrue();
        return users.stream().map(userMapper::toResponse).toList();
    }

    public UserResponse findById(Long id) {
        return userRepository.findByIdAndIsActiveTrue(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("id", id));
    }

    public UserResponse findByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }

    public UserResponse createUser(UserCreateRequest userRequest) {
        userRepository.findByEmailAndIsActiveTrue(userRequest.email())
                .ifPresent(u -> {
                    throw new DataIntegrityViolationException("User with email already exists: " + userRequest.email());
                });

        User user = userMapper.toEntity(userRequest);
        User persistedUser = userRepository.save(user);
        return userMapper.toResponse(persistedUser);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest userRequest) {
        User existingUser = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));

        userRepository.findByEmailAndIsActiveTrue(userRequest.email())
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
        existingUser.setActive(false);
        userRepository.save(existingUser);
    }
}
