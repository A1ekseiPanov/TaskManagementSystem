package ru.panov.taskmanagementsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.panov.taskmanagementsystem.config.TestConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.panov.taskmanagementsystem.util.PathConstants.TASKS_PATH;

@SpringBootTest(classes = {TestConfig.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
//@Sql(scripts = "/sql/data.sql", executionPhase = BEFORE_TEST_CLASS)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails(value = "user1@user1.ru")
    @DisplayName("Успешное создание задачи")
    void createTask_Success() throws Exception {
        mockMvc.perform(post(TASKS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "header": "Покупка автомобиля",
                                    "description":"На сделующей нгеде, преобрети автомобиль",
                                    "statusId": 1,
                                    "priority": 3
                                 }
                                """))
                .andExpectAll(status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost" + TASKS_PATH + "/4"),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "taskId": 4,
                                     "header": "Покупка автомобиля",
                                     "description": "На сделующей нгеде, преобрети автомобиль",
                                     "status": "В ожидании",
                                     "priority": "HIGH",
                                     "userId": 2
                                }
                                """));
    }

    @Test
    @DisplayName("Создание задачи, пользователь не авторизован")
    void createTask_UserIsNotAuthorized_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post(TASKS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "header": "Покупка автомобиля",
                                    "description":"На сделующей нгеде, преобрети автомобиль",
                                    "statusId": 1,
                                    "priority": 3
                                 }
                                """))
                .andExpectAll(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = "user1@user1.ru")
    @DisplayName("Создание задачи, ошибки валидации")
    void createTask_ValidatedEx_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(TASKS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "header": "",
                                    "description":"Н",
                                    "statusId": -1,
                                    "priority": -13
                                 }
                                """))
                .andExpectAll(status().isBadRequest(),
                        content().json("""
                                {
                                "errors": [
                                     "statusId не может быть меньше 1.",
                                     "priority не может быть меньше 1.",
                                     "header не может быть пустым или состоять только из пробелов.",
                                     "Минимальная длина description должна составлять 5 символов."   ]
                                }"""));
    }
}