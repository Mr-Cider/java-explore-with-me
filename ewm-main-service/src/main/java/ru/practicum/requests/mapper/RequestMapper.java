package ru.practicum.requests.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.service.RequestService;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = RequestService.class)
public interface RequestMapper {

    @Mapping(target = "event", source = "request.event.id")
    @Mapping(target = "requester", source = "request.requester.id")
    ParticipationRequestDto toParticipationRequestDto(Request request);
}
