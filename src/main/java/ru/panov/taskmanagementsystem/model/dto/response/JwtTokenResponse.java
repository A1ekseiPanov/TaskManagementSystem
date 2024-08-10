package ru.panov.taskmanagementsystem.model.dto.response;

import lombok.*;

import java.io.Serializable;

@Builder
public record JwtTokenResponse(String token) implements Serializable {
}