package ru.practicum.events.mapper;

import org.mapstruct.*;
import ru.practicum.category.model.Category;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.exception.BadRequestException;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UserService;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserService.class, UserMapper.class})
public interface EventMapper {

    @Mapping(target = "category", source = "newEventDto.category", qualifiedByName = "mapCategoryIdToCategory")
    @Mapping(target = "initiator", source = "userId", qualifiedByName = "mapInitiatorId")
    @Mapping(target = "lat", source = "newEventDto.location.lat")
    @Mapping(target = "lon", source = "newEventDto.location.lon")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "state", expression = "java(ru.practicum.events.model.State.PENDING)")
    Event toNewEvent(Long userId, NewEventDto newEventDto);

    @Mapping(target = "lat", source = "updateEventAdminRequest.location.lat")
    @Mapping(target = "lon", source = "updateEventAdminRequest.location.lon")
    @Mapping(target = "state", source = "stateAction", qualifiedByName = "mapAdminStateActionToState")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "category", source = "category", qualifiedByName = "mapCategoryIdToCategory")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event toUpdateEventAdminRequest(UpdateEventAdminRequest updateEventAdminRequest, @MappingTarget Event event);

    @Mapping(target = "lat", source = "updateEventUserRequest.location.lat")
    @Mapping(target = "lon", source = "updateEventUserRequest.location.lon")
    @Mapping(target = "state", source = "stateAction", qualifiedByName = "mapUserStateActionToState")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Event toUpdateEventUserRequest(UpdateEventUserRequest updateEventUserRequest, @MappingTarget Event event);

    @Mapping(target = "location", source = "event", qualifiedByName = "mapLatLonToLocationDto")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "confirmedRequests", ignore = true)
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    EventShortDto toEventShortDto(Event event);

    @Named("mapUserStateActionToState")
    default State mapUserStateActionToState(UserStateAction stateAction) {
        if (stateAction == null) {
            return null;
        }
        return switch (stateAction) {
            case SEND_TO_REVIEW -> State.PENDING;
            case CANCEL_REVIEW -> State.CANCELED;
            default -> throw new BadRequestException("Event must not be published");
        };
    }

    @Named("mapAdminStateActionToState")
    default State mapAdminStateActionToState(AdminStateAction stateAction) {
        if (stateAction == null) {
            return null;
        }
        return switch (stateAction) {
            case PUBLISH_EVENT -> State.PUBLISHED;
            case REJECT_EVENT -> State.CANCELED;
            default -> throw new BadRequestException("Event must not be published");
        };
    }

    @Named("mapCategoryIdToCategory")
    default Category mapCategoryIdToCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

    @Named("mapInitiatorId")
    default User mapInitiatorId(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("mapLatLonToLocationDto")
    default LocationDto mapLatLonToLocationDto(Event event) {
        if (event == null) {
            return null;
        }
        return new LocationDto(event.getLat(), event.getLon());
    }

    @AfterMapping
    default void mapCreatedOn(@MappingTarget Event event) {
        if (event.getCreatedOn() != null) {
            return;
        }
        event.setCreatedOn(LocalDateTime.now());
    }

    @AfterMapping
    default void mapViews(@MappingTarget Event event) {
        if (event.getViews() != null) {
            return;
        }
        event.setViews(0L);
    }

    @AfterMapping
    default void mapPublishedOn(@MappingTarget Event event) {
        if (!(event.getState().equals(State.PUBLISHED))) {
            return;
        }
        if (event.getPublishedOn() != null) {
            return;
        }
        event.setPublishedOn(LocalDateTime.now());
    }

}
