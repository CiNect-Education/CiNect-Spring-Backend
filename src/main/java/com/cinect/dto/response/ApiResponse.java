package com.cinect.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private T data;
    private Object meta;
    private String message;
    private Object error;
    @Builder.Default
    private String timestamp = Instant.now().toString();

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, Object meta) {
        return ApiResponse.<T>builder()
                .data(data)
                .meta(meta)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Object errorDetail) {
        return ApiResponse.<T>builder()
                .message(message)
                .error(errorDetail)
                .timestamp(Instant.now().toString())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .timestamp(Instant.now().toString())
                .build();
    }
}
