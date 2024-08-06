package ru.panov.taskmanagementsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "header не может быть пустым или состоять только из пробелов.")
        String header,
        @NotBlank(message = "description не может быть пустым или состоять только из пробелов.")
        @Size(min = 5, message = "Минимальная длина description должна составлять 5 символов.")
        String description,
        @Positive(message = "statusId не может быть меньше 1.")
        @NotNull(message = "tatusId не может быть null.")
        Long statusId,
        @Positive(message = "priority не может быть меньше 1.")
        @NotNull(message = "priority не может быть null.")
        Integer priority) {
}