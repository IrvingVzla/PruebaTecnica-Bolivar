package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposParentesco;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO que representa a un beneficiario de una póliza de salud.
 * <p>
 * Se utiliza para exponer los datos de un beneficiario de salud en las
 * respuestas de la API, sin exponer directamente la entidad JPA.
 * </p>
 */
@Data
public class BeneficiarioSaludDTO {
    /** Identificador único del beneficiario de salud. */
    private Long id;
    /** Tipo de parentesco del beneficiario con el titular de la póliza. */
    private TiposParentesco tipoParentesco;
    /** Nombre completo del beneficiario. */
    private String nombreCompleto;
    /** Monto adicional asegurado para el beneficiario. Por defecto es cero. */
    private BigDecimal montoAdicional = BigDecimal.ZERO;
}
