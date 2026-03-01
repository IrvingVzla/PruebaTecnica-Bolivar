package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Parámetros de entrada para crear una especialización de póliza de vida.
 * <p>
 * Se aplica sobre una póliza base de tipo VIDA ya existente y activa.
 * El monto asegurado debe ser mayor a cero.
 * </p>
 */
@Data
public class ParametrosCrearPolizaVida {
    /** Identificador de la póliza base de tipo VIDA. */
    private Long polizaId;
    /** Monto total asegurado para la póliza de vida. Debe ser mayor a cero. */
    private BigDecimal montoAsegurado;
}
