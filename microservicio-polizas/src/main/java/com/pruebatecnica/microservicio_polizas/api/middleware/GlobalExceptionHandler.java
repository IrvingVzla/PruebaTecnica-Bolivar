package com.pruebatecnica.microservicio_polizas.api.middleware;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Manejador global de excepciones para todos los controladores REST.
 * <p>
 * Intercepta las excepciones más comunes del ciclo de vida de una solicitud HTTP
 * y las convierte en respuestas {@link ProblemDetails} con el código HTTP apropiado,
 * evitando que los stacktraces se filtren al cliente.
 * </p>
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /** Logger para registrar los errores interceptados. */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja errores de validación de campos en el cuerpo de la solicitud
     * ({@code @Valid} sobre parámetros de controlador).
     *
     * @param ex excepción lanzada por Spring al fallar la validación de argumentos
     * @return respuesta 400 con el detalle de cada campo inválido
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<ProblemDetails> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage(), ex);

        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        ProblemDetails problem = ProblemDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .type("Validation")
                .title("Validation Error")
                .detail("Request validation failed")
                .timestamp(Instant.now())
                .errors(errors)
                .build();

        return new ResponseEntity<>(problem, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja violaciones de restricciones de Bean Validation
     * ({@link ConstraintViolationException}).
     *
     * @param ex excepción con el conjunto de violaciones de restricciones
     * @return respuesta 400 con el detalle de cada violación
     */
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<ProblemDetails> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage(), ex);
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
            errors.add(cv.getPropertyPath() + ": " + cv.getMessage());
        }

        ProblemDetails problem = ProblemDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .type("Validation")
                .title("Validation Error")
                .detail("Constraint violations occurred")
                .timestamp(Instant.now())
                .errors(errors)
                .build();

        return new ResponseEntity<>(problem, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de validación de negocio lanzadas explícitamente
     * desde los servicios ({@link ValidationException}).
     *
     * @param ex excepción con el mensaje de error de negocio
     * @return respuesta 400 con el mensaje de la excepción como detalle
     */
    @ExceptionHandler({ ValidationException.class })
    public ResponseEntity<ProblemDetails> handleValidationException(ValidationException ex) {
        log.error("Validation exception: {}", ex.getMessage(), ex);

        ProblemDetails problem = ProblemDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .type("Validation")
                .title("Validation Error")
                .detail(ex.getMessage())
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja solicitudes con JSON mal formado o ilegible.
     *
     * @param ex excepción indicando que el cuerpo de la solicitud no pudo ser parseado
     * @return respuesta 400 con la causa más específica disponible
     */
    @ExceptionHandler({ HttpMessageNotReadableException.class })
    public ResponseEntity<ProblemDetails> handleNotReadable(HttpMessageNotReadableException ex) {
        log.error("Malformed JSON request: {}", ex.getMessage(), ex);
        ProblemDetails problem = ProblemDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .type("MalformedRequest")
                .title("Malformed JSON request")
                .detail(ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja violaciones de integridad de datos en la base de datos
     * (p. ej. clave única duplicada).
     *
     * @param ex excepción de integridad de datos lanzada por Spring Data
     * @return respuesta 400 con la causa más específica disponible
     */
    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<ProblemDetails> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);
        ProblemDetails problem = ProblemDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .type("DataIntegrity")
                .title("Data integrity violation")
                .detail(ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja el caso en que un recurso solicitado no fue encontrado
     * ({@link NoSuchElementException}).
     *
     * @param ex excepción indicando que el recurso no existe
     * @return respuesta 400 con el mensaje de la excepción
     */
    @ExceptionHandler({ NoSuchElementException.class })
    public ResponseEntity<ProblemDetails> handleNotFound(NoSuchElementException ex) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        ProblemDetails problem = ProblemDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .type("NotFound")
                .title("Resource not found")
                .detail(ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
    }

    /**
     * Manejador de último recurso que captura cualquier excepción no tratada
     * por los handlers anteriores.
     *
     * @param ex excepción genérica no manejada
     * @return respuesta 500 con un mensaje genérico de error interno
     */
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<ProblemDetails> handleAll(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        ProblemDetails problem = ProblemDetails.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .type("Server Error")
                .title("Internal Server Error")
                .detail("An internal server error has occurred")
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(problem, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
