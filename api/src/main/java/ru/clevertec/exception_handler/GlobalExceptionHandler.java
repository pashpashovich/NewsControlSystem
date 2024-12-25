package ru.clevertec.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.clevertec.api.ApiResponse;
import ru.clevertec.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNotFoundException(NotFoundException ex) {
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .message(ex.getMessage())
                .status(false)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return ApiResponse.<ErrorResponse>builder()
                .message(ex.getMessage())
                .status(false)
                .build();
    }
}
