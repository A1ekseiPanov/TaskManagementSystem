package ru.panov.taskmanagementsystem.service;

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
import ru.panov.taskmanagementsystem.mapper.UserMapper;
import ru.panov.taskmanagementsystem.model.Role;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.JwtTokenResponse;
import ru.panov.taskmanagementsystem.model.dto.UserDTO;
import ru.panov.taskmanagementsystem.model.dto.UserRequest;
import ru.panov.taskmanagementsystem.model.dto.UserResponse;
import ru.panov.taskmanagementsystem.reposirory.UserRepository;
import ru.panov.taskmanagementsystem.security.JwtService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService detailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(UserDTO userDTO) {
        Optional<User> currentUser = userRepository.findByEmail(userDTO.email());

        if (currentUser.isPresent()) {
            throw new DuplicateException("Такой пользователь уже существует");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        User newUser = User.builder()
                .lastName(userDTO.lastName())
                .firstName(userDTO.firstName())
                .email(userDTO.email())
                .roles(roles)
                .password(passwordEncoder.encode(userDTO.password()))
                .build();
        return userMapper.userToResponseEntity(userRepository.save(newUser));
    }

    public JwtTokenResponse login(UserRequest userRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userRequest.email(),
                            userRequest.password()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new InputDataConflictException("неправильное имя пользователя или пароль");
        }

        UserDetails userDetails = detailsService.loadUserByUsername(userRequest.email());
        String jwtToken = jwtService.generateToken(userDetails);
        return JwtTokenResponse.builder()
                .token(jwtToken)
                .build();
    }
}
