package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class NewCompilationDto {
    private List<Integer> events;

    private Boolean pinned;

    @NotBlank
    private String title;
}
