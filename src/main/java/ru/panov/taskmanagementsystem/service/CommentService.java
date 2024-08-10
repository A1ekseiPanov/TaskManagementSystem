package ru.panov.taskmanagementsystem.service;

import org.springframework.data.domain.Pageable;
import ru.panov.taskmanagementsystem.model.Comment;
import ru.panov.taskmanagementsystem.model.dto.request.CommentRequest;
import ru.panov.taskmanagementsystem.model.dto.response.CommentResponse;

import java.util.List;

/**
 * Сервис для управления комментариями в системе управления задачами.
 * Этот интерфейс определяет методы для добавления, обновления, удаления
 * и получения комментариев, связанных с задачами.
 */
public interface CommentService {
    /**
     * Добавляет новый комментарий к задаче.
     *
     * @param commentRequest объект запроса, содержащий данные для нового комментария.
     * @param taskId         ID задачи, к которой добавляется комментарий.
     * @param userId         ID пользователя, добавляющего комментарий.
     * @return {@link CommentResponse} объект, содержащий информацию о созданном комментарии.
     */
    CommentResponse add(CommentRequest commentRequest, Long taskId, Long userId);

    /**
     * Обновляет существующий комментарий.
     *
     * @param commentId      ID комментария, который необходимо обновить.
     * @param commentRequest объект запроса, содержащий обновленные данные комментария.
     * @param taskId         ID задачи, к которой относится комментарий.
     * @param userId         ID пользователя, обновляющего комментарий.
     */
    void update(Long commentId, CommentRequest commentRequest, Long taskId, Long userId);

    /**
     * Удаляет комментарий по его ID.
     *
     * @param commentId ID комментария, который необходимо удалить.
     * @param taskId    ID задачи, к которой относится комментарий.
     * @param userId    ID пользователя, удаляющего комментарий.
     */
    void delete(Long commentId, Long taskId, Long userId);

    /**
     * Получает список всех комментариев, связанных с конкретной задачей.
     *
     * @param taskId   ID задачи, для которой требуется получить комментарии.
     * @param pageable объект {@link Pageable} для пагинации результатов.
     * @return список {@link CommentResponse}, представляющий комментарии задачи.
     */
    List<CommentResponse> commentsByTask(Long taskId, Pageable pageable);

    /**
     * Возвращает комментарий по его ID.
     *
     * @param commentId ID комментария.
     * @return объект {@link Comment}, представляющий комментарий.
     */
    Comment getComment(Long commentId);
}