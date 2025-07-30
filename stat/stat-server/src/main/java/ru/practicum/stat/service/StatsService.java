package ru.practicum.stat.service;

import ru.practicum.stat.HitDto;
import ru.practicum.stat.ViewStats;
import ru.practicum.stat.ViewStatsRequest;

import java.util.List;


public interface StatsService {

    HitDto saveHit(HitDto hitDto);

    List<ViewStats> getStats(ViewStatsRequest viewStatsRequest);
}
