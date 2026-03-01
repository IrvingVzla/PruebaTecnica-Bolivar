package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposParentesco;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Parámetros de entrada para crear un beneficiario en una póliza de salud.
 * <p>
 * La elegibilidad del beneficiario es validada según el tipo de cobertura
 * de la póliza de salud (solo cliente, cliente y padres, cliente cónyuge e hijos).
 * </p>
 */
@Data
public class ParametrosCrearBeneficiarioSalud {
    /** Identificador de la póliza de salud a la que se agrega el beneficiario. */
    private Long polizaSaludId;
    /** Identificador del tipo de parentesco del beneficiario con el titular. */
    private Long tipoParentescoId;
    /** Nombre completo del beneficiario. */
    private String nombreCompleto;
    /** Número de documento de identidad del beneficiario. Opcional. */
    private String numeroDocumento;
    /** Monto adicional asegurado para el beneficiario. Por defecto es cero. */
    private BigDecimal montoAdicional = BigDecimal.ZERO;
}
