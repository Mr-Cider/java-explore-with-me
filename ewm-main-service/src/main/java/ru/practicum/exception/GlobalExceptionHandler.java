package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Collections;


 @RestControllerAdvice
 public class GlobalExceptionHandler {

     @ExceptionHandler(NotFoundException.class)
     @ResponseStatus(HttpStatus.NOT_FOUND)
     public ApiError handleNotFoundException(final NotFoundException e) {
         return new ApiError(
                 ErrorStatus.NOT_FOUND,
                 "The required object was not found.",
                 e.getMessage(),
                 Collections.emptyList());
     }

     @ExceptionHandler(BadRequestException.class)
     @ResponseStatus(HttpStatus.BAD_REQUEST)
     public ApiError handleBadRequestException(final BadRequestException e) {
         return new ApiError(
                 ErrorStatus.BAD_REQUEST,
                 "Incorrectly made request.",
                 e.getMessage(),
                 Collections.emptyList());
     }

     @ExceptionHandler(CategoryIsNotEmptyException.class)
     @ResponseStatus(HttpStatus.CONFLICT)
     public ApiError handleCategoryIsNotEmptyException(final CategoryIsNotEmptyException e) {
         return new ApiError(
                 ErrorStatus.CONFLICT,
                 "For the requested operation the conditions are not met.",
                 e.getMessage(),
                 Collections.emptyList());
     }

     @ExceptionHandler(MethodArgumentNotValidException.class)
     @ResponseStatus(HttpStatus.BAD_REQUEST)
     public ApiError handleIncorrectDateException(final MethodArgumentNotValidException e) {
         FieldError fieldError = e.getBindingResult().getFieldError();
         String message = String.format(
                 "Field: %s. Error: %s. Value: %s",
                 fieldError.getField(),
                 fieldError.getDefaultMessage(),
                 fieldError.getRejectedValue()
         );
         return new ApiError(
                 ErrorStatus.BAD_REQUEST,
                 "Incorrectly made request.",
                 message,
                 Collections.emptyList());
     }

     @ExceptionHandler(ConflictException.class)
     @ResponseStatus(HttpStatus.CONFLICT)
     public ApiError handleForbiddenException(final ConflictException e) {
         return new ApiError(
                 ErrorStatus.CONFLICT,
                 "For the requested operation the conditions are not met.",
                 e.getMessage(),
                 Collections.emptyList());
     }
 }
