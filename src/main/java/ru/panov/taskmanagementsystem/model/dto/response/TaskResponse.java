package ru.panov.taskmanagementsystem.model.dto.response;

import lombok.Builder;
import ru.panov.taskmanagementsystem.model.Comment;

import java.util.List;

@Builder
public record TaskResponse(Long taskId,
                           String header,
                           String description,
                           String status,
                           String priority,
                           Long userId,
                           List<UserResponse> performers,
                           List<Comment> comments) {
}