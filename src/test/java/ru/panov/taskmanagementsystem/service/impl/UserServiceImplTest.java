package ru.panov.taskmanagementsystem.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.panov.taskmanagementsystem.exception.DuplicateException;
import ru.panov.taskmanagementsystem.exception.InputDataConflictException;
import ru.panov.taskmanagementsystem.exception.NotFoundException;
import ru.panov.taskmanagementsystem.mapper.UserMapper;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.LoginRequest;
import ru.panov.taskmanagementsystem.model.dto.request.UserRequest;
import ru.panov.taskmanagementsystem.model.dto.response.JwtTokenResponse;
import ru.panov.taskmanagementsystem.reposirory.UserRepository;
import ru.panov.taskmanagementsystem.security.JwtService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserDetailsService detailsService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Регистрация, успешная регистрация пользователя")
    void register_ValidUserRegisterSuccess() {
        UserRequest userRequest = UserRequest.builder()
                .email("user1@user1.ru")
                .password("user1")
                .build();
        when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());

        userService.register(userRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Регистрация, пользователь уже существует")
    void register_ExistingUserThrowsInputDataConflictException() {
        UserRequest userRequest = UserRequest.builder()
                .email("user1@user1.ru")
                .password("user1")
                .build();
        when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(User.builder().build()));

        assertThatThrownBy(() -> userService.register(userRequest))
                .isInstanceOf(DuplicateException.class)
                .hasMessage("Такой пользователь уже существует");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Авторизация, успешная авторизация пользователя")
    void login_LoginSuccess() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("user1@user1.ru")
                .password("user1")
                .build();

        User user = User.builder()
                .email(loginRequest.email())
                .password(loginRequest.password())
                .build();

        String jwtToken = "JwtToken";

        when(detailsService.loadUserByUsername(loginRequest.email())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(jwtToken);

        JwtTokenResponse jwt = userService.login(loginRequest);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        verify(jwtService).generateToken(user);
        assertThat(jwt.token()).isEqualTo(jwtToken);
    }

    @Test
    @DisplayName("Авторизация, неверное имя пользователя или пароль")
    public void login_BadCredentialsThrowsInvalidCredentials() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("invalidEmail")
                .password("invalidPassword")
                .build();

        doThrow(new BadCredentialsException("")).when(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(), loginRequest.password()));

        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(InputDataConflictException.class)
                .hasMessage("Неправильное имя пользователя или пароль");

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
    }

    @Test
    @DisplayName("Успешное получение пользователя по его id")
    public void getById_ValidId_ReturnsUser() {
        Long userId = 1L;
        User userTest = User.builder()
                .email("user1@user1.ru")
                .password("user1")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userTest));

        User user = userService.getById(userId);

        assertThat(user).isEqualTo(userTest);
    }

    @Test
    @DisplayName("Получение пользователя по его id с неподходящим id")
    public void getById_InvalidId_ThrowsNotFoundException() {
        Long userId = 0L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с id:%s не найден".formatted(userId));
    }
}