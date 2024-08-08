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

@RestController
@RequestMapping(value = STATUSES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StatusController {
    private final StatusService statusService;
    @Operation(
            summary = "Получение всех статусов задач"
    )
    @GetMapping
    public List<StatusResponse> getAllStatuses() {
        return statusService.statuses();
    }

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