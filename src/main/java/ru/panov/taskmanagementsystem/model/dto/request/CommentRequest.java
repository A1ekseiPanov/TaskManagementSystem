package ru.panov.taskmanagementsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
        @NotBlank(message = "comment не может быть пустым или состоять только из пробелов.")
        String comment) {
}