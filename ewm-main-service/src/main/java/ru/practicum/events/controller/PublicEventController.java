package ru.practicum.events.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.paramsDto.PublicEventsParamDto;
import ru.practicum.events.service.interfaces.EventService;
import ru.practicum.events.service.interfaces.PublicEventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {

    private final EventService eventService;
    private final WebClient webClient;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest httpServletRequest) {
        sendStatsHit(httpServletRequest);
        PublicEventsParamDto publicEventsParamDto = PublicEventsParamDto.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();
        return eventService.getEvents(publicEventsParamDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId, HttpServletRequest httpServletRequest) {
        sendStatsHit(httpServletRequest);
        return eventService.getEvent(eventId);
    }

    private void sendStatsHit(HttpServletRequest request) {
        webClient.post()
                .uri("/hit")
                .bodyValue(Map.of(
                                "app", "ewm-main-service",
                                "uri", request.getRequestURI(),
                                "ip", request.getRemoteAddr(),
                                "timestamp", LocalDateTime.now()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        ))
                        .retrieve()
                        .toBodilessEntity()
                        .subscribe(
                                response -> log.info("Статистика отправлена"),
                                error -> log.error("Ошибка отправки статистики", error)
                        );
    }
}
