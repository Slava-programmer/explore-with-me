package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.request.ParticipationRequestDto;
import ru.practicum.mainservice.entity.Event;
import ru.practicum.mainservice.entity.Request;
import ru.practicum.mainservice.entity.User;
import ru.practicum.mainservice.exception.EventConflictException;
import ru.practicum.mainservice.exception.IncorrectRequestException;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.model.RequestStatus;
import ru.practicum.mainservice.repository.RequestRepository;
import ru.practicum.mainservice.service.mapper.RequestMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventPublicService eventService;

    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userService.getUserByIdIfExist(userId);
        Event event = eventService.getEventByIdIfExist(eventId);

        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new EventConflictException(String.format("User with id='%s' is owner event by id='%s' and cannot create request",
                    userId, eventId));
        }

        Integer countConfirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= countConfirmedRequests) {
            throw new EventConflictException(String.format("No free seats for sign ip on event with id='%s'", eventId));
        }
        if (!Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new EventConflictException("Event state is not 'PUBLISHED'");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new EventConflictException(String.format("User with id='%s' already sign up on event with id='%s'",
                    user, event));
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();

        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        Request savedRequest = requestRepository.save(request);
        return RequestMapper.toDto(savedRequest);
    }

    public List<ParticipationRequestDto> getAllRequestsByUserId(Long userId) {
        userService.checkExistUserById(userId);

        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return RequestMapper.toDtoList(requests);
    }

    @Transactional
    public ParticipationRequestDto cancelledRequestById(Long userId, Long requestId) {
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
}
