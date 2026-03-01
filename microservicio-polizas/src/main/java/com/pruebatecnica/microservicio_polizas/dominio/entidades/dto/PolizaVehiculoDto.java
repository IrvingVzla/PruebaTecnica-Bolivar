package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Poliza;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposCoberturaSalud;
import lombok.Data;

/**
 * DTO que representa una póliza de vehículo.
 * <p>
 * Expone la información de la especialización de vehículo asociada a
 * una póliza base. Los vehículos concretos asegurados se consultan por separado.
 * </p>
 */
@Data
public class PolizaVehiculoDto {
    /** Identificador único de la póliza de vehículo. */
    private Long id;
    /** Póliza base asociada a esta póliza de vehículo. */
    private Poliza poliza;
}
