package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas;

import lombok.Data;

/**
 * Ítem de beneficiario de vida utilizado como parte de los parámetros de creación de póliza.
 * <p>
 * Representa los datos básicos de un beneficiario de vida cuando se incluye
 * dentro de una solicitud de creación de póliza completa.
 * </p>
 */
@Data
public class BeneficiarioVidaItem {
    /** Nombre completo del beneficiario. */
    private String nombreCompleto;
    /** Número de documento de identidad del beneficiario. */
    private String numeroDocumento;
    /** Tipo de parentesco del beneficiario (p. ej. {@code PADRE}, {@code CONYUGE}). */
    private String parentesco;
}
