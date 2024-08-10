package ru.panov.taskmanagementsystem.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.dto.request.TaskRequest;
import ru.panov.taskmanagementsystem.model.dto.response.TaskResponse;
import ru.panov.taskmanagementsystem.model.dto.response.UserResponse;

import java.util.List;

/**
 * Сервис для управления задачами в системе управления задачами.
 * Этот интерфейс определяет методы для создания, получения, обновления,
 * удаления задач, а также управления исполнителями и статусами задач.
 */
public interface TaskService {
    /**
     * Создает новую задачу.
     *
     * @param taskRequest объект запроса, содержащий данные для новой задачи.
     * @param userId      ID пользователя, создающего задачу.
     * @return {@link TaskResponse} объект, содержащий информацию о созданной задаче.
     */
    TaskResponse create(TaskRequest taskRequest, Long userId);

    /**
     * Возвращает задачу по её ID и ID пользователя.
     *
     * @param taskId ID задачи.
     * @param userId ID пользователя.
     * @return объект {@link Task}, представляющий задачу.
     */
    Task getTaskByIdAndUserId(Long taskId, Long userId);

    /**
     * Возвращает задачу по её ID.
     *
     * @param taskId ID задачи.
     * @return объект {@link Task}, представляющий задачу.
     */
    Task getTaskById(Long taskId);

    /**
     * Добавляет исполнителя к задаче.
     *
     * @param taskId    ID задачи.
     * @param userId    ID пользователя, назначающего исполнителя.
     * @param performId ID пользователя, который назначается исполнителем.
     * @return {@link TaskResponse} объект, содержащий обновленную информацию о задаче.
     */
    TaskResponse addPerformer(Long taskId, Long userId, Long performId);

    /**
     * Возвращает список всех исполнителей задачи по её ID.
     *
     * @param taskId ID задачи.
     * @return список {@link UserResponse}, представляющий исполнителей задачи.
     */
    List<UserResponse> getPerformerByTaskId(Long taskId);

    /**
     * Обновляет существующую задачу.
     *
     * @param taskId      ID задачи, которую необходимо обновить.
     * @param taskRequest объект запроса, содержащий обновленные данные задачи.
     * @param userId      ID пользователя, обновляющего задачу.
     */
    void update(Long taskId, TaskRequest taskRequest, Long userId);

    /**
     * Обновляет статус задачи.
     *
     * @param taskId   ID задачи.
     * @param userId   ID пользователя, обновляющего статус.
     * @param statusId ID нового статуса.
     */
    void updateStatus(Long taskId, Long userId, Long statusId);

    /**
     * Возвращает список всех задач с учетом спецификаций и пагинации.
     *
     * @param specification объект {@link Specification} для фильтрации задач.
     * @param pageable      объект {@link Pageable} для пагинации результатов.
     * @return список {@link TaskResponse}, представляющий задачи.
     */
    List<TaskResponse> getAll(Specification<Task> specification, Pageable pageable);

    /**
     * Удаляет задачу по её ID и ID пользователя.
     *
     * @param taskId ID задачи, которую необходимо удалить.
     * @param userId ID пользователя, удаляющего задачу.
     */
    void delete(Long taskId, Long userId);
}