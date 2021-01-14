package org.example.restaurant.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            stringBuilder.append(fieldError.getField());
            stringBuilder.append(": ");
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append("\n");
        });
        log.info(stringBuilder.toString());
        return ResponseEntity.unprocessableEntity().body(stringBuilder.toString());
    }

    @ExceptionHandler({NoSuchElementException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<?> handleEmptyResultDataAccessException(RuntimeException ex) {
        log.info(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.info(ex.getMessage());
        return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
}
