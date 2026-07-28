package com.restaurantbot.common.exception;

import com.restaurantbot.restaurant.exception.RestaurantNotFoundException;
import com.restaurantbot.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestaurantAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleRestaurantAlreadyExists(
            RestaurantAlreadyExistsException ex
            ) {

        return ApiResponse.<Void>builder()
            .success(false)
            .message(ex.getMessage())
            .data(null)
            .build();
            }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception ex) {

        return ApiResponse.<Void>builder()
            .success(false)
            .message(ex.getMessage())
            .build();
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleRestaurantNotFound(
            RestaurantNotFoundException ex) {

        return ApiResponse.<Void>builder()
            .success(false)
            .message(ex.getMessage())
            .build();
            }
}
