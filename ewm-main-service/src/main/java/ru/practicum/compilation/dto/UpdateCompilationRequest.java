package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCompilationRequest {
    private List<Long> id;

    private Boolean pinned;

    @Size(min = 1)
    @Size(max = 50)
    private String title;


}
