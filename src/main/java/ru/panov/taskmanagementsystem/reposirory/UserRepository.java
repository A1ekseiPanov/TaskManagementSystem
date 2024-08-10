package ru.panov.taskmanagementsystem.reposirory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.panov.taskmanagementsystem.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select  u from User u left join fetch u.roles where u.email = :email")
    Optional<User> findByEmail(String email);
}