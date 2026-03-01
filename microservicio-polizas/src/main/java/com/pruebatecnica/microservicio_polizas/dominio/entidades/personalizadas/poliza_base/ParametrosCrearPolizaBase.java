package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.BeneficiarioVidaItem;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.PolizaVehiculoItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Parámetros de entrada para crear una póliza base.
 * <p>
 * Contiene los datos mínimos necesarios para registrar una póliza en el sistema:
 * cliente, tipo de póliza y rango de fechas de vigencia.
 * </p>
 */
@Data
public class ParametrosCrearPolizaBase {
    /** Identificador del cliente al que se le asignará la póliza. */
    private Long clienteId;
    /** Identificador del tipo de póliza (Vida, Salud, Vehículo). */
    private Integer tipoPolizaId;
    /** Fecha de inicio de vigencia de la póliza. */
    private LocalDate fechaInicio;
    /** Fecha de fin de vigencia de la póliza. */
    private LocalDate fechaFin;
}
