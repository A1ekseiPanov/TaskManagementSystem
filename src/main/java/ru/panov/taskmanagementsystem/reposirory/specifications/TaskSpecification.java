package ru.panov.taskmanagementsystem.reposirory.specifications;


import org.springframework.data.jpa.domain.Specification;
import ru.panov.taskmanagementsystem.model.Task;

public class TaskSpecification {
    public static Specification<Task> headerContains(String header) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("header")), ("%" + header + "%").toLowerCase());
    }

    public static Specification<Task> descriptionContains(String description) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("description")), ("%" + description + "%").toLowerCase());
    }
}