package ru.practicum.events.service.interfaces;

import ru.practicum.events.dto.*;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto createNewEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

}
