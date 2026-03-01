package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Parámetros de entrada para crear una especialización de póliza de salud.
 * <p>
 * Se aplica sobre una póliza base de tipo SALUD ya existente.
 * </p>
 */
@Data
public class ParametrosCrearPolizaSalud {
    /** Identificador de la póliza base de tipo SALUD. */
    private Long polizaId;
    /** Identificador del tipo de cobertura de salud a asociar. */
    private Integer tipoCoberturaId;
}
