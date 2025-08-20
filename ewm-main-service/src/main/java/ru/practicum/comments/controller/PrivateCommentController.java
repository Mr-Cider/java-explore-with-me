package ru.practicum.comments.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}")
@Validated
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody NewCommentDto newCommentDto) {
        log.debug("POST /users/{userId}/events/{eventId}/comments");
        log.info("Private: Создание комментария");
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @PathVariable Long commentId,
                                    @RequestBody NewCommentDto updatedCommentDto) {
        log.debug("PATCH /users/{userId}/events/{eventId}/comments/{commentId}");
        log.info("Private: Редактирование комментария пользователем");
        return commentService.updateUserComment(userId, eventId, commentId, updatedCommentDto);
    }

    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        log.debug("DELETE /users/{userId}/events/{eventId}/comments/{commentId}");
        log.info("Private: Удаление комментария пользователем");
        commentService.deleteComment(userId, eventId, commentId);
    }

    @GetMapping("/comments")
    public List<CommentDto> getComments(@PathVariable Long userId,
                                        @PositiveOrZero @RequestParam (defaultValue = "0") int from,
                                        @Positive @RequestParam (defaultValue = "10") int size) {
        log.debug("GET /users/{userId}/comments");
        log.info("Private: Получение всех комментариев текущего пользователя");
        return commentService.getUserComments(userId, from, size);
    }
}
