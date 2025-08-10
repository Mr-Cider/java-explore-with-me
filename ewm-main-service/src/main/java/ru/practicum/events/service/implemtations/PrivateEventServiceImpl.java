package ru.practicum.events.service.implemtations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.dto.*;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.service.interfaces.PrivateEventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.repository.RequestRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final EventMapper eventMapper;

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        Page<Event> events = eventRepository.findEventByInitiator_Id(userId, PageRequest.of(from, size));
        List<EventShortDto> dtos = events.map(eventMapper::toEventShortDto).getContent();

        List<Long> eventIds = dtos.stream().map(EventShortDto::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.countConfirmedRequestsByEventIds(eventIds);

        dtos.forEach(dto -> dto.setConfirmedRequests(
                confirmedRequests.getOrDefault(dto.getId(), 0L)
        ));

        return dtos;
    }

    @Transactional
    @Override
    public EventFullDto createNewEvent(Long userId, NewEventDto newEventDto) {
        Event event = eventRepository.save(eventMapper.toNewEvent(userId, newEventDto));
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        return eventMapper.toEventFullDto(checkAndGetEvent(userId, eventId));
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = checkAndGetEvent(userId, eventId);
        if (!(event.getState().equals(State.PENDING) || event.getState().equals(State.CANCELED))) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        Event updatedEvent = eventRepository.save(eventMapper.toUpdateEventUserRequest(updateEventUserRequest, event));
        return eventMapper.toEventFullDto(updatedEvent);
    }

    private Event checkAndGetEvent(Long userId, Long eventId) {
        return eventRepository.findEventByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }
}
