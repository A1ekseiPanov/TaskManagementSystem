package ru.panov.taskmanagementsystem.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;


@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Task Management System API",
                description = """
                        Task Management System API
                        <p><b>Тестовые данные:</b><br>
                        - admin: admin@admin.ru / admin<br>
                        - user: user1@user1.ru / user1<br>
                        """,
                version = "1.0.0",
                contact = @Contact(
                        name = "Aleksey Panov",
                        email = "evil199315@yandex.ru"
                )
        ),
        security = @SecurityRequirement(name = "Bearer Authentication")
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerOpenAPIConfig {
}