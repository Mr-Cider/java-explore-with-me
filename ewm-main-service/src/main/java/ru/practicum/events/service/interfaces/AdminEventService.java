package ru.practicum.events.service.interfaces;

import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.dto.paramsDto.AdminEventsParamDto;

import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEvents(AdminEventsParamDto paramDto);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
