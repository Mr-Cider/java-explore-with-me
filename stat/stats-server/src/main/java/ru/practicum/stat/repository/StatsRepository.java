package ru.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stat.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
    SELECT h.app AS app, h.uri AS uri, COUNT(DISTINCT h.ip) AS hits
    FROM EndpointHit h
    WHERE h.timestamp BETWEEN :start AND :end
    AND (:uris IS NULL OR h.uri IN :uris)
    GROUP BY h.app, h.uri
    ORDER BY hits DESC
    """)
    List<Object[]> getUniqueHits(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("uris") Set<String> uris);

    @Query("""
    SELECT h.app AS app, h.uri AS uri, COUNT(h.id) AS hits
    FROM EndpointHit h
    WHERE h.timestamp BETWEEN :start AND :end
    AND (:uris IS NULL OR h.uri IN :uris)
    GROUP BY h.app, h.uri
    ORDER BY hits DESC
    """)
    List<Object[]> getAllHits(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end,
                              @Param("uris") Set<String> uris);
}
