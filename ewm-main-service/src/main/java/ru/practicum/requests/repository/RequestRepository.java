package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.model.Status;
import ru.practicum.requests.model.Request;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = :eventId AND r.status = 'CONFIRMED'")
    long countConfirmedRequests(@Param("eventId") Long eventId);

    Optional<Request> findByIdAndRequester_Id(Long requestId, Long userId);

    List<Request> findByRequester_Id(Long userId);

    List<Request> findByEvent_Id(Long eventId);

    List<Request> findByEvent_IdAndStatus(Long eventId, Status status);

    Long countByEventIdAndStatus(Long eventId, Status status);

    @Query("SELECT r.event.id, COUNT(r) FROM Request r " +
            "WHERE r.event.id IN :eventIds AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id")
    Map<Long, Long> countConfirmedRequestsByEventIds(@Param("eventIds") List<Long> eventIds);
}

