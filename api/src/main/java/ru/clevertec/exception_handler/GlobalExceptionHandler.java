package ru.clevertec.exception_handler;

import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.clevertec.api.ApiResponse;
import ru.clevertec.exception.NotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return ApiResponse.<ErrorResponse>builder()
                .message(ex.getMessage())
                .status(false)
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return ApiResponse.<ErrorResponse>builder()
                .message(ex.getMessage())
                .status(false)
                .build();
    }
}
