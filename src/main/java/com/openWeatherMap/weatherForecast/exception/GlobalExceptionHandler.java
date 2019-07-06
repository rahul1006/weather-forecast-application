package com.openWeatherMap.weatherForecast.exception;

import com.openWeatherMap.weatherForecast.domain.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static com.openWeatherMap.weatherForecast.constants.WeatherConstant.*;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {


    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ExceptionResponse> handleMissingPathVariableException(MissingPathVariableException ipe) {
        String message = ipe.getVariableName() != null ? MANDATORY_CITY_MSG.replace("{variable}", ipe.getVariableName()) : WENT_WRONG_MESSAGE;
        return new ResponseEntity<>(new ExceptionResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : RESOURCE_NOT_FOUND_MESSAGE;
        return new ResponseEntity<>(new ExceptionResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnhandledException(Exception ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : WENT_WRONG_MESSAGE;
        return new ResponseEntity<>(new ExceptionResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentException(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ExceptionResponse exceptionResponse = new ExceptionResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), String.join(", ", errors));
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        ExceptionResponse exceptionResponse = new ExceptionResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), String.join(", ", errors));
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}