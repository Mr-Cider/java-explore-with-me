package ru.practicum.events.dto.paramsDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.State;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminEventsParamDto {
        List<Long> users;

        List<State> states;

        List<Long> categories;

        LocalDateTime rangeStart;

        LocalDateTime rangeEnd;

        Integer from;

        Integer size;


}
