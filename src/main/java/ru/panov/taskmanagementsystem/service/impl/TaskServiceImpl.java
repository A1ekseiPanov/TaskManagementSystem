package ru.panov.taskmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.panov.taskmanagementsystem.exception.DuplicateException;
import ru.panov.taskmanagementsystem.exception.InputDataConflictException;
import ru.panov.taskmanagementsystem.exception.NotFoundException;
import ru.panov.taskmanagementsystem.mapper.TaskMapper;
import ru.panov.taskmanagementsystem.mapper.UserMapper;
import ru.panov.taskmanagementsystem.model.Priority;
import ru.panov.taskmanagementsystem.model.Status;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.TaskRequest;
import ru.panov.taskmanagementsystem.model.dto.response.TaskResponse;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;
import ru.panov.taskmanagementsystem.reposirory.TaskRepository;
import ru.panov.taskmanagementsystem.service.StatusService;
import ru.panov.taskmanagementsystem.service.TaskService;
import ru.panov.taskmanagementsystem.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final StatusService statusService;
    private final UserMapper userMapper;

    @Override
    public TaskResponse create(TaskRequest taskRequest, Long userId) {
        checkUniq(taskRequest);

        User user = userService.getById(userId);
        Task task = Task.builder()
                .header(taskRequest.header())
                .description(taskRequest.description())
                .status(statusService.get(taskRequest.statusId()))
                .priority(Priority.values()[taskRequest.priority() - 1])
                .user(user)
                .build();
        return taskMapper.entityToResponse(taskRepository.save(task));
    }


    @Override
    @Transactional(readOnly = true)
    public Task getTaskByIdAndUserId(Long taskId, Long userId) {
        return taskRepository.findByIdAndUser_Id(taskId, userId)
                .orElseThrow(() ->
                        new NotFoundException("Задачи с id:%s у пользовтателя с id:%s не существует"
                                .formatted(taskId, userId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() ->
                        new NotFoundException("Задачи с id:%s не существует"
                                .formatted(taskId)));
    }

    @Override
    public TaskResponse addPerformer(Long taskId, Long userId, Long performId) {
        Task task = getTaskByIdAndUserId(taskId, userId);
        List<User> users = task.getPerformers();
        User performUser = userService.getById(performId);
        if (users.contains(performUser)) {
            throw new DuplicateException("Исполнитель с id:%s уже добавлен к задаче с id:%s"
                    .formatted(performId, taskId));
        }
        users.add(performUser);
        return taskMapper.entityToResponse(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getPerformerByTaskId(Long taskId) {
        return userMapper.listUserToListResponseEntity(
                taskRepository.findAllPerformersByTaskId(taskId));
    }

    @Override
    public void update(Long taskId, TaskRequest taskRequest, Long userId) {
        Task task = getTaskByIdAndUserId(taskId, userId);
        task.setHeader(taskRequest.header());
        task.setDescription(taskRequest.description());
        task.setStatus(statusService.get(taskRequest.statusId()));
        task.setPriority(Priority.values()[taskRequest.priority() - 1]);
        task.setUpdated(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Override
    public void updateStatus(Long taskId, Long userId, Long statusId) {
        Task task = getTaskById(taskId);
        if (task.getPerformers().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new InputDataConflictException(
                    ("Пользователь c id:%s не может изменять статус задачи c id:%s, " +
                            "т.к. не является исполнитиелем данной задачи")
                            .formatted(userId, taskId));
        }
        Status status = statusService.get(statusId);
        task.setStatus(status);
        taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAll(Specification<Task> specification, Pageable pageable) {
        return taskMapper.listEntityToListResponse(taskRepository.findAll(specification, pageable).getContent());
    }

    @Override
    public void delete(Long taskId, Long userId) {
        Task task = getTaskByIdAndUserId(taskId, userId);
        taskRepository.delete(task);
    }

    private void checkUniq(TaskRequest taskRequest) {
        Optional<Task> taskByHeader = taskRepository.findByHeader(taskRequest.header());
        if (taskByHeader.isPresent()) {
            throw new DuplicateException("Задача с заголовком: %s уже существует"
                    .formatted(taskRequest.header()));
        }
    }
}