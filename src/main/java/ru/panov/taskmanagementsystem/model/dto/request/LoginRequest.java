package ru.panov.taskmanagementsystem.model.dto.request;

import lombok.Builder;

@Builder
public record LoginRequest(String email, String password) {
}