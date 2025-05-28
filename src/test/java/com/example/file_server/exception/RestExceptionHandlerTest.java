package com.example.file_server.exception;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestExceptionHandlerTest {

    private RestExceptionHandler handler;

    @BeforeEach
    void setUp() throws Exception {
        handler = new RestExceptionHandler();
        Field maxSizeField = RestExceptionHandler.class.getDeclaredField("maxSize");
        maxSizeField.setAccessible(true);
        maxSizeField.set(handler, "1MB");
    }

    @Test
    void handleException_returnsInternalServerError() {
        Exception ex = new Exception("something went wrong");
        ResponseEntity<ApplicationError> resp = handler.handleException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), body.getTitle());
        assertEquals("Внутренняя ошибка сервера", body.getMessage());
        assertNull(body.getMessages());
    }

    @Test
    void handleMaxUploadSizeExceededException_returnsPayloadTooLarge() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(42);
        ResponseEntity<ApplicationError> resp = handler.handleMaxUploadSizeExceededException(ex);

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, resp.getStatusCode());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE.toString(), body.getTitle());
        assertEquals("Файл слишком большой! Максимальный разрешенный размер: 1MB.", body.getMessage());
    }

    @Test
    void handleNoResourceFoundException_returnsNotFound() {
        ObjectNotFoundException ex = new ObjectNotFoundException("Entity with id=99 not found");
        ResponseEntity<ApplicationError> resp = handler.handleNoResourceFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND.toString(), body.getTitle());
        assertEquals("Entity with id=99 not found", body.getMessage());
    }

    @Test
    void handleIllegalArgumentExceptionException_forIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("bad argument");
        ResponseEntity<ApplicationError> resp = handler.handleIllegalArgumentExceptionException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.toString(), body.getTitle());
        assertEquals("bad argument", body.getMessage());
    }

    @Test
    void handleIllegalArgumentExceptionException_forMediaTypeNotSupported() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("application/xml");
        ResponseEntity<ApplicationError> resp = handler.handleIllegalArgumentExceptionException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.toString(), body.getTitle());
        assertEquals("application/xml", body.getMessage());
    }

    @Test
    void handleIllegalArgumentExceptionException_forMissingServletPart() {
        MissingServletRequestPartException ex = new MissingServletRequestPartException("file");
        ResponseEntity<ApplicationError> resp = handler.handleIllegalArgumentExceptionException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.toString(), body.getTitle());
        assertTrue(body.getMessage().contains("file"));
    }
}