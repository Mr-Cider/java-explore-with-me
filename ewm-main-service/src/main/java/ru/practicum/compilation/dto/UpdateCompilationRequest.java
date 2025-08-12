package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {

    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;


}
