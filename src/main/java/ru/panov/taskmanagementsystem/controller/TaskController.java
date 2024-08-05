package ru.panov.taskmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.CommentRequest;
import ru.panov.taskmanagementsystem.model.dto.request.StatusRequest;
import ru.panov.taskmanagementsystem.model.dto.request.TaskRequest;
import ru.panov.taskmanagementsystem.model.dto.response.CommentResponse;
import ru.panov.taskmanagementsystem.model.dto.response.StatusResponse;
import ru.panov.taskmanagementsystem.model.dto.response.TaskResponse;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;
import ru.panov.taskmanagementsystem.service.CommentService;
import ru.panov.taskmanagementsystem.service.StatusService;
import ru.panov.taskmanagementsystem.service.TaskService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final StatusService statusService;
    private final CommentService commentService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest taskRequest,
                                                   @AuthenticationPrincipal User user,
                                                   UriComponentsBuilder uriComponentsBuilder) {
        TaskResponse task = taskService.create(taskRequest, user.getId());
        return ResponseEntity.created(uriComponentsBuilder.
                        replacePath("tasks/{task_id}")
                        .build(Map.of("task_id", task.taskId())))
                .body(task);
    }

    @GetMapping
    public List<TaskResponse> gelAll(@RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                     @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return taskService.getAll(PageRequest.of(offset, limit));
    }

    @PutMapping(value = "/{task_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateTask(@PathVariable("task_id") Long taskId,
                                           @RequestBody TaskRequest task,
                                           @AuthenticationPrincipal User user) {

        taskService.update(taskId, task, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{task_id}/status/{status_id}", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping("/statuses")
    public List<StatusResponse> getAllStatuses() {
        return statusService.statuses();
    }

    @PostMapping("/statuses")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StatusResponse> crateStatus(@RequestBody StatusRequest statusRequest,
                                                      UriComponentsBuilder uriComponentsBuilder) {
        StatusResponse statusResponse = statusService.create(statusRequest);

        return ResponseEntity.created(uriComponentsBuilder.
                        replacePath("tasks/statuses/{status_id}")
                        .build(Map.of("status_id", statusResponse.statusId())))
                .body(statusResponse);
    }

    @DeleteMapping("/statuses/{status_id}")
    //    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteStatus(@PathVariable("status_id") Long statusId) {
        statusService.delete(statusId);
        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping(value = "/statuses/{status_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    //    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateStatus(@PathVariable("status_id") Long statusId,
                                              @RequestBody StatusRequest statusRequest) {
       statusService.update(statusId,statusRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{task_id}/performers")
    public List<UserResponse> getAllPerformers(@PathVariable("task_id") Long taskId) {
        return taskService.getPerformerByTaskId(taskId);
    }

    @PutMapping(value = "/{task_id}/performers/{performer_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addPerformer(@PathVariable("task_id") Long taskId,
                                             @PathVariable("performer_id") Long performerId,
                                             @AuthenticationPrincipal User user) {
        taskService.addPerformer(taskId, user.getId(), performerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{task_id}/comments")
    public List<CommentResponse> getAllComments(@PathVariable("task_id") Long taskId,
                                                @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return commentService.commentsByTask(taskId, PageRequest.of(offset, limit));
    }

    @PostMapping(value = "/{task_id}/comments/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentResponse> addComment(@PathVariable("task_id") Long taskId,
                                                      @RequestBody CommentRequest commentRequest,
                                                      @AuthenticationPrincipal User user,
                                                      UriComponentsBuilder uriComponentsBuilder) {

        CommentResponse commentResponse = commentService.add(commentRequest, taskId, user.getId());
        return ResponseEntity.created(uriComponentsBuilder.
                        replacePath("tasks/{task_id}/comments/{comment_Id}")
                        .build(Map.of("task_id", taskId,
                                "commentId", commentResponse.commentId())))
                .body(commentResponse);
    }

    @DeleteMapping("/{task_id}/comments/{comment_id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("task_id") Long taskId,
                                              @PathVariable("comment_id") Long commentId,
                                              @AuthenticationPrincipal User user) {
        commentService.delete(commentId, taskId, user.getId());
        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping(value = "/{task_id}/comments/{comment_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateComment(@PathVariable("task_id") Long taskId,
                                              @PathVariable("comment_id") Long commentId,
                                              @RequestBody CommentRequest commentRequest,
                                              @AuthenticationPrincipal User user) {
        commentService.update(commentId, commentRequest, taskId, user.getId());
        return ResponseEntity.noContent().build();
    }
}