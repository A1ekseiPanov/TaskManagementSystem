package ru.panov.taskmanagementsystem.mapper;

import org.mapstruct.Mapper;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse userToResponseEntity(User user);
    List<UserResponse> listUserToListResponseEntity(List<User> users);
}