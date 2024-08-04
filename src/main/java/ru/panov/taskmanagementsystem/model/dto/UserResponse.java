package ru.panov.taskmanagementsystem.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.panov.taskmanagementsystem.model.Role;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(Long id,
                           String username,
                           Set<Role> roles,
                           @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
                           LocalDateTime created) {
}