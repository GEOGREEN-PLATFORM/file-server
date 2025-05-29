package com.example.file_server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxSize;

    private void logTheException(Exception e) {
        log.error("Exception: {} handled normally. Message: {}", e.getClass().getName(), e.getMessage());
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<ApplicationError> handleException(Exception e) {
        logTheException(e);
        var ApplicationError = new ApplicationError(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Внутренняя ошибка сервера");
        return new ResponseEntity<>(ApplicationError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApplicationError> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logTheException(e);
        var ApplicationError = new ApplicationError(HttpStatus.PAYLOAD_TOO_LARGE.toString(), String.format("Файл слишком большой! Максимальный разрешенный размер: %s.", maxSize));
        return new ResponseEntity<>(ApplicationError, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ApplicationError> handleNoResourceFoundException(ObjectNotFoundException e) {
        logTheException(e);
        var ApplicationError = new ApplicationError(HttpStatus.NOT_FOUND.toString(), e.getMessage());
        return new ResponseEntity<>(ApplicationError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMediaTypeNotSupportedException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ApplicationError> handleIllegalArgumentExceptionException(Exception e) {
        logTheException(e);
        var ApplicationError = new ApplicationError(HttpStatus.BAD_REQUEST.toString(), e.getMessage());
        return new ResponseEntity<>(ApplicationError, HttpStatus.BAD_REQUEST);
    }
}
