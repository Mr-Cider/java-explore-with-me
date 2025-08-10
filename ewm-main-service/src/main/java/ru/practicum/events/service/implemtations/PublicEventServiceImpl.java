package ru.practicum.events.service.implemtations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.paramsDto.PublicEventsParamDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.service.interfaces.PublicEventService;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final EventMapper eventMapper;

    @Override
    public List<EventShortDto> getEvents(PublicEventsParamDto publicEventsParamDto) {
        log.info("============================================================");
        log.info("from = " + publicEventsParamDto.getFrom());
        log.info("size = " + publicEventsParamDto.getSize());
        log.info("============================================================");
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
        Map<Long, Long> confirmedRequests = requestRepository.countConfirmedRequestsByEventIds(eventIds);
        dtos.forEach(dto -> dto.setConfirmedRequests(
                confirmedRequests.getOrDefault(dto.getId(), 0L)
        ));
        return dtos;
    }

    @Override
    public EventFullDto getEvent(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
        EventFullDto dto = eventMapper.toEventFullDto(event);
        dto.setConfirmedRequests(requestRepository.countConfirmedRequests(eventId));
        return dto;
    }

    private void checkDate(PublicEventsParamDto publicEventsParamDto) {
        if (publicEventsParamDto.getRangeStart() == null && publicEventsParamDto.getRangeEnd() == null) {
            return;
        }
        if (publicEventsParamDto.getRangeStart() == null) {
            publicEventsParamDto.setRangeStart(LocalDateTime.now());
        }
        if (publicEventsParamDto.getRangeEnd() == null) {
            publicEventsParamDto.setRangeEnd(null);
        }
        if (publicEventsParamDto.getRangeStart() != null && publicEventsParamDto.getRangeEnd() != null
                && publicEventsParamDto.getRangeStart().isAfter(publicEventsParamDto.getRangeEnd())) {
            throw new BadRequestException("The end date should be after the start date.");
        }
    }
}
