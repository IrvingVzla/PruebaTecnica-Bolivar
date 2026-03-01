package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Poliza;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa una póliza de vida.
 * <p>
 * Expone la información de la especialización de vida asociada a
 * una póliza base, incluyendo la referencia a la póliza y el monto asegurado.
 * </p>
 */
@Data
public class PolizaVidaDTO {
    /** Identificador único de la póliza de vida. */
    private Long id;
    /** Póliza base asociada a esta póliza de vida. */
    private Poliza poliza;
    /** Monto total asegurado de la póliza de vida. */
    private BigDecimal montoAsegurado;
}
