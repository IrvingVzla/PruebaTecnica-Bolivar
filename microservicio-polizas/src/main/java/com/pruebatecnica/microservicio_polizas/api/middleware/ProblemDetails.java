package com.pruebatecnica.microservicio_polizas.api.middleware;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Objeto de respuesta estándar para errores de la API.
 * <p>
 * Sigue el formato Problem Details (RFC 7807) y es retornado por el
 * {@link GlobalExceptionHandler} ante cualquier excepción conocida o genérica.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDetails {
    /** Código HTTP del error (p. ej. 400, 404, 500). */
    private int status;
    /** Clasificación del error (p. ej. {@code Validation}, {@code NotFound}, {@code Server Error}). */
    private String type;
    /** Título corto del error. */
    private String title;
    /** Descripción detallada del error. */
    private String detail;
    /** Instante en que ocurrió el error. */
    private Instant timestamp;
    /** Lista de mensajes de error individuales (útil para errores de validación múltiple). */
    private List<String> errors;
}
