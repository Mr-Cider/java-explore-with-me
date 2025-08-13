package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.model.Status;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        List<Request> requests = requestRepository.findByRequester_Id(userId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).toList();
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = eventRepository.findEventWithInitiatorById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        validateRequest(userId, event);
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(userRepository.getReferenceById(userId));
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequester_Id(requestId, userId).orElseThrow(() ->
                new NotFoundException("Request with id=" + requestId + " was not found"));
        request.setStatus(Status.CANCELED);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventOwnerRequests(Long userId, Long eventId) {
        Event event = eventRepository.findEventWithInitiatorById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
        List<Request> requests = requestRepository.findByEvent_Id(eventId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).toList();
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId,
                                                               EventRequestStatusUpdateRequest update) {
        if (update.getRequestIds() == null || update.getRequestIds().isEmpty()) {
            throw new BadRequestException("Request ids cannot be empty");
        }

        Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Event must be published");
        }

        List<Request> requests = requestRepository.findAllById(update.getRequestIds());

        requests.forEach(request -> {
            if (request.getStatus() != Status.PENDING) {
                throw new ConflictException("Request with id=" + request.getId() + " has already been processed");
            }
            if (!request.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Request with id=" + request.getId() + " belongs to another event");
            }
        });

        if (update.getStatus() == Status.CONFIRMED) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
            if (event.getParticipantLimit() != 0 &&
                    confirmedCount + requests.size() > event.getParticipantLimit()) {
                throw new ConflictException("Participant limit exceeded");
            }
        }

        requests.forEach(request -> request.setStatus(update.getStatus()));
        requestRepository.saveAll(requests);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        List<Request> confirmedRequests = update.getStatus() == Status.CONFIRMED
                ? requests
                : requestRepository.findByEvent_IdAndStatus(eventId, Status.CONFIRMED);
        result.setConfirmedRequests(confirmedRequests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList()));

        List<Request> rejectedRequests = update.getStatus() == Status.REJECTED
                ? requests
                : requestRepository.findByEvent_IdAndStatus(eventId, Status.REJECTED);
        result.setRejectedRequests(rejectedRequests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList()));

        return result;
    }

    private void validateRequest(Long userId, Event event) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator can't request participation in own event");
        }

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Event must be published");
        }

        if (requestRepository.existsByEventIdAndRequesterId(event.getId(), userId)) {
            throw new ConflictException("Participation request already exists");
        }

        if (event.getParticipantLimit() > 0 &&
                event.getParticipantLimit() <= requestRepository.countConfirmedRequests(event.getId())) {
            throw new ConflictException("Participant limit reached");
        }
    }
}
