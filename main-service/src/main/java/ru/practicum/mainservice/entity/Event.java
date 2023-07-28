package ru.practicum.mainservice.entity;

import lombok.*;
import ru.practicum.mainservice.model.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Событие
 *
 *
 * id - Идентификатор
 * annotation - Краткое описание
 * category - Категория
 * confirmedRequests - Количество одобренных заявок на участие в данном событии
 * createdOn - Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
 * description - Полное описание события
 * eventDate - Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
 * initiator - Пользователь - инициатор (краткая информация)
 * location - Широта и долгота места проведения события
 * paid - Нужно ли оплачивать участие
 * participantLimit - Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
 * publishedOn - Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
 * requestModeration - Нужна ли пре-модерация заявок на участие
 * title - Заголовок
 * views - Количество просмотрев события
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation;

    private Integer confirmedRequests;

    private LocalDateTime createdOn;

    private String description;

    private LocalDateTime eventDate;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    @Enumerated(value = EnumType.STRING)
    private EventState state;

    private String title;

    private Integer views;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;
}