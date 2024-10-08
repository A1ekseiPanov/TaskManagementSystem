package ru.panov.taskmanagementsystem.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRequest(
        @NotBlank(message = "firstName не может быть пустым или состоять только из пробелов.")
        String firstName,
        @NotBlank(message = "lastName не может быть пустым или состоять только из пробелов.")
        String lastName,
        @Email
        @NotBlank(message = "email не может быть пустым или состоять только из пробелов.")
        String email,
        @NotBlank(message = "password не может быть пустым или состоять только из пробелов.")
        @Size(min = 5, max = 30, message = "Длина password должна составлять от 5 до 30 символов.")
        String password) {
}