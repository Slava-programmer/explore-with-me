package ru.practicum.mainservice.dto.event;

import lombok.*;
import ru.practicum.mainservice.dto.location.LocationDto;
import ru.practicum.mainservice.model.EventStateAction;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventUpdateUserRequest {
    private String annotation;
    private Long category;
    private String description;
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventStateAction stateAction;
    private String title;
}
