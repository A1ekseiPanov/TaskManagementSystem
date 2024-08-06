package ru.panov.taskmanagementsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.panov.taskmanagementsystem.model.Priority;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.dto.request.TaskRequest;
import ru.panov.taskmanagementsystem.model.dto.response.TaskResponse;
import ru.panov.taskmanagementsystem.service.StatusService;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TaskMapper {
    @Autowired
    private StatusService statusService;

    public Task requestToEntity(TaskRequest taskRequest) {
        return Task.builder()
                .priority(Priority.values()[taskRequest.priority() - 1])
                .description(taskRequest.description())
                .header(taskRequest.header())
                .status(statusService.get(taskRequest.statusId()))
                .build();
    }

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "status", source = "task.status.status")
    @Mapping(target = "userId", source = "task.user.id")
    public abstract TaskResponse entityToResponse(Task task);
    public abstract List<TaskResponse> listEntityToListResponse(List<Task> task);
}