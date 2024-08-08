package ru.panov.taskmanagementsystem.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CommentRequest(
        @NotBlank(message = "comment не может быть пустым или состоять только из пробелов.")
        @Schema(description = "Текст коментария к задаче.")
        String comment) {
}