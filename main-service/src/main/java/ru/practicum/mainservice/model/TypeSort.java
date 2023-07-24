package ru.practicum.mainservice.model;

import org.springframework.data.domain.Sort;

public enum TypeSort {
    EVENT_DATE("eventDate"),
    VIEWS("views");

    private final String sortField;

    TypeSort(String sortField) {
        this.sortField = sortField;
    }

    public Sort toSort(Sort.Direction direction) {
        return Sort.by(direction, this.sortField);
    }
}
