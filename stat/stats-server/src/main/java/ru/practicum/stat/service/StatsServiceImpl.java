package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stat.HitDto;
import ru.practicum.stat.ViewStats;
import ru.practicum.stat.ViewStatsRequest;
import ru.practicum.stat.mapper.HitMapper;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.repository.StatsRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    private final HitMapper hitMapper;

    @Transactional
    @Override
    public HitDto saveHit(HitDto hitDto) {
        EndpointHit hit = statsRepository.save(hitMapper.toHit(hitDto));
        return hitMapper.toHitDto(hit);
    }

    @Override
    public List<ViewStats> getStats(ViewStatsRequest viewStatsRequest) {
        Set<String> uris = viewStatsRequest.getUris();
        List<Object[]> results;
        if (viewStatsRequest.isUnique()) {
            results = statsRepository.getUniqueHits(
                    viewStatsRequest.getStart(),
                    viewStatsRequest.getEnd(),
                    uris != null && !uris.isEmpty() ? uris : null
            );
        } else {
            results = statsRepository.getAllHits(
                    viewStatsRequest.getStart(),
                    viewStatsRequest.getEnd(),
                    uris != null && !uris.isEmpty() ? uris : null
            );
        }
        return results.stream()
                .map(row -> ViewStats.builder()
                        .app((String) row[0])
                        .uri((String) row[1])
                        .hits((Long) row[2])
                        .build()).collect(Collectors.toList());
    }
}
