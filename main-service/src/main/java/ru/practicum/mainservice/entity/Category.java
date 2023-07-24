package ru.practicum.mainservice.entity;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.persistence.*;

/**
 * Категории событий
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
