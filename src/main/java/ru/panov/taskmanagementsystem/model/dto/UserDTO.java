package ru.panov.taskmanagementsystem.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank(message = "firstName не может быть пустыми или состоять только из пробелов.")
        @Size(min = 5, max = 30, message = "Длина firstName должна составлять от 5 до 30 символов.")
        String firstName,
        @NotBlank(message = "lastName не может быть пустыми или состоять только из пробелов.")
        @Size(min = 5, max = 30, message = "Длина lastName должна составлять от 5 до 30 символов.")
        String lastName,
        @Email
        String email,
        @NotBlank(message = "password не может быть пустыми или состоять только из пробелов.")
        @Size(min = 5, max = 30, message = "Длина password должна составлять от 5 до 30 символов.")
        String password) {
}