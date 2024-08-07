package ru.panov.taskmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.panov.taskmanagementsystem.exception.DuplicateException;
import ru.panov.taskmanagementsystem.exception.NotFoundException;
import ru.panov.taskmanagementsystem.mapper.StatusMapper;
import ru.panov.taskmanagementsystem.model.Status;
import ru.panov.taskmanagementsystem.model.dto.request.StatusRequest;
import ru.panov.taskmanagementsystem.model.dto.response.StatusResponse;
import ru.panov.taskmanagementsystem.reposirory.StatusRepository;
import ru.panov.taskmanagementsystem.service.StatusService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {
    public final StatusRepository statusRepository;
    public final StatusMapper statusMapper;

    @Override
    public StatusResponse create(StatusRequest statusRequest) {
        checkUniq(statusRequest);
        Status status = Status.builder()
                .status(statusRequest.status())
                .build();
        return statusMapper.statusToResponseEntity(statusRepository.save(status));
    }

    @Override
    public Status get(Long statusId) {
        return statusRepository.findById(statusId)
                .orElseThrow(() ->
                        new NotFoundException("Статус с id:%s не найден"
                                .formatted(statusId)));
    }

    @Override
    public void delete(Long statusId) {
        statusRepository.deleteById(statusId);
    }

    @Override
    public void update(Long statusId, StatusRequest statusRequest) {
        Status status = get(statusId);
        checkUniq(statusRequest);
        status.setStatus(statusRequest.status());
        status.setUpdated(LocalDateTime.now());
        statusRepository.save(status);
    }

    @Override
    public List<StatusResponse> statuses() {
        return statusMapper
                .listStatusToListResponseEntity(statusRepository.findAll());
    }

    private void checkUniq(StatusRequest statusRequest) {
        Optional<Status> byStatus = statusRepository.findByStatus(statusRequest.status());
        if (byStatus.isPresent()) {
            throw new DuplicateException("Данный статус: %s уже существует"
                    .formatted(statusRequest.status()));
        }
    }
}