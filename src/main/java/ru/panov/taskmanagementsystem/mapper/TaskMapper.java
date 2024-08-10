package ru.panov.taskmanagementsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.dto.response.TaskResponse;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface TaskMapper {
    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "status", source = "status.status")
    @Mapping(target = "userId", source = "user.id")
    TaskResponse entityToResponse(Task task);

    List<TaskResponse> listEntityToListResponse(List<Task> task);
}