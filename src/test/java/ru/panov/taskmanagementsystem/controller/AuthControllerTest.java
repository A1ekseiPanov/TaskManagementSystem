package ru.panov.taskmanagementsystem.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.panov.taskmanagementsystem.config.TestConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.panov.taskmanagementsystem.util.PathConstants.*;


@SpringBootTest(classes = {TestConfig.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Успешный вход")
    void login_ValidRequest() throws Exception {
        mockMvc.perform(post(AUTH_PATH + LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "email": "user1@user1.ru",
                                "password":"user1"
                                }
                                """))
                .andExpectAll(status().isOk());
    }

    @Test
    @DisplayName("Вход с неверными данными")
    void login_InvalidLoginInformation_ReturnsConflict() throws Exception {
        mockMvc.perform(post(AUTH_PATH + LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "email": "user",
                                "password":"user"
                                }
                                """))
                .andExpectAll(status().isConflict(),
                        content().json("""
                                {
                                "detail": "Неправильное имя пользователя или пароль"
                                }
                                """));
    }

    @Test
    @DisplayName("Успешная регистрация")
    void registration_ValidRequest_ReturnsUserResponse() throws Exception {
        mockMvc.perform(post(AUTH_PATH + REGISTRATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "firstName": "Вася",
                                "lastName": "Васин",
                                "email": "email@email.ru",
                                "password":"password"
                                }
                                """))
                .andExpectAll(status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/users/3"),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "id": 3,
                                "firstName": "Вася",
                                "lastName": "Васин",
                                "email": "email@email.ru"
                                }
                                """));
    }

    @Test
    @DisplayName("Регистрация пользователя, который уже существует")
    void registration_UserIsPresent_ReturnsConflict() throws Exception {
        mockMvc.perform(post(AUTH_PATH + REGISTRATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "firstName": "user1",
                                "lastName": "user1",
                                "email": "user1@user1.ru",
                                "password":"user1"
                                }
                                """))
                .andExpectAll(status().isBadRequest(),
                        content().json("""
                                {
                                "detail": "Такой пользователь уже существует"
                                }
                                """));
    }

    @Test
    @DisplayName("Регистрация с пустыми данными")
    void registration_UsernameAndPasswordIsBlank_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(AUTH_PATH + REGISTRATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                }
                                """))
                .andExpectAll(status().isBadRequest(),
                        content().json("""
                                {
                                "errors": [
                                     "lastName не может быть пустым или состоять только из пробелов.",
                                     "firstName не может быть пустым или состоять только из пробелов.",
                                     "password не может быть пустым или состоять только из пробелов.",
                                     "email не может быть пустым или состоять только из пробелов."
                                    ]
                                }"""));
    }

    @Test
    @DisplayName("Регистрация с коротким паролем")
    void registration_PasswordSizeSmall_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(AUTH_PATH + REGISTRATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "firstName": "user1",
                                "lastName": "user1",
                                "email": "user@user.ru",
                                "password":"u"
                                }
                                """))
                .andExpectAll(status().isBadRequest(),
                        content().json("""
                                {
                                "errors": [
                                      "Длина password должна составлять от 5 до 30 символов."
                                    ]
                                }"""));
    }
}