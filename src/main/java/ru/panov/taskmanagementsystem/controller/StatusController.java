package ru.panov.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.panov.taskmanagementsystem.model.dto.request.StatusRequest;
import ru.panov.taskmanagementsystem.model.dto.response.StatusResponse;
import ru.panov.taskmanagementsystem.service.StatusService;

import java.util.List;
import java.util.Map;

import static ru.panov.taskmanagementsystem.util.PathConstants.STATUSES_PATH;

/**
 * Класс StatusController отвечает за обработку HTTP-запросов, связанных со статусами задач.
 * Предоставляет эндпоинты для получения, создания, обновления и удаления статусов задач.
 */
@RestController
@RequestMapping(value = STATUSES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StatusController {
    private final StatusService statusService;

    /**
     * Получает все статусы задач.
     *
     * @return список объектов {@link StatusResponse}, представляющих все статусы задач.
     */
    @Operation(
            summary = "Получение всех статусов задач"
    )
    @GetMapping
    public List<StatusResponse> getAllStatuses() {
        return statusService.statuses();
    }

    /**
     * Создает новый статус задачи.
     * Эта операция доступна только пользователям с правами 'ADMIN'.
     *
     * @param statusRequest        объект запроса, содержащий данные для создания нового статуса.
     * @param bindingResult        результат валидации.
     * @param uriComponentsBuilder используется для построения URI для созданного статуса.
     * @return {@link ResponseEntity}, содержащий созданный объект {@link StatusResponse} и URI его местоположения.
     * @throws BindException если запрос содержит ошибки валидации.
     */
    @Operation(
            summary = "Создание нового статуса задач"
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<StatusResponse> crateStatus(@Valid @RequestBody StatusRequest statusRequest,
                                                      BindingResult bindingResult,
                                                      UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            StatusResponse statusResponse = statusService.create(statusRequest);
            return ResponseEntity.created(uriComponentsBuilder.
                            replacePath("/statuses/{status_id}")
                            .build(Map.of("status_id", statusResponse.statusId())))
                    .body(statusResponse);
        }
    }

    /**
     * Удаляет статус задачи по его ID.
     * Эта операция доступна только пользователям с правами 'ADMIN'.
     *
     * @param statusId ID статуса, который необходимо удалить.
     * @return {@link ResponseEntity} без содержимого.
     */
    @Operation(
            summary = "Удаление статуса задач"
    )
    @DeleteMapping("/{status_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteStatus(@PathVariable("status_id") Long statusId) {
        statusService.delete(statusId);
        return ResponseEntity.noContent()
                .build();
    }

    /**
     * Обновляет существующий статус задачи по его ID.
     * Эта операция доступна только пользователям с правами 'ADMIN'.
     *
     * @param statusId      ID статуса, который необходимо обновить.
     * @param statusRequest объект запроса, содержащий обновленные данные статуса.
     * @param bindingResult результат валидации.
     * @return {@link ResponseEntity} без содержимого.
     * @throws BindException если запрос содержит ошибки валидации.
     */
    @Operation(
            summary = "Обновление статуса задач"
    )
    @PutMapping(value = "/{status_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateStatus(@PathVariable("status_id") Long statusId,
                                             @Valid @RequestBody StatusRequest statusRequest,
                                             BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            statusService.update(statusId, statusRequest);
            return ResponseEntity.noContent().build();
        }
    }
}