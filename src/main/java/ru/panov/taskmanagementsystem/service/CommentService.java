package ru.panov.taskmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.panov.taskmanagementsystem.exception.InputDataConflictException;
import ru.panov.taskmanagementsystem.exception.NotFoundException;
import ru.panov.taskmanagementsystem.mapper.CommentMapper;
import ru.panov.taskmanagementsystem.model.Comment;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.CommentRequest;
import ru.panov.taskmanagementsystem.model.dto.response.CommentResponse;
import ru.panov.taskmanagementsystem.reposirory.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final TaskService taskService;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse add(CommentRequest commentRequest,Long taskId, Long userId) {
        Task task = taskService.getTask(taskId, userId);
        User author = userService.getById(userId);
        Comment comment = Comment.builder()
                .author(author)
                .comment(commentRequest.comment())
                .task(task)
                .build();
        return commentMapper.commentToResponseEntity(commentRepository.save(comment));
    }

    @Transactional
    public void update(Long commentId, CommentRequest commentRequest,Long taskId, Long userId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            if (comment.get().getTask().getId().equals(taskId)
                    && comment.get().getAuthor().getId().equals(userId)) {
                comment.get().setComment(commentRequest.comment());
            } else {
                throw new InputDataConflictException("Не совпадает юзер или задача");
            }
        } else {
            throw new NotFoundException("Коментарий с id:%s не найден".formatted(commentId));
        }
    }

    @Transactional
    public void delete(Long commentId, Long taskId, Long userId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            if (comment.get().getTask().getId().equals(taskId)
                    && comment.get().getAuthor().getId().equals(userId)) {
                commentRepository.deleteById(commentId);
            } else {
                throw new InputDataConflictException("Не совпадает юзер или задача");
            }
        } else {
            throw new NotFoundException("Коментарий с id:%s не найден".formatted(commentId));
        }
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> commentsByTask(Long taskId, Pageable pageable) {
        return commentMapper.listCommentToListResponseEntity(
                commentRepository.findByTask_Id(taskId, pageable).getContent());
    }
}