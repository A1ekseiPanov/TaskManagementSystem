package ru.panov.taskmanagementsystem.service;

import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.LoginRequest;
import ru.panov.taskmanagementsystem.model.dto.request.UserRequest;
import ru.panov.taskmanagementsystem.model.dto.response.JwtTokenResponse;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;

public interface UserService {
    UserResponse register(UserRequest userRequest);

    JwtTokenResponse login(LoginRequest loginRequest);

    User getById(Long userId);
}