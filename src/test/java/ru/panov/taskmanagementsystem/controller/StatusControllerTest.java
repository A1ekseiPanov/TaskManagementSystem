package ru.panov.taskmanagementsystem.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.panov.taskmanagementsystem.config.TestConfig;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.panov.taskmanagementsystem.util.PathConstants.STATUSES_PATH;

@SpringBootTest(classes = {TestConfig.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
@Sql(scripts = "/sql/data.sql", executionPhase = BEFORE_TEST_CLASS)
class StatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails(value = "user1@user1.ru")
    @DisplayName("Получение всех статусов задач")
    void getAllStatuses() throws Exception {
        mockMvc.perform(get(STATUSES_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  {
                                    "statusId": 1,
                                    "status": "В ожидании"
                                  },
                                  {
                                    "statusId": 2,
                                    "status": "В процессе"
                                  },
                                  {
                                    "statusId": 3,
                                    "status": "Завершено"
                                  }
                                ]"""));
    }

    @Test
    @WithUserDetails(value = "admin@admin.ru")
    @DisplayName("Создание статуса задач")
    void crateStatus_Success() throws Exception {
        mockMvc.perform(post(STATUSES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "status": "string"
                                }
                                """))
                .andExpectAll(status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost" + STATUSES_PATH + "/4"),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                   "statusId": 4,
                                   "status": "string"
                                 }
                                """));
    }

    @Test
    @WithUserDetails(value = "user1@user1.ru")
    @DisplayName("Создание статуса задач, не подходящая роль пользователя")
    void crateStatus_InappropriateRole() throws Exception {
        mockMvc.perform(post(STATUSES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "status": "string"
                                }
                                """))
                .andExpectAll(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = "admin@admin.ru")
    @DisplayName("Создание статуса задач, данные не валидны")
    void crateStatus_NotValidateData() throws Exception {
        mockMvc.perform(post(STATUSES_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "status": ""
                                }
                                """))
                .andExpectAll(status().isBadRequest(),
                        content().json("""
                                {
                                "errors": [
                                     "status не может быть пустым или состоять только из пробелов."
                                          ]
                                }"""));
    }

    @Test
    @WithUserDetails(value = "admin@admin.ru")
    @DisplayName("Успешное удаление статуса")
    void deleteStatus_Success() throws Exception {
        mockMvc.perform(delete(STATUSES_PATH + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = "admin@admin.ru")
    @DisplayName("Удаление тренировки, id невалиден")
    void deleteStatus_idIsNotValid() throws Exception {
        mockMvc.perform(delete(STATUSES_PATH + "/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isNotFound(),
                        content().json("""
                                {
                                "detail": "Статус с id:5 не найден"
                                }
                                """));
    }

    @Test
    @WithUserDetails(value = "admin@admin.ru")
    @DisplayName("Успешное удаление статуса")
    void updateStatus_Success() throws Exception {
        mockMvc.perform(put(STATUSES_PATH + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "Продвигаемся"
                                }
                                """))
                .andExpect(status().isNoContent());
    }
}