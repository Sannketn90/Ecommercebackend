package com.project.ecommerce.mapper;

import com.project.ecommerce.dto.SignupRequest;
import com.project.ecommerce.dto.UserResponse;
import com.project.ecommerce.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest request);

    UserResponse toResponse(User user);
}
