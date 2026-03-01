package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de resumen para una póliza base.
 * <p>
 * Contiene los campos mínimos necesarios para listar pólizas sin incluir
 * la información detallada de especializaciones (vida, salud, vehículo).
 * </p>
 */
@Data
public class PolizaResumenDTO {
    /** Identificador único de la póliza. */
    private Long id;
    /** Número de referencia de negocio de la póliza. */
    private String numeroPoliza;
    /** Identificador del tipo de póliza. */
    private Integer tipoPolizaId;
    /** Código del tipo de póliza. */
    private String tipoPolizaCodigo;
    /** Fecha de inicio de vigencia. */
    private LocalDate fechaInicio;
    /** Fecha de fin de vigencia. */
    private LocalDate fechaFin;
    /** Monto base de la póliza. */
    private BigDecimal montoBase;
    /** Indica si la póliza está activa. */
    private Boolean estado;
}
