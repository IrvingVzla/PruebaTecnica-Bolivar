package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas;

import lombok.Data;

import java.time.LocalDate;

/**
 * Parámetros de filtro generales para consultar pólizas.
 * <p>
 * Todos los campos son opcionales y se combinan como condiciones AND.
 * </p>
 */
@Data
public class ParametrosConsultarPolizas {
    /** Filtro por identificador del cliente propietario. */
    private Long clienteId;
    /** Filtro por identificador del tipo de póliza. */
    private Integer tipoPolizaId;
    /** Filtro por estado activo/inactivo de la póliza. */
    private Boolean activo;
    /** Fecha mínima de inicio de vigencia (inclusive). */
    private LocalDate fechaInicioDesde;
    /** Fecha máxima de inicio de vigencia (inclusive). */
    private LocalDate fechaInicioHasta;
}
