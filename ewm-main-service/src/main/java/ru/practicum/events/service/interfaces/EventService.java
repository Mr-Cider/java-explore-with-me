package ru.practicum.events.service.interfaces;

import ru.practicum.events.dto.*;
import ru.practicum.events.dto.paramsDto.AdminEventsParamDto;
import ru.practicum.events.dto.paramsDto.PublicEventsParamDto;

import java.util.List;

public interface EventService {
    List<EventFullDto> getEvents(AdminEventsParamDto paramDto);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    //------------------

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto createNewEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    //-------------------

    List<EventShortDto> getEvents(PublicEventsParamDto publicEventsParamDto);

    EventFullDto getEvent(Long eventId);
}

