package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.event.EventRequestStatusUpdateResponse;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.Request;
import ru.practicum.mainservice.entity.User;
import ru.practicum.mainservice.exception.EventConflictException;
import ru.practicum.mainservice.exception.IncorrectRequestException;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.model.RequestStatus;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.RequestRepository;
import ru.practicum.mainservice.service.mapper.RequestMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = userService.getUserByIdIfExist(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NoFoundObjectException(String.format("Event with id='%s' not found", eventId)));

        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new EventConflictException(String.format("User with id='%s' is owner event by id='%s' and cannot create request",
                    userId, eventId));
        }

        if (!Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new EventConflictException("Нельзя добавить запрос на участие в неопубликованном событии");
        }

        Request request = new Request(null, LocalDateTime.now(), event, user, RequestStatus.PENDING);

        int confirmed = event.getConfirmedRequests();
        int limit = event.getParticipantLimit();

        if (limit == 0) {
            event.setConfirmedRequests(confirmed + 1);
            eventRepository.save(event);
            request.setStatus(RequestStatus.CONFIRMED);
        } else if (confirmed < limit) {
            if (!event.getRequestModeration()) {
                event.setConfirmedRequests(confirmed + 1);
                eventRepository.save(event);
                request.setStatus(RequestStatus.PENDING);
            }
        } else {
            throw new EventConflictException(String.format("No free seats for sign ip on event with id='%s'", eventId));
        }

        Request savedRequest = requestRepository.save(request);
        return RequestMapper.toDto(savedRequest);
    }

    public List<RequestDto> getAllRequestsByUserId(Long userId) {
        userService.checkExistUserById(userId);

        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return RequestMapper.toDtoList(requests);
    }

    @Transactional
    public RequestDto cancelledRequestById(Long userId, Long requestId) {
        userService.checkExistUserById(userId);

        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() ->
                new NoFoundObjectException(String.format("Request with id='%s' and with requester id='%s' not found",
                        requestId, userId)));

        if ((Objects.equals(request.getStatus(), RequestStatus.CANCELED))
                || (Objects.equals(request.getStatus(), RequestStatus.REJECTED))) {
            throw new IncorrectRequestException("Request already canceled");
        }

        request.setStatus(RequestStatus.CANCELED);
        Request savedRequest = requestRepository.save(request);
        return RequestMapper.toDto(savedRequest);
    }

    @Transactional
    public EventRequestStatusUpdateResponse updateStatusRequestByEventId(Long userId, Long eventId,
                                                                         EventRequestStatusUpdateRequest request) {
        userService.checkExistUserById(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NoFoundObjectException(String.format(
                        "Event with id='%s' and initiator with id='%s' not found", eventId, userId)));


        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new EventConflictException("Достигнут лимит заявок на участие в событии");
        }

        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        List<Request> requests = requestRepository.findAllById(request.getRequestIds());
        for (Request r : requests) {
            if (r.getStatus() == RequestStatus.PENDING) {
                if (event.getParticipantLimit() == 0) {
                    r.setStatus(RequestStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else if (event.getParticipantLimit() > event.getConfirmedRequests()) {
                    if (!event.getRequestModeration()) {
                        r.setStatus(RequestStatus.CONFIRMED);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    } else {
                        if (request.getStatus() == RequestStatus.CONFIRMED) {
                            r.setStatus(RequestStatus.CONFIRMED);
                            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        } else {
                            r.setStatus(RequestStatus.REJECTED);
                        }
                    }
                } else {
                    r.setStatus(RequestStatus.REJECTED);
                }
            } else {
                throw new EventConflictException("Статус можно изменить только у заявок в статусе WAITING");
            }

            if (r.getStatus().equals((RequestStatus.CONFIRMED))) {
                confirmed.add(r);
            } else {
                rejected.add(r);
            }
        }
        eventRepository.save(event);

        return new EventRequestStatusUpdateResponse(confirmed.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList()),
                rejected.stream()
                        .map(RequestMapper::toDto)
                        .collect(Collectors.toList()));
    }


    public List<RequestDto> getAllParticipationRequestsByEventId(Long userId, Long eventId) {
        userService.checkExistUserById(userId);

        if (!eventRepository.existsByInitiatorIdAndId(userId, eventId)) {
            throw new EventConflictException("User is not initiator of event");
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return RequestMapper.toDtoList(requests);
    }

    private void updateEventRequest(Request request, int change) {
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() + change);

            eventRepository.save(event);
        }
    }

    private void canceledRequests(Event event) {
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {

            List<Request> canceledRequests = requestRepository.findAllByEventIdAndStatus(event.getId(), RequestStatus.PENDING)
                    .stream()
                    .peek(request -> request.setStatus(RequestStatus.CANCELED))
                    .collect(Collectors.toList());

            requestRepository.saveAll(canceledRequests);

            throw new EventConflictException("the limit on applications for this event has already been reached");
        }
    }

}