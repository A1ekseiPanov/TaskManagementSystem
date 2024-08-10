package ru.panov.taskmanagementsystem.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.panov.taskmanagementsystem.exception.NotFoundException;
import ru.panov.taskmanagementsystem.mapper.CommentMapper;
import ru.panov.taskmanagementsystem.model.Comment;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.CommentRequest;
import ru.panov.taskmanagementsystem.model.dto.response.CommentResponse;
import ru.panov.taskmanagementsystem.reposirory.CommentRepository;
import ru.panov.taskmanagementsystem.service.TaskService;
import ru.panov.taskmanagementsystem.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Mock
    private CommentMapper commentMapper;

    @Test
    @DisplayName("Добавление коментаря к задаче, успешно")
    void addComment_Success() {
        Long taskId = 1L;
        Long userId = 2L;

        CommentRequest commentRequest = CommentRequest.builder()
                .comment("коментарий")
                .build();

        Task task = Task.builder().performers(new ArrayList<>()).build();
        User user = User.builder().build();
        task.setId(taskId);
        user.setId(userId);
        task.setUser(user);

        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(userService.getById(userId)).thenReturn(user);

        CommentResponse commentResponse = commentService.add(commentRequest, taskId, userId);

        verify(taskService, times(1)).getTaskById(taskId);
        verify(userService, times(1)).getById(userId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Обновление коментаря к задаче, успешно")
    void updateComment_Success() {
        Long commentId = 1L;
        Long taskId = 1L;
        Long userId = 2L;
        CommentRequest commentRequest = CommentRequest.builder()
                .comment("коментарий")
                .build();

        Task task = Task.builder().performers(new ArrayList<>()).build();
        User user = User.builder().build();
        task.setId(taskId);
        user.setId(userId);
        task.setUser(user);

        Comment comment = Comment.builder()
                .author(user)
                .task(task)
                .comment("comment")
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.update(commentId, commentRequest, taskId, userId);

        verify(commentRepository, times(1)).save(comment);
        verify(commentRepository, times(1)).findById(commentId);
    }


    @Test
    @DisplayName("Удаление коментаря к задаче, успешно")
    void testDeleteComment_Success() {
        Long commentId = 1L;
        Long taskId = 1L;
        Long userId = 2L;

        Task task = Task.builder().performers(new ArrayList<>()).build();
        User user = User.builder().build();
        task.setId(taskId);
        user.setId(userId);

        Comment comment = Comment.builder()
                .author(user)
                .task(task)
                .build();
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));


        commentService.delete(commentId, taskId, userId);


        verify(commentRepository).deleteById(commentId);
    }

    @Test
    @DisplayName("Получение списка коментариев к задаче")
    void commentsByTask_Success() {
        Long taskId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Comment comment = Comment.builder().build();
        CommentResponse response = CommentResponse.builder()
                .taskId(taskId)
                .comment("k")
                .build();

        when(commentRepository.findByTask_Id(taskId, pageable)).thenReturn(new PageImpl<>(List.of(comment)));
        when(commentMapper.listCommentToListResponseEntity(anyList())).thenReturn(List.of(response));


        List<CommentResponse> result = commentService.commentsByTask(taskId, pageable);

        assertThat(result.size()).isEqualTo(1);
        assertThat(response).isEqualTo(result.get(0));
    }

    @Test
    @DisplayName("Получение коментария по его id, успешно")
    void getComment_Success() {
        Long commentId = 1L;

        Comment comment = Comment.builder().build();
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.getComment(commentId);

        assertThat(result).isEqualTo(comment);
    }

    @Test
    @DisplayName("Получение коментария по его id, коментария не существует")
    void getComment_NotFound() {
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getComment(commentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Коментария с Id:%s не существует".formatted(commentId));
    }
}