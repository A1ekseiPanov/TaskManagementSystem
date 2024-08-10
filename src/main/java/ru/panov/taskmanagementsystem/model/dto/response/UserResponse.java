package ru.panov.taskmanagementsystem.model.dto.response;

public record UserResponse(Long id,
                           String firstName,
                           String lastName,
                           String email) {
}