package ru.panov.taskmanagementsystem.model.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TaskResponse(Long taskId,
                           String header,
                           String description,
                           String status,
                           String priority,
                           Long userId,
                           List<UserResponse> performers,
                           List<CommentResponse> comments) {
}