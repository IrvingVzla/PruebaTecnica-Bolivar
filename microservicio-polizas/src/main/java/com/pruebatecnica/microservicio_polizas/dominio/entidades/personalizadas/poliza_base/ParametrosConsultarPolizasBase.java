package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base;

import lombok.Data;

import java.time.LocalDate;

/**
 * Parámetros de filtro para consultar pólizas base.
 * <p>
 * Todos los campos son opcionales; los campos informados se combinan como
 * condiciones AND sobre la tabla de pólizas base.
 * </p>
 */
@Data
public class ParametrosConsultarPolizasBase {
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
