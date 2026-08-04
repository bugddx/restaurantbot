package com.restaurantbot.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success;

    private final String message;

    private final T data;
}
