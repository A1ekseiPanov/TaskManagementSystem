package ru.panov.taskmanagementsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "statuses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Status extends BaseEntity{
    @Column(nullable = false, unique = true)
    private String status;
}