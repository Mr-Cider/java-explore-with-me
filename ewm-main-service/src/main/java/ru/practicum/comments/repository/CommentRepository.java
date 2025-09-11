package ru.practicum.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.comments.model.Comment;

import java.time.LocalDateTime;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> getByIdAndAuthor_IdAndEvent_Id(Long id, Long authorId, Long eventId);

    Page<Comment> getByAuthor_Id(@Param ("authorId")Long authorId, Pageable pageable);

    Page<Comment> getByEvent_Id(@Param("eventId") Long eventId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE " +
            "(:text IS NULL OR (" +
            "   LOWER(c.text) LIKE LOWER(CONCAT('%', CAST(:text AS string), '%')))) AND " +
            "c.created >= :rangeStart AND " +
            "c.created <= :rangeEnd")
    Page<Comment> getAllEvents(@Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd,
                               @Param("text") String text,
                               Pageable pageable);
}
