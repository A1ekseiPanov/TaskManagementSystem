package ru.panov.taskmanagementsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "coments")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity{
    @NotBlank
    private String comment;

    @ManyToOne
    private User author;

    @ManyToOne
    private Task task;
}