package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.ParamCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateUserComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto);

    void deleteComment(Long userId, Long eventId, Long commentId);

    List<CommentDto> getUserComments(Long userId, int from, int size);

    List<CommentDto> getEventComments(Long eventId, int from, int size);

    CommentDto getComment(Long commentId);

    CommentDto moderationComment(Long commentId, NewCommentDto newCommentDto);

    void deleteCommentByAdmin(Long commentId);

    List<CommentDto> getAllComments(ParamCommentDto paramCommentDto, int from, int size);
}

