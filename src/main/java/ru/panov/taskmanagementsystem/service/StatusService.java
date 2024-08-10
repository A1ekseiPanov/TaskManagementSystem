package ru.panov.taskmanagementsystem.service;

import ru.panov.taskmanagementsystem.model.Status;
import ru.panov.taskmanagementsystem.model.dto.request.StatusRequest;
import ru.panov.taskmanagementsystem.model.dto.response.StatusResponse;

import java.util.List;

/**
 * Сервис для управления статусами задач в системе управления задачами.
 * Этот интерфейс определяет методы для создания, получения, обновления
 * и удаления статусов, а также для получения списка всех статусов.
 */
public interface StatusService {
    /**
     * Создает новый статус задачи.
     *
     * @param statusRequest объект запроса, содержащий данные для нового статуса.
     * @return {@link StatusResponse} объект, содержащий информацию о созданном статусе.
     */
    StatusResponse create(StatusRequest statusRequest);

    /**
     * Возвращает статус по его ID.
     *
     * @param statusId ID статуса.
     * @return объект {@link Status}, представляющий статус задачи.
     */
    Status get(Long statusId);

    /**
     * Удаляет статус по его ID.
     *
     * @param statusId ID статуса, который необходимо удалить.
     */
    void delete(Long statusId);

    /**
     * Обновляет существующий статус задачи.
     *
     * @param statusId      ID статуса, который необходимо обновить.
     * @param statusRequest объект запроса, содержащий обновленные данные статуса.
     */
    void update(Long statusId, StatusRequest statusRequest);

    /**
     * Получает список всех статусов задач.
     *
     * @return список {@link StatusResponse}, представляющий все статусы задач.
     */
    List<StatusResponse> statuses();
}