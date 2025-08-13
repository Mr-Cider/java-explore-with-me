package ru.practicum.stat.mapper;

import ru.practicum.stat.HitDto;
import org.mapstruct.MappingConstants;
import ru.practicum.stat.model.EndpointHit;

@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HitMapper {

       HitDto toHitDto(EndpointHit hit);

       EndpointHit toHit(HitDto hitDto);

}

