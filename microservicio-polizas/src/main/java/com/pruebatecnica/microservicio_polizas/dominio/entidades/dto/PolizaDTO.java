package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Objeto de transferencia de datos (DTO) que representa una póliza base.
 * <p>
 * Utilizado para exponer los datos esenciales de una {@code Poliza} en las
 * respuestas de la API, evitando exponer directamente la entidad JPA.
 * </p>
 */
@Data
public class PolizaDTO {
    /** Identificador único de la póliza. */
    private Long id;
    /** Número único de referencia de negocio de la póliza. */
    private String numeroPoliza;
    /** Identificador del cliente propietario de la póliza. */
    private Integer clienteId;
    /** Identificador del tipo de póliza. */
    private Integer tipoPolizaId;
    /** Código del tipo de póliza (p. ej. {@code VIDA}, {@code SALUD}, {@code VEHICULO}). */
    private String tipoPolizaCodigo;
    /** Fecha de inicio de vigencia de la póliza. */
    private LocalDate fechaInicio;
    /** Fecha de fin de vigencia de la póliza. */
    private LocalDate fechaFin;
    /** Indica si la póliza está activa. */
    private Boolean estado;
    /** Fecha y hora de creación del registro de la póliza. */
    private LocalDateTime fechaCreacion;

    // Sub-objetos (vida/salud/vehiculo) pueden añadirse más adelante
}
