package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Parámetros de entrada para registrar un beneficiario en una póliza de vida.
 * <p>
 * Una póliza de vida admite un máximo de dos beneficiarios. El número de documento
 * debe ser único dentro de la misma póliza de vida.
 * </p>
 */
@Data
public class ParametrosCrearBeneficiarioVida {
    /** Identificador de la póliza de vida a la que se agrega el beneficiario. */
    private Long polizaVidaId;
    /** Identificador del tipo de parentesco del beneficiario con el titular. */
    private Long tipoParentescoId;
    /** Nombre completo del beneficiario. */
    private String nombreCompleto;
    /** Número de documento de identidad del beneficiario. Opcional pero no duplicable. */
    private String numeroDocumento;
}
