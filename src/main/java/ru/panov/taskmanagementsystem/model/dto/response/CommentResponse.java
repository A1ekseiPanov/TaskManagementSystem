package ru.panov.taskmanagementsystem.model.dto.response;

import lombok.Builder;

@Builder
public record CommentResponse(Long commentId,
                              String comment,
                              Long taskId,
                              Long authorId) {
}