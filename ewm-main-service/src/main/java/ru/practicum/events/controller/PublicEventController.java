package ru.practicum.events.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.paramsDto.PublicEventsParamDto;
import ru.practicum.events.service.EventService;
import ru.practicum.stat.HitDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicEventController {

    private final EventService eventService;
    private final StatsClient statsClient;

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
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest httpServletRequest) {
        log.debug("GET /events");
        log.info("Public: Получение событий с возможностью фильтрации");
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
        return eventService.getPublicEvents(publicEventsParamDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long eventId, HttpServletRequest httpServletRequest) {
        log.debug("GET /events/{}", eventId);
        log.info("Public: Получение подробной информации об опубликованном событии по его идентификатору");
        sendStatsHit(httpServletRequest);
        return eventService.getEvent(eventId);
    }

    private void sendStatsHit(HttpServletRequest request) {
        statsClient.saveHit(
                HitDto.builder()
                        .app("ewm-main-service")
                        .uri(request.getRequestURI())
                        .ip(request.getRemoteAddr())
                        .timestamp(LocalDateTime.now())
                        .build()
        ).subscribe(
                response -> log.info("Статистика отправлена"),
                error -> log.error("Ошибка отправки статистики", error)
        );
    }
}
