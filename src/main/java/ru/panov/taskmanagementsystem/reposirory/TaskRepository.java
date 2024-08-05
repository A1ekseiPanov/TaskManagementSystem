package ru.panov.taskmanagementsystem.reposirory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.panov.taskmanagementsystem.model.Task;
import ru.panov.taskmanagementsystem.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByIdAndUser_Id(Long taskId, Long userId);

    Page<Task> findAll(Pageable pageable);

    @Query("select t.performers from Task t where t.id = :taskId")
    List<User> findAllPerformersByTaskId(Long taskId);
}