package com.renzo.auth_example.user;

import com.renzo.auth_example.user.dto.UserCreateRequest;
import com.renzo.auth_example.user.dto.UserResponse;
import com.renzo.auth_example.user.dto.UserUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserCreateRequest dto) {
        if (dto == null) return null;
        return new User(
                dto.name(),
                dto.lastname(),
                dto.email(),
                dto.password()
        );
    }

    public UserResponse toResponse(User entity) {
        if (entity == null) return null;
        return new UserResponse(
                entity.getId(),
                entity.getName(),
                entity.getLastname(),
                entity.getEmail()
        );
    }

    public void updateEntity(User entity, UserUpdateRequest dto) {
        entity.setName(dto.name());
        entity.setLastname(dto.lastname());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
    }
}
