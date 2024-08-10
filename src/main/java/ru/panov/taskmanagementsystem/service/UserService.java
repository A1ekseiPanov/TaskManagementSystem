package ru.panov.taskmanagementsystem.service;

import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.LoginRequest;
import ru.panov.taskmanagementsystem.model.dto.request.UserRequest;
import ru.panov.taskmanagementsystem.model.dto.response.JwtTokenResponse;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;

/**
 * Сервис для управления пользователями в системе управления задачами.
 * Этот интерфейс определяет методы для регистрации, аутентификации пользователей,
 * а также получения информации о пользователях.
 */
public interface UserService {
    /**
     * Регистрирует нового пользователя.
     *
     * @param userRequest объект запроса, содержащий данные для регистрации нового пользователя.
     * @return {@link UserResponse} объект, содержащий информацию о зарегистрированном пользователе.
     */
    UserResponse register(UserRequest userRequest);

    /**
     * Выполняет аутентификацию пользователя и возвращает JWT токен.
     *
     * @param loginRequest объект запроса, содержащий данные для аутентификации (email и пароль).
     * @return {@link JwtTokenResponse} объект, содержащий JWT токен для аутентифицированного пользователя.
     */
    JwtTokenResponse login(LoginRequest loginRequest);

    /**
     * Возвращает пользователя по его ID.
     *
     * @param userId ID пользователя.
     * @return объект {@link User}, представляющий пользователя.
     */
    User getById(Long userId);
}