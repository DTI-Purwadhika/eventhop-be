package com.riri.eventhop.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class ApiResponse<T> {
    private int statusCode;
    private String status;
    private String message;
    private T data;

    public ApiResponse(int statusCode, String status, String message, T data) {
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(HttpStatus.OK.value(), "OK", message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Created", message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(status.value(), "Error", message, null));
    }
}
