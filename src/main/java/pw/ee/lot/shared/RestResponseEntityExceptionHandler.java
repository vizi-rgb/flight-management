package pw.ee.lot.shared;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
class RestResponseEntityExceptionHandler {

    private static final String RESPONSE_ERRORS_KEY = "errors";

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, List<String>>> handleIllegalStateException(IllegalStateException exception) {
        return createResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, List<String>>> handleIllegalArgumentException(IllegalArgumentException exception) {
        return createResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, List<String>>> handleNoSuchElementException(NoSuchElementException exception) {
        return createResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        final var errors = exception.getBindingResult().getAllErrors().stream()
            .map(error -> String.format("%s %s",
                ((FieldError) error).getField(),
                error.getDefaultMessage()
            ))
            .toList();
        return createResponse(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationException(ValidationException exception) {
        return createResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, List<String>>> handleHttpMessageNotReadableException(HttpMessageConversionException exception) {
        return createResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, List<String>>> handleException(Exception exception) {
        log.error("Internal server error", exception);
        return createResponse("Intenal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, List<String>>> createResponse(List<String> errors, HttpStatus status) {
        final var content = Map.of(RESPONSE_ERRORS_KEY, errors);
        return new ResponseEntity<>(content, new HttpHeaders(), status);
    }

    private ResponseEntity<Map<String, List<String>>> createResponse(String message, HttpStatus status) {
        return createResponse(List.of(message), status);
    }

}