package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Parámetros de entrada para crear una especialización de póliza de vehículo.
 * <p>
 * Se aplica sobre una póliza base de tipo VEHICULO ya existente y activa.
 * </p>
 */
@Data
public class ParametrosCrearPolizaVehiculo {
    /** Identificador de la póliza base de tipo VEHICULO. */
    private Long polizaId;
}
