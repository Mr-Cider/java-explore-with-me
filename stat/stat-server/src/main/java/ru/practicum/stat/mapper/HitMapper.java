package ru.practicum.stat.mapper;

import org.mapstruct.MappingConstants;
import ru.practicum.stat.HitDto;
import ru.practicum.stat.model.EndpointHit;

@org.mapstruct.Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HitMapper {

       HitDto toHitDto (EndpointHit hit);

       EndpointHit toHit (HitDto hitDto);

}

