package ru.practicum.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam (required = false) Boolean pinned,
                                                @PositiveOrZero @RequestParam (defaultValue = "0") int from,
                                                @Positive @RequestParam (defaultValue = "10") int size) {
        log.debug("GET /compilations");
        log.info("Public: Получение подборок событий");
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.debug("GET /compilations/{}", compId);
        log.info("Public: Получение подборки событий по его id");
        return compilationService.getCompilation(compId);
    }
}
