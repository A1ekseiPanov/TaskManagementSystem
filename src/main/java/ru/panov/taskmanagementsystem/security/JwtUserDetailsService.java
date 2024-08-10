package ru.panov.taskmanagementsystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.panov.taskmanagementsystem.reposirory.UserRepository;

/**
 * Сервис для загрузки деталей пользователя по его email.
 * Реализует интерфейс {@link UserDetailsService} и предоставляет механизм
 * для получения информации о пользователе из базы данных.
 */
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Загружает детали пользователя по его email.
     *
     * @param email email пользователя.
     * @return объект {@link UserDetails}, содержащий информацию о пользователе.
     * @throws UsernameNotFoundException если пользователь с указанным email не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User ‘" + email + "’ not found"));
    }
}