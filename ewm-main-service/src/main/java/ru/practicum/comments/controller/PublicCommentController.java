package ru.practicum.comments.controller;


import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequestMapping
@Slf4j
@Validated
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getPublicComments(@PathVariable Long eventId,
                                              @PositiveOrZero @RequestParam (defaultValue = "0") int from,
                                              @Positive @RequestParam (defaultValue = "10") int size) {
        log.debug("GET /events/{eventId}/comments");
        log.info("Public: Получение всех комментариев к событию");
        return commentService.getEventComments(eventId, from, size);
    }

    @GetMapping("/comments/{commentId}")
    public CommentDto getComment(@PathVariable Long commentId) {
        log.debug("GET /comments/{commentId}");
        log.info("Public: Получение события по id");
        return commentService.getComment(commentId);
    }
}
