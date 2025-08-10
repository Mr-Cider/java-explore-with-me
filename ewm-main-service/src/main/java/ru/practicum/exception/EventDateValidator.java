package ru.practicum.exception;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDateNotToEarly, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {
        if (eventDate == null) {
            return true;
        }
        LocalDateTime minAllowedTime = LocalDateTime.now().plusHours(2);
        return eventDate.isAfter(minAllowedTime);
    }
}

