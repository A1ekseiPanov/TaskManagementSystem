package ru.panov.taskmanagementsystem.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Конфигурация тестового окружения для работы с контейнером PostgreSQL.
 * Эта конфигурация используется для создания и управления контейнером базы данных PostgreSQL,
 * который будет использоваться в тестах.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestConfig {
    /**
     * Создает и настраивает контейнер PostgreSQL для тестирования.
     * Контейнер будет автоматически запущен перед выполнением тестов и остановлен после их завершения.
     *
     * @param registry Регистратор динамических свойств для контейнера.
     * @return Настроенный контейнер PostgreSQL.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer(DynamicPropertyRegistry registry) {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16-alpine");
        return container;
    }
}