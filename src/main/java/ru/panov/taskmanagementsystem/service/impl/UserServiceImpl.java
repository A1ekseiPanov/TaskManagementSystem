package ru.panov.taskmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.panov.taskmanagementsystem.exception.DuplicateException;
import ru.panov.taskmanagementsystem.exception.InputDataConflictException;
import ru.panov.taskmanagementsystem.exception.NotFoundException;
import ru.panov.taskmanagementsystem.mapper.UserMapper;
import ru.panov.taskmanagementsystem.model.Role;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.response.JwtTokenResponse;
import ru.panov.taskmanagementsystem.model.dto.request.UserRequest;
import ru.panov.taskmanagementsystem.model.dto.request.LoginRequest;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;
import ru.panov.taskmanagementsystem.reposirory.UserRepository;
import ru.panov.taskmanagementsystem.security.JwtService;
import ru.panov.taskmanagementsystem.service.UserService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService detailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(UserRequest userRequest) {
        Optional<User> currentUser = userRepository.findByEmail(userRequest.email());

        if (currentUser.isPresent()) {
            throw new DuplicateException("Такой пользователь уже существует");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        User newUser = User.builder()
                .lastName(userRequest.lastName())
                .firstName(userRequest.firstName())
                .email(userRequest.email())
                .roles(roles)
                .password(passwordEncoder.encode(userRequest.password()))
                .build();
        return userMapper.userToResponseEntity(userRepository.save(newUser));
    }

    @Override
    public JwtTokenResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InputDataConflictException("Неправильное имя пользователя или пароль");
        }

        UserDetails userDetails = detailsService.loadUserByUsername(loginRequest.email());
        String jwtToken = jwtService.generateToken(userDetails);
        return JwtTokenResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:%s не найден".formatted(userId)));
    }
}