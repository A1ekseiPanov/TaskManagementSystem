package ru.panov.taskmanagementsystem.service;

import ru.panov.taskmanagementsystem.model.Status;
import ru.panov.taskmanagementsystem.model.dto.request.StatusRequest;
import ru.panov.taskmanagementsystem.model.dto.response.StatusResponse;

import java.util.List;

public interface StatusService {
    StatusResponse create(StatusRequest statusRequest);

    Status get(Long statusId);

    void delete(Long statusId);

    void update(Long statusId, StatusRequest statusRequest);

    List<StatusResponse> statuses();
}