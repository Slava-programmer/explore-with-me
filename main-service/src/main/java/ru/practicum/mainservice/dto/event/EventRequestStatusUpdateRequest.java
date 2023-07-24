package ru.practicum.mainservice.dto.event;

import lombok.*;
import ru.practicum.mainservice.model.RequestStatus;

import java.util.Set;

/**
 * Запрос на изменение статуса запроса на участие в событии пользователя
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;
    private RequestStatus status;
}
