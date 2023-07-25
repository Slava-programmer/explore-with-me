package ru.practicum.mainservice.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.RequestDto;
import ru.practicum.mainservice.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class RequestUserController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        log.info("RequestUserController: Request to create request");
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<RequestDto> getAllRequests(@PathVariable(name = "userId") Long userId) {
        log.info("RequestUserController: Request to get all requests");
        return requestService.getAllRequestsByUserId(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelledRequest(@PathVariable(name = "userId") Long userId,
                                       @PathVariable(name = "requestId") Long requestId) {
        return requestService.cancelledRequestById(userId, requestId);
    }
}
