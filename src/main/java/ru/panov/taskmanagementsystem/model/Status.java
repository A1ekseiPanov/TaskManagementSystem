package ru.panov.taskmanagementsystem.model;

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
    private String status;
}