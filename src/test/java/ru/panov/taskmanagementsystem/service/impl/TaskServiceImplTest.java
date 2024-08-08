package ru.panov.taskmanagementsystem.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.panov.taskmanagementsystem.exception.DuplicateException;
import ru.panov.taskmanagementsystem.exception.NotFoundException;
import ru.panov.taskmanagementsystem.mapper.TaskMapper;
import ru.panov.taskmanagementsystem.mapper.UserMapper;
import ru.panov.taskmanagementsystem.model.Status;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.TaskRequest;
import ru.panov.taskmanagementsystem.reposirory.TaskRepository;
import ru.panov.taskmanagementsystem.service.StatusService;
import ru.panov.taskmanagementsystem.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private UserService userService;
    @Mock
    private StatusService statusService;
    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Сорханение успешно новой задачи у авторезированного пользователя")
    void createTask_Success() {
        Long userId = 1L;
        TaskRequest taskRequest = TaskRequest.builder()
                .header("заголовок")
                .description("описание")
                .priority(1)
                .statusId(1L)
                .build();

        when(taskRepository.findByHeader(taskRequest.header())).thenReturn(Optional.empty());
        when(userService.getById(userId)).thenReturn(User.builder().build());

        taskService.create(taskRequest, userId);

        verify(taskRepository, times(1)).findByHeader(taskRequest.header());
        verify(userService, times(1)).getById(userId);
        verify(taskRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("Сорханение новой задачи не удалось, задача с таким заголовко уже сущесвует")
    void createTask_AlreadyExistByTitleDuplicateException() {
        Long userId = 1L;
        TaskRequest taskRequest = TaskRequest.builder()
                .header("заголовок")
                .description("описание")
                .priority(1)
                .statusId(1L)
                .build();

        when(taskRepository.findByHeader(taskRequest.header())).thenReturn(Optional.of(Task.builder().build()));

        assertThatThrownBy(() -> taskService.create(taskRequest, userId))
                .isInstanceOf(DuplicateException.class)
                .hasMessage("Задача с заголовком: %s уже существует"
                        .formatted(taskRequest.header()));
    }


    @Test
    @DisplayName("Получение задачи по id задачи и пользователя, успешно")
    void getTaskByIdAndUserId_Success() {
        Long taskId = 1L;
        Long userId = 1L;
        Task expectedTask = Task.builder()
                .header("заголовок")
                .description("описание")
                .build();

        when(taskRepository.findByIdAndUser_Id(taskId, userId)).thenReturn(Optional.of(expectedTask));

        Task actualTask = taskService.getTaskByIdAndUserId(taskId, userId);

        verify(taskRepository, times(1)).findByIdAndUser_Id(taskId, userId);
        assertThat(actualTask).isEqualTo(expectedTask);
    }

    @Test
    @DisplayName("Получение задачи по id задачи и пользователя, задача у пользователя не найдена")
    void getTaskByIdAndUserId_UserTaskWasNotFound() {
        Long taskId = 1L;
        Long userId = 1L;

        when(taskRepository.findByIdAndUser_Id(taskId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskByIdAndUserId(taskId, userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Задачи с id:%s у пользовтателя с id:%s не существует"
                        .formatted(taskId, userId));
    }

    @Test
    @DisplayName("Получение задачи по id задачи, успешно")
    void getTaskById_Success() {
        Long taskId = 1L;
        Task expectedTask = Task.builder()
                .header("заголовок")
                .description("описание")
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(expectedTask));

        Task actualTask = taskService.getTaskById(taskId);

        verify(taskRepository, times(1)).findById(taskId);
        assertThat(actualTask).isEqualTo(expectedTask);
    }

    @Test
    @DisplayName("Получение задачи по id задачи, задачи не сущесвует")
    void getTaskById_TaskNotFound() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Задачи с id:%s не существует"
                        .formatted(taskId));
    }

    @Test
    @DisplayName("Добавление исполнителя к задаче, успешно")
    void addPerformer_Success() {
        Long taskId = 1L;
        Long userId = 1L;
        Long performId = 2L;

        Task task = Task.builder()
                .header("заголовок")
                .description("описание").build();

        User user = User.builder().build();
        user.setId(userId);

        User performer = User.builder().build();
        performer.setId(performId);

        task.setPerformers(new ArrayList<>());

        when(taskRepository.findByIdAndUser_Id(taskId, userId)).thenReturn(Optional.of(task));
        when(userService.getById(performId)).thenReturn(performer);

        taskService.addPerformer(taskId, userId, performId);

        verify(taskRepository, times(1)).findByIdAndUser_Id(taskId, userId);
        verify(userService, times(1)).getById(performId);
        verify(taskRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("Добавление исполнителя к задаче, исполнитель уже был добавлен к задаче")
    void addPerformer_PerformerHasAlreadyBeenAdded() {
        Long taskId = 1L;
        Long userId = 1L;
        Long performId = 2L;

        Task task = Task.builder()
                .header("заголовок")
                .description("описание").build();

        User user = User.builder().build();
        user.setId(userId);

        User performer = User.builder().build();
        performer.setId(performId);

        task.setPerformers(new ArrayList<>(Arrays.asList(performer)));

        when(taskRepository.findByIdAndUser_Id(taskId, userId)).thenReturn(Optional.of(task));
        when(userService.getById(performId)).thenReturn(performer);


        assertThatThrownBy(() -> taskService.addPerformer(taskId, userId, performId))
                .isInstanceOf(DuplicateException.class)
                .hasMessage("Исполнитель с id:%s уже добавлен к задаче с id:%s"
                        .formatted(performId, taskId));
    }

    @Test
    @DisplayName("Получение исполнительей по id задачи, успешно")
    void getPerformerByTaskId_Success() {
        Long taskId = 1L;
        when(taskRepository.findAllPerformersByTaskId(taskId)).thenReturn(new ArrayList<>());

        taskService.getPerformerByTaskId(taskId);

        verify(userMapper,times(1)).listUserToListResponseEntity(anyList());
        verify(taskRepository,times(1)).findAllPerformersByTaskId(taskId);
    }

    @Test
    @DisplayName("Обновление задачи, успешно")
    void update_Success() {
        Long taskId = 1L;
        Long userId = 1L;
        TaskRequest taskRequest = TaskRequest.builder().statusId(1L).priority(1).build();
        when(taskRepository.findByIdAndUser_Id(taskId, userId))
                .thenReturn(Optional.of(Task.builder().build()));
        when(statusService.get(taskRequest.statusId())).thenReturn(Status.builder().build());

        taskService.update(taskId,taskRequest,userId);

        verify(taskRepository,times(1)).findByIdAndUser_Id(taskId, userId);
        verify(statusService,times(1)).get(taskRequest.statusId());
        verify(taskRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("Обновление статуса задачи, успешно")
    void updateStatus_Success() {
        Long taskId = 1L;
        Long userId = 1L;
        Long statusId = 1L;

        User user = User.builder().build();
        user.setId(userId);

        Status status = Status.builder().build();
        status.setId(statusId);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(Task.builder()
                        .performers(new ArrayList<>(Arrays.asList(user))).build()));
        when(statusService.get(statusId)).thenReturn(status);

        taskService.updateStatus(taskId,userId,statusId);

        verify(taskRepository,times(1)).findById(taskId);
        verify(statusService,times(1)).get(status.getId());
        verify(taskRepository,times(1)).save(any());
    }

    @Test
    @DisplayName("Удаление задачи, успешно")
    void delete() {
        Long taskId = 1L;
        Long userId = 1L;

        when(taskRepository.findByIdAndUser_Id(taskId, userId))
                .thenReturn(Optional.of(Task.builder().build()));

        taskService.delete(taskId,userId);

        verify(taskRepository,times(1)).findByIdAndUser_Id(taskId, userId);
        verify(taskRepository,times(1)).delete(any(Task.class));
    }
}