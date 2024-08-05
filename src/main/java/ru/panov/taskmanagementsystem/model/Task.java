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
public class Task extends BaseEntity{
    private String header;
    private String description;
    @ManyToOne
    private Status status;
    @Enumerated(value = EnumType.STRING)
    private Priority priority;
    @ManyToOne
    private User user;
    @OneToMany
    List<Comment> comments = new ArrayList<>();
    @OneToMany
    private List<User> performers = new ArrayList<>();
}