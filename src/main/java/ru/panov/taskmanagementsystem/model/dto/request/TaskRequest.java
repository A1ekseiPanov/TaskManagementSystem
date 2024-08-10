package ru.panov.taskmanagementsystem.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TaskRequest(
        @NotBlank(message = "header не может быть пустым или состоять только из пробелов.")
        String header,
        @NotBlank(message = "description не может быть пустым или состоять только из пробелов.")
        @Size(min = 5, message = "Минимальная длина description должна составлять 5 символов.")
        String description,
        @Positive(message = "statusId не может быть меньше 1.")
        @NotNull(message = "tatusId не может быть null.")
        @Schema(description = "id статуса задачи")
        Long statusId,
        @Positive(message = "priority не может быть меньше 1.")
        @NotNull(message = "priority не может быть null.")
        @Schema(description = "Приоритет задачи(. )1-низкий, 2-средний, 3-высокий).")
        Integer priority) {
}