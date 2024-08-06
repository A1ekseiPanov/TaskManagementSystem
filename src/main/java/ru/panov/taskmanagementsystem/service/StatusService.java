package ru.panov.taskmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.panov.taskmanagementsystem.exception.NotFoundException;
import ru.panov.taskmanagementsystem.mapper.StatusMapper;
import ru.panov.taskmanagementsystem.model.Status;
import ru.panov.taskmanagementsystem.model.dto.request.StatusRequest;
import ru.panov.taskmanagementsystem.model.dto.response.StatusResponse;
import ru.panov.taskmanagementsystem.reposirory.StatusRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {
    public final StatusRepository statusRepository;
    public final StatusMapper statusMapper;

    public StatusResponse create(StatusRequest statusRequest) {
        Status status = Status.builder()
                .status(statusRequest.status())
                .build();
        return statusMapper.statusToResponseEntity(statusRepository.save(status));
    }

    public Status get(Long statusId) {
        return statusRepository.findById(statusId)
                .orElseThrow(() ->
                        new NotFoundException("Статус с id:%s не найден"
                                .formatted(statusId)));
    }

    public void delete(Long statusId) {
        statusRepository.deleteById(statusId);
    }

    public void update(Long statusId, StatusRequest statusRequest) {
        Status status = get(statusId);
        status.setStatus(statusRequest.status());
        status.setUpdated(LocalDateTime.now());
        statusRepository.save(status);
    }

    public List<StatusResponse> statuses() {
        return statusMapper
                .listStatusToListResponseEntity(statusRepository.findAll());
    }
}