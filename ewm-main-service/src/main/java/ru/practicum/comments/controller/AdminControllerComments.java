package ru.practicum.comments.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.ParamCommentDto;
import ru.practicum.comments.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminControllerComments {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto moderationComment(@PathVariable Long commentId, @RequestBody NewCommentDto newCommentDto) {
        log.debug("PATCH admin/comments/{commentId}");
        log.info("Admin: Модерация комментария");
        return commentService.moderationComment(commentId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        log.debug("DELETE admin/comments/{commentId}");
        log.info("Admin: Удаление комментария администратором");
        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping
    public List<CommentDto> getComments( @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(required = false) String text,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        log.debug("GET admin/comments");
        log.info("Admin: Получение всех комментариев за определенный период времени");
        ParamCommentDto paramCommentDto = ParamCommentDto.builder()
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .text(text)
                .build();
        return commentService.getAllComments(paramCommentDto, from, size);
    }
}
