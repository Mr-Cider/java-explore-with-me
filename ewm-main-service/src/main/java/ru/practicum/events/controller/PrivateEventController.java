package ru.practicum.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {

    private final EventService eventService;

    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("GET /users/{userId}/events");
        log.info("Private: Получение событий, добавленных текущим пользователем");
        return eventService.getPrivateEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createNewEvent(@PathVariable Long userId,
                                       @Valid @RequestBody NewEventDto newEventDto) {
        log.debug("POST /users/{}/events", userId);
        log.info("Private: Добавление нового события");
        return eventService.createNewEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        log.debug("GET /users/{}/events/{}", userId, eventId);
        log.info("Private: Получение полной информации о событии добавленном текущим пользователем");
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.debug("PATCH /users/{}/events/{}", userId, eventId);
        log.info("Private: Изменение события добавленного текущим пользователем");
        return eventService.updateUserEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        log.debug("GET /users/{}/events/{}/requests", userId, eventId);
        log.info("Private: Получение информации о запросах на участие в событии текущего пользователя");
        return requestService.getEventOwnerRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @Valid @RequestBody EventRequestStatusUpdateRequest update) {
        log.debug("PATCH /users/{}/events/{}/requests", userId, eventId);
        log.info("Private: Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя");
        return requestService.updateRequestsStatus(userId, eventId, update);
    }




}
