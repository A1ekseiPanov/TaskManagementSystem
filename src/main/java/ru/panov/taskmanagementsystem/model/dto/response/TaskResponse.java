package ru.panov.taskmanagementsystem.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponse(Long taskId,
                           String header,
                           String description,
                           String status,
                           String priority,
                           Long userId,
                           List<UserResponse> performers,
                           List<CommentResponse> comments) {
}