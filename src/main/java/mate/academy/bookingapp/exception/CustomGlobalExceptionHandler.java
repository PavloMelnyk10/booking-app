package mate.academy.bookingapp.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();

        Map<String, Object> body = createErrorBody(
                status.value(),
                "Validation Failed",
                errors
        );

        return new ResponseEntity<>(body, headers, status);
    }

    private String getErrorMessage(ObjectError e) {
        if (e instanceof FieldError) {
            String fieldName = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            return fieldName + ": " + message;
        }
        return e.getDefaultMessage();
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Object> handleRegistrationException(RegistrationException e) {
        Map<String, Object> body = createErrorBody(
                HttpStatus.CONFLICT.value(),
                "Registration Error",
                List.of(e.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request) {

        Map<String, Object> body = createErrorBody(
                HttpStatus.NOT_FOUND.value(),
                "Entity Not Found",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<Object> handleDuplicateEntityException(DuplicateEntityException ex) {
        Map<String, Object> body = createErrorBody(
                HttpStatus.CONFLICT.value(),
                "Duplicate Entity",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<Object> handleInvalidOperationException(InvalidOperationException ex) {
        Map<String, Object> body = createErrorBody(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Operation",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccommodationFullyBookedException.class)
    public ResponseEntity<Object> fullyBookedException(AccommodationFullyBookedException ex) {
        Map<String, Object> body = createErrorBody(
                HttpStatus.CONFLICT.value(),
                "Fully booked",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = createErrorBody(
                HttpStatus.BAD_REQUEST.value(),
                "error",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        Map<String, Object> body = createErrorBody(
                HttpStatus.BAD_REQUEST.value(),
                "error",
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> createErrorBody(int status, String error, List<String> messages) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("messages", messages);
        return body;
    }
}
