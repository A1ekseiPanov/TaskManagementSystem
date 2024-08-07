package ru.panov.taskmanagementsystem.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.panov.taskmanagementsystem.model.dto.request.LoginRequest;
import ru.panov.taskmanagementsystem.model.dto.request.UserRequest;
import ru.panov.taskmanagementsystem.model.dto.response.JwtTokenResponse;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;
import ru.panov.taskmanagementsystem.service.UserService;

import java.util.Map;

import static ru.panov.taskmanagementsystem.util.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = AUTH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    private final UserService userService;

    @Operation(
            summary = "Вход в систему",
            description = "Вход в систему, получаем токен для дальнейшей авторизации"
    )
    @PostMapping(value = LOGIN_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        JwtTokenResponse token = userService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Регистрация нового пользователя"
    )
    @PostMapping(value = REGISTRATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registration(@Valid @RequestBody UserRequest userRequest,
                                                     BindingResult bindingResult,
                                                     UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            UserResponse user = userService.register(userRequest);
            return ResponseEntity.created(uriComponentsBuilder.
                            replacePath("users/{userId}")
                            .build(Map.of("userId", user.id())))
                    .body(user);
        }
    }
}