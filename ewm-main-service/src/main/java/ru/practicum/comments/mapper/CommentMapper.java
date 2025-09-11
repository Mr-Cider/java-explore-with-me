package ru.practicum.comments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class, EventMapper.class})
public interface CommentMapper {
    @Mapping(target = "id", source = "comment.id")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    Comment toNewComment(User author, Event event, NewCommentDto newCommentDto);

}
