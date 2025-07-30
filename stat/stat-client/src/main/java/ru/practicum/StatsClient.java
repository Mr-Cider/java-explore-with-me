package ru.practicum;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.stat.HitDto;
import ru.practicum.stat.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsClient {
    private final WebClient webClient;

    public Mono<HitDto> saveHit(HitDto hitDto) {
        return webClient.post()
                .uri("/hit")
                .bodyValue(hitDto)
                .retrieve()
                .bodyToMono(HitDto.class);
    }

    public Flux<ViewStats> getStats(LocalDateTime start,
                                    LocalDateTime end,
                                    List<String> uris,
                                    boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return webClient.get().uri(uriBuilder -> uriBuilder
                .path("/stats")
                .queryParam("start", URLEncoder.encode(start.format(formatter), StandardCharsets.UTF_8))
                .queryParam("end", URLEncoder.encode(end.format(formatter), StandardCharsets.UTF_8))
                .queryParam("uris", uris)
                .queryParam("unique", String.valueOf(unique))
                .build())
                .retrieve()
                .bodyToFlux(ViewStats.class);
    }
}
