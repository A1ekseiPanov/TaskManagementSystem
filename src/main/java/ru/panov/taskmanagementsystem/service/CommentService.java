package ru.panov.taskmanagementsystem.service;

import org.springframework.data.domain.Pageable;
import ru.panov.taskmanagementsystem.model.Comment;
import ru.panov.taskmanagementsystem.model.dto.request.CommentRequest;
import ru.panov.taskmanagementsystem.model.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse add(CommentRequest commentRequest, Long taskId, Long userId);

    void update(Long commentId, CommentRequest commentRequest, Long taskId, Long userId);

    void delete(Long commentId, Long taskId, Long userId);

    List<CommentResponse> commentsByTask(Long taskId, Pageable pageable);

    Comment getComment(Long commentId);
}