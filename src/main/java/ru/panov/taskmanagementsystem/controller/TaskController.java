package ru.panov.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.User;
import ru.panov.taskmanagementsystem.model.dto.request.CommentRequest;
import ru.panov.taskmanagementsystem.model.dto.request.TaskRequest;
import ru.panov.taskmanagementsystem.model.dto.response.CommentResponse;
import ru.panov.taskmanagementsystem.model.dto.response.TaskResponse;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;
import ru.panov.taskmanagementsystem.reposirory.specifications.TaskSpecification;
import ru.panov.taskmanagementsystem.service.CommentService;
import ru.panov.taskmanagementsystem.service.TaskService;

import java.util.List;
import java.util.Map;

import static ru.panov.taskmanagementsystem.util.PathConstants.TASKS_PATH;

/**
 * Класс TaskController отвечает за обработку HTTP-запросов, связанных с задачами.
 * Предоставляет эндпоинты для создания, получения, обновления и удаления задач,
 * а также управления исполнителями и комментариями к задачам.
 */
@RestController
@RequestMapping(value = TASKS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final CommentService commentService;

    /**
     * Создает новую задачу.
     * Эта операция доступна для аутентифицированных пользователей.
     *
     * @param taskRequest          объект запроса, содержащий данные для создания задачи.
     * @param bindingResult        результат валидации.
     * @param user                 аутентифицированный пользователь.
     * @param uriComponentsBuilder используется для построения URI для созданной задачи.
     * @return {@link ResponseEntity}, содержащий созданный объект {@link TaskResponse} и URI его местоположения.
     * @throws BindException если запрос содержит ошибки валидации.
     */
    @Operation(
            summary = "Создание задачи",
            description = "Создание задачи аутентифицированным пользователем"
    )
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

    /**
     * Получает все задачи с возможностью фильтрации по заголовку и описанию и пагинацией.
     *
     * @param header      заголовок задачи для фильтрации (опционально).
     * @param description описание задачи для фильтрации (опционально).
     * @param offset      смещение для пагинации (по умолчанию 0).
     * @param limit       количество записей на страницу (по умолчанию 20).
     * @return список объектов {@link TaskResponse}, представляющих все задачи, соответствующие фильтрам.
     */
    @Operation(
            summary = "Получение всех задач"
    )
    @GetMapping
    public List<TaskResponse> gelAll(@RequestParam(value = "header", required = false) String header,
                                     @RequestParam(value = "description", required = false) String description,
                                     @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                     @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        Specification<Task> specification = Specification.where(null);
        if (header != null) {
            specification = specification.and(TaskSpecification.headerContains(header));
        }
        if (description != null) {

            specification = specification.and(TaskSpecification.descriptionContains(description));
        }
        return taskService.getAll(specification, PageRequest.of(offset, limit));
    }

    /**
     * Обновляет задачу по ее ID.
     * Эта операция доступна для аутентифицированных пользователей.
     *
     * @param taskId        ID задачи, которую необходимо обновить.
     * @param task          объект запроса, содержащий обновленные данные задачи.
     * @param bindingResult результат валидации.
     * @param user          аутентифицированный пользователь.
     * @return {@link ResponseEntity} без содержимого.
     * @throws BindException если запрос содержит ошибки валидации.
     */
    @Operation(
            summary = "Обновление задачи",
            description = "Обновление задачи по ее id"
    )
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

    /**
     * Обновляет статус задачи по ее ID.
     * Эта операция доступна исполнителям задачи.
     *
     * @param taskId   ID задачи, для которой обновляется статус.
     * @param statusId ID нового статуса.
     * @param user     аутентифицированный пользователь.
     * @return {@link ResponseEntity} без содержимого.
     */
    @Operation(
            summary = "Обновление статуса у задачи исполнителями",
            description = "Обновление статуса у конкретной задачи исполнителями"
    )
    @PutMapping(value = "/{task_id}/statuses/{status_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateTaskStatus(@PathVariable("task_id") Long taskId,
                                                 @PathVariable("status_id") Long statusId,
                                                 @AuthenticationPrincipal User user) {
        taskService.updateStatus(taskId, user.getId(), statusId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Удаляет задачу по ее ID.
     * Эта операция доступна для аутентифицированных пользователей.
     *
     * @param taskId ID задачи, которую необходимо удалить.
     * @param user   аутентифицированный пользователь.
     * @return {@link ResponseEntity} без содержимого.
     */
    @Operation(
            summary = "Удаление задачи",
            description = "Удаление задачи по ее id"
    )
    @DeleteMapping("/{task_id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("task_id") Long taskId,
                                           @AuthenticationPrincipal User user) {
        taskService.delete(taskId, user.getId());
        return ResponseEntity.noContent()
                .build();
    }

    /**
     * Получает всех исполнителей задачи по ее ID.
     *
     * @param taskId ID задачи.
     * @return список объектов {@link UserResponse}, представляющих исполнителей задачи.
     */
    @Operation(
            summary = "Получение всех исполнителей задачи",
            description = "Получить всех исполнителей задачи по ее id"
    )
    @GetMapping("/{task_id}/performers")
    public List<UserResponse> getAllPerformersByTask(@PathVariable("task_id") Long taskId) {
        return taskService.getPerformerByTaskId(taskId);
    }

    /**
     * Назначает исполнителя задаче.
     * Эта операция доступна автору задачи.
     *
     * @param taskId      ID задачи, для которой назначается исполнитель.
     * @param performerId ID исполнителя.
     * @param user        аутентифицированный пользователь.
     * @return {@link ResponseEntity} без содержимого.
     */
    @Operation(
            summary = "Назначение исполнителя задаче",
            description = "Назначение исполнителя задаче автором задачи"
    )
    @PutMapping(value = "/{task_id}/performers/{performer_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addPerformerToTask(@PathVariable("task_id") Long taskId,
                                                   @PathVariable("performer_id") Long performerId,
                                                   @AuthenticationPrincipal User user) {
        taskService.addPerformer(taskId, user.getId(), performerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает все комментарии к задаче по ее ID с возможностью пагинации.
     *
     * @param taskId ID задачи.
     * @param offset смещение для пагинации (по умолчанию 0).
     * @param limit  количество записей на страницу (по умолчанию 20).
     * @return список объектов {@link CommentResponse}, представляющих комментарии к задаче.
     */
    @Operation(
            summary = "Получение всех коментариев к задаче",
            description = "Получение всех коментариев к задаче по ее id"
    )
    @GetMapping("/{task_id}/comments")
    public List<CommentResponse> getAllCommentsByTask(@PathVariable("task_id") Long taskId,
                                                      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                      @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return commentService.commentsByTask(taskId, PageRequest.of(offset, limit));
    }

    /**
     * Добавляет новый комментарий к задаче.
     * Эта операция доступна для аутентифицированных пользователей.
     *
     * @param taskId               ID задачи, к которой добавляется комментарий.
     * @param commentRequest       объект запроса, содержащий данные для нового комментария.
     * @param bindingResult        результат валидации.
     * @param user                 аутентифицированный пользователь.
     * @param uriComponentsBuilder используется для построения URI для созданного комментария.
     * @return {@link ResponseEntity}, содержащий созданный объект {@link CommentResponse} и URI его местоположения.
     * @throws BindException если запрос содержит ошибки валидации.
     */
    @Operation(
            summary = "Добавление нового коментария к задаче"
    )
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

    /**
     * Удаляет комментарий у задачи по его ID.
     * Эта операция доступна для аутентифицированных пользователей.
     *
     * @param taskId    ID задачи, к которой относится комментарий.
     * @param commentId ID комментария, который необходимо удалить.
     * @param user      аутентифицированный пользователь.
     * @return {@link ResponseEntity} без содержимого.
     */
    @Operation(
            summary = "Удаление коментария у задачи",
            description = "Удаление коментария у задачи по ее id"
    )
    @DeleteMapping("/{task_id}/comments/{comment_id}")
    public ResponseEntity<Void> deleteCommentFromTask(@PathVariable("task_id") Long taskId,
                                                      @PathVariable("comment_id") Long commentId,
                                                      @AuthenticationPrincipal User user) {
        commentService.delete(commentId, taskId, user.getId());
        return ResponseEntity.noContent()
                .build();
    }

    /**
     * Обновляет комментарий у задачи по его ID.
     * Эта операция доступна для аутентифицированных пользователей.
     *
     * @param taskId         ID задачи, к которой относится комментарий.
     * @param commentId      ID комментария, который необходимо обновить.
     * @param commentRequest объект запроса, содержащий обновленные данные комментария.
     * @param bindingResult  результат валидации.
     * @param user           аутентифицированный пользователь.
     * @return {@link ResponseEntity} без содержимого.
     * @throws BindException если запрос содержит ошибки валидации.
     */
    @Operation(
            summary = "Обновление коментария у задачи",
            description = "Обновление коментария у задачи по ее id"
    )
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