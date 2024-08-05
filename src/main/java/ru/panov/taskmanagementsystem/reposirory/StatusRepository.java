package ru.panov.taskmanagementsystem.reposirory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.panov.taskmanagementsystem.model.Status;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
}