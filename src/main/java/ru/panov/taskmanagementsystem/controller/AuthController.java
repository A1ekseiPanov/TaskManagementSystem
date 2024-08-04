package ru.panov.taskmanagementsystem.controller;


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
import ru.panov.taskmanagementsystem.model.dto.JwtTokenResponse;
import ru.panov.taskmanagementsystem.model.dto.UserDTO;
import ru.panov.taskmanagementsystem.model.dto.UserRequest;
import ru.panov.taskmanagementsystem.model.dto.UserResponse;
import ru.panov.taskmanagementsystem.service.UserService;

import java.util.Map;

import static ru.panov.taskmanagementsystem.util.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = AUTH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    private final UserService userService;

    @PostMapping(value = LOGIN_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JwtTokenResponse> login(@RequestBody UserRequest userRequest) {
        JwtTokenResponse token = userService.login(userRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping(value = REGISTRATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> registration(@Valid @RequestBody UserDTO userDTO,
                                                     BindingResult bindingResult,
                                                     UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            UserResponse user = userService.register(userDTO);
            return ResponseEntity.created(uriComponentsBuilder.
                            replacePath("users/{userId}")
                            .build(Map.of("userId", user.id())))
                    .body(user);
        }
    }
}