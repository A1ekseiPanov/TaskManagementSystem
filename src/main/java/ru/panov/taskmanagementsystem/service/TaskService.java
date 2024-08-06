package ru.panov.taskmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.panov.taskmanagementsystem.exception.DuplicateException;
import ru.panov.taskmanagementsystem.exception.NotFoundException;
import ru.panov.taskmanagementsystem.mapper.TaskMapper;
import ru.panov.taskmanagementsystem.mapper.UserMapper;
import ru.panov.taskmanagementsystem.model.Status;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.TaskRequest;
import ru.panov.taskmanagementsystem.model.dto.response.TaskResponse;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;
import ru.panov.taskmanagementsystem.reposirory.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final StatusService statusService;
    private final UserMapper userMapper;

    public TaskResponse create(TaskRequest taskRequest, Long userId) {
        User user = userService.getById(userId);
        Task task = taskMapper.requestToEntity(taskRequest);
        task.setUser(user);
        return taskMapper.entityToResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public Task getTask(Long taskId, Long userId) {
        return taskRepository.findByIdAndUser_Id(taskId, userId)
                .orElseThrow(() ->
                        new NotFoundException("Задачи с id:%s у пользовтателя с id:%s не существует"
                                .formatted(taskId, userId)));
    }

    public TaskResponse addPerformer(Long taskId, Long userId, Long performId) {
        Task task = getTask(taskId, userId);
        List<User> users = task.getPerformers();
        User performUser = userService.getById(performId);
        if (users.contains(performUser)) {
            throw new DuplicateException("Исполнитель с id:%s уже добавлен к задаче с id:%s"
                    .formatted(performId, taskId));
        }
        users.add(performUser);
        return taskMapper.entityToResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getPerformerByTaskId(Long taskId) {
        return userMapper.listUserToListResponseEntity(
                taskRepository.findAllPerformersByTaskId(taskId));
    }

    public void update(Long taskId, TaskRequest taskRequest, Long userId) {
        Task task = getTask(taskId, userId);
        Task updatedTask = taskMapper.requestToEntity(taskRequest);
        updatedTask.setId(task.getId());
        updatedTask.setUpdated(LocalDateTime.now());
        taskRepository.save(updatedTask);
    }

    public Task updateStatus(Long taskId, Long userId, Long statusId) {
        Task task = getTask(taskId, userId);
        if (task.getPerformers().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new AccessDeniedException(
                    "У пользователя c id:%s нет прав на изменение статуса задачи c id:%s"
                            .formatted(userId, taskId));
        }
        Status status = statusService.get(statusId);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAll(Pageable pageable) {
        return taskMapper.listEntityToListResponse(taskRepository.findAll(pageable).getContent());
    }

    public void delete(Long taskId, Long userId) {
        Task task = getTask(taskId, userId);
        taskRepository.delete(task);
    }
}