package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.ParamCommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        Comment comment = commentMapper.toNewComment(user, event, newCommentDto);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public CommentDto updateUserComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        Comment comment = getCommentByIdAndAuthorIdAndEventId(commentId, user.getId(), event.getId());
        comment.setText(newCommentDto.getText());
        return commentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        Comment comment = getCommentByIdAndAuthorIdAndEventId(commentId, user.getId(), event.getId());
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        int pageNumber = from / size;
        Page<Comment> results = commentRepository.getByAuthor_Id(userId, PageRequest.of(pageNumber, size));
        return results.map(commentMapper::toCommentDto).getContent();
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId, int from, int size) {
        int pageNumber = from / size;
        Page<Comment> results = commentRepository.getByEvent_Id(eventId, PageRequest.of(pageNumber, size));
        return results.map(commentMapper::toCommentDto).getContent();
    }

    @Override
    public CommentDto getComment(Long commentId) {
        return commentMapper.toCommentDto(getCommentOrThrow(commentId));
    }

    @Transactional
    @Override
    public CommentDto moderationComment(Long commentId, NewCommentDto newCommentDto) {
        Comment comment = getCommentOrThrow(commentId);
        comment.setText(newCommentDto.getText());
        return commentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getAllComments(ParamCommentDto paramCommentDto, int from, int size) {
        int pageNumber = from / size;
        checkDate(paramCommentDto);
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Comment> comments = commentRepository.getAllEvents(
                paramCommentDto.getRangeStart(),
                paramCommentDto.getRangeEnd(),
                paramCommentDto.getText(),
                pageable
        );
        return comments.map(commentMapper::toCommentDto).getContent();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private Comment getCommentByIdAndAuthorIdAndEventId(Long commentId, Long authorId, Long eventId) {
        return commentRepository.getByIdAndAuthor_IdAndEvent_Id(commentId, authorId, eventId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id=" + commentId + " was not found"));
    }

    private void checkDate(ParamCommentDto paramCommentDto) {
        if (paramCommentDto.getRangeStart() == null) {
            paramCommentDto.setRangeStart(LocalDateTime.now());
        }
        if (paramCommentDto.getRangeEnd() == null) {
            paramCommentDto.setRangeEnd(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        }
        if (paramCommentDto.getRangeStart().isAfter(paramCommentDto.getRangeEnd())) {
            throw new BadRequestException("The end date should be after the start date.");
        }
    }
}