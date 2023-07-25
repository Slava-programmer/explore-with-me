package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.mainservice.dto.event.*;
import ru.practicum.mainservice.entity.Category;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.exception.EventConflictException;
import ru.practicum.mainservice.exception.IncorrectRequestException;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.mapper.EventMapper;
import ru.practicum.mainservice.service.mapper.LocationMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventAdminService {
    private static final Integer HOURS_BEFORE_START_EVENT = 1;
    private final EventRepository eventRepository;
    private final CategoryService categoryService;

    public List<EventFullDto> getAllEvents(List<Long> users, List<String> states,
                                           List<Long> categories, LocalDateTime startDate, LocalDateTime endDate,
                                           Integer from, Integer size) {
        checkEndIsAfterStart(startDate, endDate);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        Specification<Event> specification = Specification.where(null);

        if (Objects.nonNull(users) && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }
        if (Objects.nonNull(states) && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }
        if (Objects.nonNull(categories) && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (Objects.nonNull(startDate)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), startDate));
        }
        if (Objects.nonNull(endDate)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), endDate));
        }

        List<Event> events = eventRepository.findAll(specification, pageable);
        return EventMapper.toEventFullDtoList(events);
    }

    @Transactional
    public EventFullDto updateEventById(Long eventId, EventUpdateAdminRequest request) {
        Event foundEvent = getEventByIdIfExist(eventId);

        if (!Objects.equals(EventState.PENDING, foundEvent.getState())) {
            throw new EventConflictException("Event state must be 'PENDING'");
        }

        if (Objects.nonNull(request.getAnnotation()) && StringUtils.hasLength(request.getAnnotation())) {
            if (request.getAnnotation().length() < 20 || request.getAnnotation().length() > 2000) {
                throw new IncorrectRequestException("incorrect length of the annotation parameter");
            } else {
                foundEvent.setAnnotation(request.getAnnotation());
            }
        }
        if (Objects.nonNull(request.getTitle()) && StringUtils.hasLength(request.getTitle())) {
            foundEvent.setTitle(request.getTitle());
        }
        if (Objects.nonNull(request.getCategory())) {
            final Category category = categoryService.getCategoryByIdIfExist(request.getCategory());
            foundEvent.setCategory(category);
        }
        if (Objects.nonNull(request.getDescription()) && StringUtils.hasLength(request.getDescription())) {
            foundEvent.setDescription(request.getDescription());
        }
        if (Objects.nonNull(request.getEventDate())) {
            checksStartTimeAfterMinPeriod(request.getEventDate());
            foundEvent.setEventDate(request.getEventDate());
        }
        if (Objects.nonNull(request.getLocation())) {
            foundEvent.setLocation(LocationMapper.toLocation(request.getLocation()));
        }
        if (Objects.nonNull(request.getPaid())) {
            foundEvent.setPaid(request.getPaid());
        }
        if (Objects.nonNull(request.getParticipantLimit())) {
            foundEvent.setParticipantLimit(request.getParticipantLimit());
        }
        if (Objects.nonNull(request.getRequestModeration())) {
            foundEvent.setRequestModeration(request.getRequestModeration());
        }
        if (Objects.nonNull(request.getStateAction())) {
            switch (request.getStateAction()) {
                case PUBLISH_EVENT:
                    foundEvent.setState(EventState.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    foundEvent.setState(EventState.CANCELED);
                    break;
            }
        }
        Event updatedEvent = eventRepository.save(foundEvent);
        return EventMapper.toEventFullDto(updatedEvent);
    }

    private Event getEventByIdIfExist(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NoFoundObjectException(String.format("Event with id='%s' not found", eventId)));
    }

    private void checksStartTimeAfterMinPeriod(LocalDateTime startDate) {
        LocalDateTime minStartDate = LocalDateTime.now().plusHours(HOURS_BEFORE_START_EVENT);
        if (startDate.isBefore(minStartDate)) {
            throw new IncorrectRequestException("The event will start in less than 1 hours. The start date of the event cannot be changed.");
        }
    }

    private void checkEndIsAfterStart(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IncorrectRequestException("Start date can not after end date");
        }
    }
}