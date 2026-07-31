package com.restaurantbot.common.exception;

import com.restaurantbot.restaurant.exception.RestaurantNotFoundException;
import com.restaurantbot.restaurant.exception.RestaurantAlreadyExistsException;
import com.restaurantbot.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestaurantAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleRestaurantAlreadyExists(RestaurantAlreadyExistsException ex) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleRestaurantNotFound(RestaurantNotFoundException ex) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ApiResponse.<Void>builder()
                .success(false)
                .message(message.isBlank() ? "Validation failed" : message)
                .data(null)
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception ex) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message("Unexpected error occurred")
                .data(null)
                .build();
    }
}
