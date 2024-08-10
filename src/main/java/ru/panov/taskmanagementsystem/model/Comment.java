package ru.panov.taskmanagementsystem.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coments")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity{
    @Column(nullable = false)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}