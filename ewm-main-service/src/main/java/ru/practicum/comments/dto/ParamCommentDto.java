package ru.practicum.comments.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ParamCommentDto {
    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    private String text;
}
