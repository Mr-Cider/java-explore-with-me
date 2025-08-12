package ru.practicum.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategory_Id(Long categoryId);

    Page<Event> findEventByInitiator_Id(Long initiatorId, Pageable pageable);

    Optional<Event> findEventByIdAndInitiator_Id(Long id, Long initiatorId);

    @Query("SELECT e, COUNT(r.id) FROM Event e " +
            "LEFT JOIN Request r ON e.id = r.event.id AND r.status = 'CONFIRMED' " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) AND " +
            "(:states IS NULL OR e.state IN :states) AND " +
            "(:categories IS NULL OR e.category.id IN :categories) AND " +
            "(:rangeStart IS NULL OR e.eventDate >= :rangeStart) AND " +
            "(:rangeEnd IS NULL OR e.eventDate <= :rangeEnd) " +
            "GROUP BY e")
    Page<Object[]> findAdminEvents(
            @Param("users") List<Long> users,
            @Param("states") List<State> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    @EntityGraph(attributePaths = "initiator")
    Optional<Event> findEventWithInitiatorById(Long id);

    Optional<Event> findByIdAndState(Long eventId, State state);

    @Query("SELECT e FROM Event e WHERE " +
            "e.state = :state AND " +
            "(:onlyAvailable IS NULL OR :onlyAvailable = false OR e.participantLimit > " +
            "(SELECT COUNT(r) FROM Request r WHERE r.event = e AND r.status = 'CONFIRMED')) AND " +
            "(:text IS NULL OR " +
            "(LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "(LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))))) AND " +
            "(:categories IS NULL OR e.category.id IN :categories) AND " +
            "(:paid IS NULL OR e.paid = :paid) AND " +
            "(:rangeStart IS NULL OR e.eventDate >= :rangeStart) AND " +
            "(:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)")
    Page<Event> getEvents(@Param("text") String text,
                          @Param("categories") List<Long> categories,
                          @Param("paid") Boolean paid,
                          @Param("rangeStart") LocalDateTime rangeStart,
                          @Param("rangeEnd") LocalDateTime rangeEnd,
                          @Param("onlyAvailable") Boolean onlyAvailable,
                          @Param("state") State state,
                          Pageable pageable);
}

