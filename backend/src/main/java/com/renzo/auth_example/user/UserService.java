package com.renzo.auth_example.user;

import com.renzo.auth_example.user.dto.UserRequest;
import com.renzo.auth_example.user.dto.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    public UserResponse findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    public UserResponse createUser(UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        User persistedUser = userRepository.save(user);
        return userMapper.toResponse(persistedUser);
    }
}
