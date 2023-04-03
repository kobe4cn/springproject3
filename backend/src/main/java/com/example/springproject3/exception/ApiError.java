package com.example.springproject3.exception;

import java.time.LocalDateTime;

public record ApiError(String path, String message,
                       int statusCode,
                       LocalDateTime localDateTime) {
}
