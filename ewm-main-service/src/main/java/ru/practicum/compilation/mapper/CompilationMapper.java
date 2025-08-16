package ru.practicum.compilation.mapper;

import org.mapstruct.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {

    @Mapping(target = "events", expression = "java(mapEvents(newCompilationDto.getEvents()))")
    Compilation toCompilation(NewCompilationDto newCompilationDto);

    @Mapping(target = "events", source = "events")
    CompilationDto toCompilationDto(Compilation compilation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", expression = "java(updateCompilationRequest.getEvents() != null ? " +
            "mapEvents(updateCompilationRequest.getEvents()) : compilation.getEvents())")
    Compilation toUpdateCompilation(UpdateCompilationRequest updateCompilationRequest,
                                    @MappingTarget Compilation compilation);

    default Set<Event> mapEvents(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return null;
        }
        return eventIds.stream()
                .map(eventId -> {
                    Event event = new Event();
                    event.setId(eventId);
                    return event;
                })
                .collect(Collectors.toSet());
    }

    default List<EventShortDto> mapEventsToShortDtos(Set<Event> events) {
        if (events == null) {
            return Collections.emptyList();
        }
        return events.stream()
                .map(event -> {
                    EventShortDto dto = new EventShortDto();
                    dto.setId(event.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

