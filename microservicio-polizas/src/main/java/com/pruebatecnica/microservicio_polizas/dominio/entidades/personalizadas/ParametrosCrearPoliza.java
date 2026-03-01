package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Parámetros generales de entrada para crear una póliza completa.
 * <p>
 * Clase de uso general que agrupa todos los campos opcionales y obligatorios
 * para la creación unificada de una póliza base con sus detalles específicos
 * (vida, salud o vehículo) en una sola solicitud.
 * </p>
 */
@Data
public class ParametrosCrearPoliza {
    /** Identificador del cliente al que se le asignará la póliza. */
    private Long clienteId;
    /** Identificador del tipo de póliza (Vida, Salud, Vehículo). */
    private Integer tipoPolizaId;
    /** Fecha de inicio de vigencia de la póliza. */
    private LocalDate fechaInicio;
    /** Fecha de fin de vigencia de la póliza. */
    private LocalDate fechaFin;
    /** Monto base de la póliza. */
    private BigDecimal montoBase;

    /** Monto asegurado (aplica para pólizas de vida). */
    private BigDecimal montoAsegurado;
    /** Identificador del tipo de cobertura (aplica para pólizas de salud). */
    private Integer tipoCoberturaId;

    /** Lista de vehículos a asegurar (aplica para pólizas de vehículo). */
    private List<PolizaVehiculoItem> vehiculos;
    /** Lista de beneficiarios de vida (aplica para pólizas de vida). */
    private List<BeneficiarioVidaItem> beneficiariosVida;
}
