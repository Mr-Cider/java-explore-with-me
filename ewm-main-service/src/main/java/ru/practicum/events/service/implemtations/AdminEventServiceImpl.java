package ru.practicum.events.service.implemtations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.events.dto.AdminStateAction;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.dto.paramsDto.AdminEventsParamDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.service.interfaces.AdminEventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    @Override
    public List<EventFullDto> getEvents(AdminEventsParamDto paramDto) {
        int pageNumber = paramDto.getFrom() / paramDto.getSize();

        Page<Object[]> results = eventRepository.findAdminEvents(
                paramDto.getUsers(),
                paramDto.getStates(),
                paramDto.getCategories(),
                paramDto.getRangeStart(),
                paramDto.getRangeEnd(),
                PageRequest.of(pageNumber, paramDto.getSize()));

        return results.getContent().stream()
                .map(result -> {
                    Event event = (Event) result[0];
                    Long confirmedRequests = (Long) result[1];
                    EventFullDto dto = eventMapper.toEventFullDto(event);
                    dto.setConfirmedRequests(confirmedRequests);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
        checkEventDate(event);
        checkEventState(event, updateEventAdminRequest);
        Event updatedEvent = eventRepository.save(eventMapper.toUpdateEventAdminRequest(updateEventAdminRequest, event));
        return eventMapper.toEventFullDto(updatedEvent);
    }

    private void checkEventDate(Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("The event cannot be published because it will take place in less than an hour.");
        }
    }

    private void checkEventState(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(AdminStateAction.REJECT_EVENT) &&
                    event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Cannot reject the published event");
            }
            if (event.getState().equals(State.PUBLISHED) || event.getState().equals(State.CANCELED)) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: " +
                        event.getState());
            }
        }
    }
}
