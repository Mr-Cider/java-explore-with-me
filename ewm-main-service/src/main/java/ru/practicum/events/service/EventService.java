package ru.practicum.events.service;

import ru.practicum.events.dto.*;
import ru.practicum.events.dto.paramsDto.AdminEventsParamDto;
import ru.practicum.events.dto.paramsDto.PublicEventsParamDto;

import java.util.List;

public interface EventService {
    List<EventFullDto> getAdminEvents(AdminEventsParamDto paramDto);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getPrivateEvents(Long userId, int from, int size);

    EventFullDto createNewEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> getPublicEvents(PublicEventsParamDto publicEventsParamDto);

    EventFullDto getEvent(Long eventId);
}

