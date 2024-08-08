package ru.panov.taskmanagementsystem.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record StatusRequest(
        @NotBlank(message = "status не может быть пустым или состоять только из пробелов.")
        @Schema(description = "Название статуса задачи.")
        String status) {
}