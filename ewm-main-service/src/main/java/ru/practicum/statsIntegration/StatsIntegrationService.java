package ru.practicum.statsIntegration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stat.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsIntegrationService {
    private final WebClient webClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Map<Long, Long> getViewsForEvents(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        LocalDateTime start = LocalDateTime.now().minusYears(10);
        LocalDateTime end = LocalDateTime.now();

        List<ViewStats> stats = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start.format(formatter))
                        .queryParam("end", end.format(formatter))
                        .queryParam("uris", uris)
                        .queryParam("unique", true)
                        .build())
                .retrieve()
                .bodyToFlux(ViewStats.class)
                .collectList()
                .block();

        if (stats == null) {
            return Collections.emptyMap();
        }

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> extractEventIdFromUri(stat.getUri()),
                        ViewStats::getHits
                ));
    }

    private Long extractEventIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
