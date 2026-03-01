package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasVida;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO completo de una póliza que incluye sus especializaciones.
 * <p>
 * Utilizado en la consulta de pólizas de un cliente, agrupando en un
 * solo objeto la información base y los detalles específicos según el
 * tipo de póliza (vida, salud o vehículo).
 * </p>
 */
@Data
public class PolizaCompletoDTO {
    /** Identificador único de la póliza. */
    private Long id;
    /** Número de referencia de negocio de la póliza. */
    private String numeroPoliza;
    /** Identificador del tipo de póliza. */
    private Integer tipoPolizaId;
    /** Código del tipo de póliza. */
    private String tipoPolizaCodigo;
    /** Fecha de inicio de vigencia. */
    private LocalDate fechaInicio;
    /** Fecha de fin de vigencia. */
    private LocalDate fechaFin;
    /** Monto base de la póliza. */
    private BigDecimal montoBase;
    /** Indica si la póliza está activa. */
    private Boolean estado;

    /** Lista de especializaciones de salud asociadas a la póliza (aplica para tipo SALUD). */
    private List<PolizasSalud> polizasSalud;
    /** Especialización de vida asociada a la póliza (aplica para tipo VIDA). */
    private PolizasVida polizaVida;
    /** Lista de especializaciones de vehículo asociadas a la póliza (aplica para tipo VEHICULO). */
    private List<PolizasVehiculo> polizasVehiculo;
}
