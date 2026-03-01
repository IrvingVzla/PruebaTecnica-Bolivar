package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposParentesco;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa a un beneficiario de una póliza de vida.
 * <p>
 * Se utiliza para exponer los datos de un beneficiario de vida en las
 * respuestas de la API. Una póliza de vida puede tener máximo dos beneficiarios.
 * </p>
 */
@Data
public class BeneficiarioVidaDTO {
    /** Identificador único del beneficiario de vida. */
    private Long id;
    /** Tipo de parentesco del beneficiario con el titular de la póliza. */
    private TiposParentesco tipoParentesco;
    /** Nombre completo del beneficiario. */
    private String nombreCompleto;
}
