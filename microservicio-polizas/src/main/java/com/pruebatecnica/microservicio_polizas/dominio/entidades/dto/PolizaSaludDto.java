package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Poliza;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposCoberturaSalud;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO que representa una póliza de salud.
 * <p>
 * Expone la información de la especialización de salud asociada a
 * una póliza base, incluyendo la referencia a la póliza y el tipo de cobertura.
 * </p>
 */
@Data
public class PolizaSaludDto {
    /** Identificador único de la póliza de salud. */
    private Long id;
    /** Póliza base asociada a esta póliza de salud. */
    private Poliza poliza;
    /** Tipo de cobertura de salud seleccionado. */
    private TiposCoberturaSalud tipoCobertura;
}
