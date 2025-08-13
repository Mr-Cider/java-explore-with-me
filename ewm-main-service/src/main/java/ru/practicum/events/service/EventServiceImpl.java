package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.dto.*;
import ru.practicum.events.dto.paramsDto.AdminEventsParamDto;
import ru.practicum.events.dto.paramsDto.PublicEventsParamDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.statsIntegration.StatsIntegrationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final RequestRepository requestRepository;

    private final StatsIntegrationService statsIntegrationService;


    @Override
    public List<EventFullDto> getAdminEvents(AdminEventsParamDto paramDto) {
        int pageNumber = paramDto.getFrom() / paramDto.getSize();

        LocalDateTime rangeStart = paramDto.getRangeStart() != null ?
                paramDto.getRangeStart() : LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime rangeEnd = paramDto.getRangeEnd() != null ?
                paramDto.getRangeEnd() : LocalDateTime.of(2100, 1, 1, 0, 0);

        Page<Object[]> results = eventRepository.findAdminEvents(
                paramDto.getUsers(),
                paramDto.getStates(),
                paramDto.getCategories(),
                rangeStart,
                rangeEnd,
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
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
        checkEventDate(event);
        checkEventState(event, updateEventAdminRequest);
        Event updatedEvent = eventRepository.save(eventMapper.toUpdateEventAdminRequest(updateEventAdminRequest, event));
        return eventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> getPrivateEvents(Long userId, Integer from, Integer size) {
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
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = checkAndGetEvent(userId, eventId);
        if (!(event.getState().equals(State.PENDING) || event.getState().equals(State.CANCELED))) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        Event updatedEvent = eventRepository.save(eventMapper.toUpdateEventUserRequest(updateEventUserRequest, event));
        return eventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    @Transactional
    public List<EventShortDto> getPublicEvents(PublicEventsParamDto publicEventsParamDto) {
        int pageNumber = publicEventsParamDto.getFrom() / publicEventsParamDto.getSize();
        List<Long> categories = publicEventsParamDto.getCategories();
        checkDate(publicEventsParamDto);
        String sortField = "EVENT_DATE".equals(publicEventsParamDto.getSort())
                ? "eventDate"
                : publicEventsParamDto.getSort();
        Sort sort = Sort.by(sortField);
        Pageable pageable = PageRequest.of(pageNumber, publicEventsParamDto.getSize(), sort);

        Page<Event> events = eventRepository.getEvents(
                publicEventsParamDto.getText(),
                categories != null && categories.isEmpty() ? null : categories,
                publicEventsParamDto.getPaid(),
                publicEventsParamDto.getRangeStart(),
                publicEventsParamDto.getRangeEnd(),
                publicEventsParamDto.getOnlyAvailable(),
                State.PUBLISHED,
                pageable);

        List<EventShortDto> dtos = events.map(eventMapper::toEventShortDto).getContent();
        List<Long> eventIds = dtos.stream().map(EventShortDto::getId).collect(Collectors.toList());

        Map<Long, Long> views = statsIntegrationService.getViewsForEvents(eventIds);
        Map<Long, Long> confirmedRequests = requestRepository.countConfirmedRequestsByEventIds(eventIds);

        dtos.forEach(dto -> {
            dto.setConfirmedRequests(confirmedRequests.getOrDefault(dto.getId(), 0L));
            dto.setViews(views.getOrDefault(dto.getId(), 0L));
        });

        return dtos;
    }

    @Override
    @Transactional
    public EventFullDto getEvent(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        EventFullDto dto = eventMapper.toEventFullDto(event);
        dto.setConfirmedRequests(requestRepository.countConfirmedRequests(eventId));
        dto.setViews(statsIntegrationService.getViewsForEvents(List.of(eventId)).getOrDefault(eventId, 0L));

        return dto;
    }

    private void checkDate(PublicEventsParamDto publicEventsParamDto) {
        if (publicEventsParamDto.getRangeStart() == null) {
            publicEventsParamDto.setRangeStart(LocalDateTime.now());
        }
        if (publicEventsParamDto.getRangeEnd() == null) {
            publicEventsParamDto.setRangeEnd(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        }
        if (publicEventsParamDto.getRangeStart().isAfter(publicEventsParamDto.getRangeEnd())) {
            throw new BadRequestException("The end date should be after the start date.");
        }
    }

    private Event checkAndGetEvent(Long userId, Long eventId) {
        return eventRepository.findEventByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
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
