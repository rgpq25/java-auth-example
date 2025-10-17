package com.renzo.auth_example.user;

import com.renzo.auth_example.user.dto.UserCreateRequest;
import com.renzo.auth_example.user.dto.UserResponse;
import com.renzo.auth_example.user.dto.UserUpdateRequest;
import com.renzo.auth_example.user.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("id", id));
    }

    public UserResponse findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("email", email));
    }

    public UserResponse createUser(UserCreateRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        User persistedUser = userRepository.save(user);
        return userMapper.toResponse(persistedUser);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));

        userMapper.updateEntity(existingUser, userRequest);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("id", id));
        userRepository.delete(existingUser);
    }
}
