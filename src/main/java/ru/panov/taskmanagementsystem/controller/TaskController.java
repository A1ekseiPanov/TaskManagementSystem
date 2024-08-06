package ru.panov.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.CommentRequest;
import ru.panov.taskmanagementsystem.model.dto.request.TaskRequest;
import ru.panov.taskmanagementsystem.model.dto.response.CommentResponse;
import ru.panov.taskmanagementsystem.model.dto.response.TaskResponse;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;
import ru.panov.taskmanagementsystem.service.CommentService;
import ru.panov.taskmanagementsystem.service.TaskService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final CommentService commentService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest,
                                                   BindingResult bindingResult,
                                                   @AuthenticationPrincipal User user,
                                                   UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            TaskResponse task = taskService.create(taskRequest, user.getId());
            return ResponseEntity.created(uriComponentsBuilder.
                            replacePath("tasks/{task_id}")
                            .build(Map.of("task_id", task.taskId())))
                    .body(task);
        }
    }

    @GetMapping
    public List<TaskResponse> gelAll(@RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                     @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return taskService.getAll(PageRequest.of(offset, limit));
    }

    @PutMapping(value = "/{task_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateTask(@PathVariable("task_id") Long taskId,
                                           @Valid @RequestBody TaskRequest task,
                                           BindingResult bindingResult,
                                           @AuthenticationPrincipal User user) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            taskService.update(taskId, task, user.getId());
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping(value = "/{task_id}/statuses/{status_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateTaskStatus(@PathVariable("task_id") Long taskId,
                                                 @PathVariable("status_id") Long statusId,
                                                 @AuthenticationPrincipal User user) {
        taskService.updateStatus(taskId, user.getId(), statusId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{task_id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("task_id") Long taskId,
                                           @AuthenticationPrincipal User user) {
        taskService.delete(taskId, user.getId());
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/{task_id}/performers")
    public List<UserResponse> getAllPerformersByTask(@PathVariable("task_id") Long taskId) {
        return taskService.getPerformerByTaskId(taskId);
    }

    @PutMapping(value = "/{task_id}/performers/{performer_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addPerformerToTask(@PathVariable("task_id") Long taskId,
                                             @PathVariable("performer_id") Long performerId,
                                             @AuthenticationPrincipal User user) {
        taskService.addPerformer(taskId, user.getId(), performerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{task_id}/comments")
    public List<CommentResponse> getAllCommentsByTask(@PathVariable("task_id") Long taskId,
                                                @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return commentService.commentsByTask(taskId, PageRequest.of(offset, limit));
    }

    @PostMapping(value = "/{task_id}/comments/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> addCommentToTask(@PathVariable("task_id") Long taskId,
                                                      @Valid @RequestBody CommentRequest commentRequest,
                                                      BindingResult bindingResult,
                                                      @AuthenticationPrincipal User user,
                                                      UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {

            CommentResponse commentResponse = commentService.add(commentRequest, taskId, user.getId());
            return ResponseEntity.created(uriComponentsBuilder.
                            replacePath("tasks/{task_id}/comments/{comment_Id}")
                            .build(Map.of("task_id", taskId,
                                    "comment_Id", commentResponse.commentId())))
                    .body(commentResponse);
        }
    }

    @DeleteMapping("/{task_id}/comments/{comment_id}")
    public ResponseEntity<Void> deleteCommentFromTask(@PathVariable("task_id") Long taskId,
                                              @PathVariable("comment_id") Long commentId,
                                              @AuthenticationPrincipal User user) {
        commentService.delete(commentId, taskId, user.getId());
        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping(value = "/{task_id}/comments/{comment_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateCommentFromTask(@PathVariable("task_id") Long taskId,
                                              @PathVariable("comment_id") Long commentId,
                                              @Valid @RequestBody CommentRequest commentRequest,
                                              BindingResult bindingResult,
                                              @AuthenticationPrincipal User user) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            commentService.update(commentId, commentRequest, taskId, user.getId());
            return ResponseEntity.noContent().build();
        }
    }
}