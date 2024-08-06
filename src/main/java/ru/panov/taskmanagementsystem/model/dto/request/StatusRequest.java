package ru.panov.taskmanagementsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record StatusRequest(
        @NotBlank(message = "status не может быть пустым или состоять только из пробелов.")
        String status) {
}