package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida;

import lombok.Data;

/**
 * Parámetros de filtro para consultar pólizas de vida.
 * <p>
 * Todos los campos son opcionales y se combinan como condiciones AND.
 * </p>
 */
@Data
public class ParametrosConsultarPolizaVida {
    /** Filtro por identificador de la póliza de vida. */
    private Long id;
    /** Filtro por identificador de la póliza base asociada. */
    private Long polizaId;
}
