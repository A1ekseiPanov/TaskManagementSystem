package ru.panov.taskmanagementsystem.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.dto.request.TaskRequest;
import ru.panov.taskmanagementsystem.model.dto.response.TaskResponse;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;

import java.util.List;

public interface TaskService {
    TaskResponse create(TaskRequest taskRequest, Long userId);

    Task getTaskByIdAndUserId(Long taskId, Long userId);

    Task getTaskById(Long taskId);

    TaskResponse addPerformer(Long taskId, Long userId, Long performId);

    List<UserResponse> getPerformerByTaskId(Long taskId);

    void update(Long taskId, TaskRequest taskRequest, Long userId);

    void updateStatus(Long taskId, Long userId, Long statusId);

    List<TaskResponse> getAll(Specification<Task> specification, Pageable pageable);

    void delete(Long taskId, Long userId);
}
