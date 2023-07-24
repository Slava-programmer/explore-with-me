package ru.practicum.mainservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.ParticipationRequestDto;
import ru.practicum.mainservice.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class RequestUserController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable(name = "userId") @Positive Long userId,
                                                 @RequestParam(name = "eventId") @Positive Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getAllRequest(@PathVariable(name = "userId") @Positive Long userId) {
        return requestService.getAllRequestsByUserId(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelledRequest(@PathVariable(name = "userId") @Positive Long userId,
                                                    @PathVariable(name = "requestId") @Positive Long requestId) {
        return requestService.cancelledRequestById(userId, requestId);
    }
}
