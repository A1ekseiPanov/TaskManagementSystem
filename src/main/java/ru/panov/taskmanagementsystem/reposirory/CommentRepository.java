package ru.panov.taskmanagementsystem.reposirory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.panov.taskmanagementsystem.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByTask_Id(Long taskId, Pageable pageable);
}