package com.renzo.auth_example.user.mappers;

import com.renzo.auth_example.auth.dto.UserRegisterRequest;
import com.renzo.auth_example.user.dto.UserResponse;
import com.renzo.auth_example.user.dto.UserUpdateRequest;
import com.renzo.auth_example.user.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRegisterRequest dto) {
        if (dto == null) return null;
        return new User(
                dto.name(),
                dto.email(),
                false,
                null
        );
    }

    public UserResponse toResponse(User entity) {
        if (entity == null) return null;
        return new UserResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail()
        );
    }

    public void updateEntity(User entity, UserUpdateRequest dto) {
        entity.setName(dto.name());
        entity.setEmail(dto.email());
    }
}
