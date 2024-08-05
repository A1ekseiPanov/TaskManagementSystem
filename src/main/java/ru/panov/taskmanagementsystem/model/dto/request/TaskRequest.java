package ru.panov.taskmanagementsystem.model.dto.request;

public record TaskRequest(String header,
                          String description,
                          Long statusId,
                          Integer priority) {
}