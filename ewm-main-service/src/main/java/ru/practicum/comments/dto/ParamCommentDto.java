package ru.practicum.comments.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ParamCommentDto {
    LocalDateTime rangeStart;

    LocalDateTime rangeEnd;

    String text;
}
