package com.theBarber.TheBarber.handle;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Getter
@Log4j2
public class BarberException extends RuntimeException {

    private HttpStatus statusException;
    private ErrorBarberResponse bodyException;

    private BarberException(HttpStatus statusException, String message, Exception e) {
        super(message, e);
        this.statusException = statusException;
        this.bodyException = ErrorBarberResponse.builder()
                .message(message)
                .description(getDescription(e))
                .build();
    }

    public static BarberException build(HttpStatus statusException, String message) {
        return new BarberException(statusException, message, null);
    }

    public static BarberException build(HttpStatus statusException, String message, Exception e) {
        log.error("Exception: ", e);
        return new BarberException(statusException, message, e);
    }

    private String getDescription(Exception e) {
        return Optional.ofNullable(e)
                .map(BarberException::getMessageCause).orElse(null);
    }

    private static String getMessageCause(Exception e) {
        return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
    }



    public ResponseEntity<ErrorBarberResponse> buildErrorResponseEntity() {
        return ResponseEntity
                .status(statusException)
                .body(bodyException);
    }

    private static final long serialVersionUID = 1L;
}
