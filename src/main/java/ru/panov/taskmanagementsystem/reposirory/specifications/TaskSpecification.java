package ru.panov.taskmanagementsystem.reposirory.specifications;


import org.springframework.data.jpa.domain.Specification;
import ru.panov.taskmanagementsystem.model.Task;

/**
 * Спецификации для фильтрации задач по различным критериям.
 * Содержит методы для создания спецификаций, которые можно использовать для поиска задач
 * по частичным совпадениям в заголовке и описании.
 */
public class TaskSpecification {
    /**
     * Создает спецификацию для поиска задач, где заголовок содержит указанную строку.
     *
     * @param header Строка, которую необходимо найти в заголовке задач.
     * @return Спецификация, которая проверяет, что заголовок задачи содержит указанную строку.
     */
    public static Specification<Task> headerContains(String header) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("header")), ("%" + header + "%").toLowerCase());
    }

    /**
     * Создает спецификацию для поиска задач, где описание содержит указанную строку.
     *
     * @param description Строка, которую необходимо найти в описании задач.
     * @return Спецификация, которая проверяет, что описание задачи содержит указанную строку.
     */
    public static Specification<Task> descriptionContains(String description) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .like(criteriaBuilder.lower(root.get("description")), ("%" + description + "%").toLowerCase());
    }
}