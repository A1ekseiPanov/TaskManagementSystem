package ru.panov.taskmanagementsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Task extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String header;
    @Column(nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;
    @Enumerated(value = EnumType.STRING)
    private Priority priority;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "task")
    private List<Comment> comments = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "tasks_performers",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "performer_id"))
    private List<User> performers = new ArrayList<>();
}