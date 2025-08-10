package ru.practicum.events.service.interfaces;

import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.paramsDto.PublicEventsParamDto;

import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getEvents(PublicEventsParamDto publicEventsParamDto);

    EventFullDto getEvent(Long eventId);
}
