package ru.panov.taskmanagementsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.panov.taskmanagementsystem.model.Status;
import ru.panov.taskmanagementsystem.model.dto.response.StatusResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    @Mapping(target = "statusId", source = "status.id")
    StatusResponse statusToResponseEntity(Status status);
    List<StatusResponse> listStatusToListResponseEntity(List<Status> status);

}