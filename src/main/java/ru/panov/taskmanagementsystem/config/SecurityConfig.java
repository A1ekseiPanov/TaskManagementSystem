package ru.panov.taskmanagementsystem.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.panov.taskmanagementsystem.security.JwtAuthenticationFilter;

import static ru.panov.taskmanagementsystem.util.PathConstants.AUTH_PATH;

/**
 * Конфигурация безопасности приложения.
 */
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Настраивает цепочку фильтров безопасности для HTTP запросов.
     *
     * @param httpSecurity конфигурация безопасности HTTP
     * @return настроенная цепочка фильтров безопасности
     * @throws Exception если произошла ошибка во время настройки
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                ))
                .exceptionHandling(configurer ->
                        configurer.authenticationEntryPoint(
                                (request, response, exception) -> {
                                    response.setStatus(
                                            HttpStatus.UNAUTHORIZED
                                                    .value());
                                }))
                .authorizeHttpRequests(configurer ->
                        configurer.requestMatchers(AUTH_PATH + "/**", "/swagger-ui/**", "/v3/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /**
     * Создает и настраивает провайдера аутентификации.
     *
     * @return объект AuthenticationProvider для аутентификации пользователей
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Предоставляет AuthenticationManager Bean для аутентификации.
     * Обрабатывает запрос на аутентификацию.
     *
     * @return AuthenticationManager объект для управления аутентификацией
     * @throws Exception если возникли проблемы с настройкой
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Создает и возвращает объект для кодирования паролей.
     *
     * @return объект PasswordEncoder для кодирования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}