package com.restaurantbot.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ErrorResponse {

    private final boolean success;

    private final String message;

    private final LocalDateTime timestamp;

    private final Map<String, String> errors;
}
