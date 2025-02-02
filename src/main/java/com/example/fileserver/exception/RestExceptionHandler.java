package com.example.fileserver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.fileserver.util.ExceptionStringUtil.INTERNAL_SERVER_ERROR_ERROR_TITLE;
import static com.example.fileserver.util.ExceptionStringUtil.NOT_FOUND_ERROR_TITLE;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<ApplicationError> handleException(Exception e) {
        var ApplicationError = new ApplicationError(INTERNAL_SERVER_ERROR_ERROR_TITLE, "Внутренняя ошибка сервера");
        return new ResponseEntity<>(ApplicationError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ApplicationError> handleNoResourceFoundException(ObjectNotFoundException e) {
        var ApplicationError = new ApplicationError(NOT_FOUND_ERROR_TITLE, e.getMessage());
        return new ResponseEntity<>(ApplicationError, HttpStatus.NOT_FOUND);
    }
}
