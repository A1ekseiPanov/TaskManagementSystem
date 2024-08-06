package ru.panov.taskmanagementsystem.model.dto.response;

public record CommentResponse(Long commentId,
                              String comment,
                              Long taskId,
                              Long authorId) {
}