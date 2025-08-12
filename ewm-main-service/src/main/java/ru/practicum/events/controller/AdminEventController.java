package ru.practicum.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.dto.paramsDto.AdminEventsParamDto;
import ru.practicum.events.model.State;
import ru.practicum.events.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam (required = false) List<Long> users,
                                        @RequestParam (required = false) List<State> states,
                                        @RequestParam (required = false) List<Long> categories,
                                        @RequestParam (required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam (required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @PositiveOrZero @RequestParam (defaultValue = "0") Integer from,
                                        @Positive @RequestParam (defaultValue = "10") Integer size) {

        AdminEventsParamDto adminEventsParamDto = new AdminEventsParamDto(
                users, states, categories, rangeStart, rangeEnd, from, size
        );
        log.debug("GET /admin/events");
        log.info("Admin: Поиск событий");
        return eventService.getAdminEvents(adminEventsParamDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.debug("PATCH /admin/events/{}", eventId);
        log.info("Admin: Редактирование данных события и его статуса (отклонение/публикация).");
        return eventService.updateAdminEvent(eventId, updateEventAdminRequest);
    }
}
