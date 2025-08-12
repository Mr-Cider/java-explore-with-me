package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        log.debug("GET /users/{}/requests", userId);
        log.info("Private: Получение информации о заявках текущего пользователя на участие в чужих событиях");
        return requestService.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.debug("POST /users/{}/requests", userId);
        log.info("Private: Добавление запроса от текущего пользователя на участие в событии");
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.debug("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        log.info("Private: Добавление запроса от текущего пользователя на участие в событии");
        return requestService.cancelRequest(userId, requestId);
    }
}
