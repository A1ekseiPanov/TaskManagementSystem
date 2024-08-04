package ru.panov.taskmanagementsystem.mapper;

import org.mapstruct.Mapper;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse userToResponseEntity(User user);
}