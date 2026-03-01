package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo;

import lombok.Data;

/**
 * Parámetros de filtro para consultar pólizas de vehículo.
 * <p>
 * Todos los campos son opcionales y se combinan como condiciones AND.
 * </p>
 */
@Data
public class ParametrosConsultarPolizaVehiculo {
    /** Filtro por identificador de la póliza de vehículo. */
    private Long id;
    /** Filtro por identificador de la póliza base asociada. */
    private Long polizaId;
}
