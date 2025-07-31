package ru.practicum.stat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.HitDto;
import ru.practicum.stat.ViewStats;
import ru.practicum.stat.ViewStatsRequest;
import ru.practicum.stat.service.StatsService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public HitDto saveHit(@Valid @RequestBody HitDto hitDto) {
        return statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
    try {
        ViewStatsRequest request = ViewStatsRequest.builder()
                .start(start)
                .end(end)
                .uris(uris != null ? new HashSet<>(uris) : null)
                .unique(unique)
                .build();

        return ResponseEntity.ok(statsService.getStats(request));
    } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
    }
    }
}
