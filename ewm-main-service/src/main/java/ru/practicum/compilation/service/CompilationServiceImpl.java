package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final CompilationMapper compilationMapper;

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        Compilation savedCompilation = compilationRepository.save(compilation);
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            savedCompilation.setEvents(compilation.getEvents());
        }
        return compilationMapper.toCompilationDto(savedCompilation);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        if (!(compilationRepository.existsById(compId))) {
            throw new NotFoundException("Compilation with id=" + compId + " not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + compId + " not found"));
        Compilation updatedCompilation = compilationRepository.save(
                compilationMapper.toUpdateCompilation(updateCompilationRequest, compilation));
        return compilationMapper.toCompilationDto(updatedCompilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        int pageNumber = from / size;
        Page<Compilation> results = compilationRepository.getCompilations(
                pinned,
                PageRequest.of(pageNumber, size)
        );
        return results.map(compilationMapper::toCompilationDto).getContent();
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        return compilationMapper.toCompilationDto(compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + compId + " not found")));
    }
}
