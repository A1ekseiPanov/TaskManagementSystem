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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final TaskService taskService;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse add(CommentRequest commentRequest, Long taskId, Long userId) {
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
    public void update(Long commentId, CommentRequest commentRequest, Long taskId, Long userId) {
        Comment comment = getComment(commentId);
        if (comment.getTask().getId().equals(taskId)
                && comment.getAuthor().getId().equals(userId)) {
            comment.setComment(commentRequest.comment());
            comment.setUpdated(LocalDateTime.now());
            commentRepository.save(comment);
        } else {
            throw new InputDataConflictException(
                    ("Для изменения коментария с id:%s выбрана не та задача с id:%s," +
                            " либо это не коментарий польвоателя с id:%s")
                            .formatted(commentId, taskId, userId));
        }
    }


    @Transactional
    public void delete(Long commentId, Long taskId, Long userId) {
        Comment comment = getComment(commentId);
        if (comment.getTask().getId().equals(taskId)
                && comment.getAuthor().getId().equals(userId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new InputDataConflictException(
                    ("Для удаления коментария с id:%s выбрана не та задача с id:%s," +
                            " либо это не коментарий польвоателя с id:%s")
                            .formatted(commentId, taskId, userId));
        }
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> commentsByTask(Long taskId, Pageable pageable) {
        return commentMapper.listCommentToListResponseEntity(
                commentRepository.findByTask_Id(taskId, pageable).getContent());
    }

    @Transactional(readOnly = true)
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Коментария с Id:%s не существует"
                        .formatted(commentId)));
    }
}