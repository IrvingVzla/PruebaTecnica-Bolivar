package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Ítem de vehículo utilizado como parte de los parámetros de creación de póliza.
 * <p>
 * Representa la referencia a un vehículo junto con su valor asegurado cuando
 * se incluye dentro de una solicitud de creación de póliza de vehículo completa.
 * </p>
 */
@Data
public class PolizaVehiculoItem {
    /** Identificador del vehículo a asegurar. */
    private Long vehiculoId;
    /** Valor monetario asegurado del vehículo. */
    private BigDecimal montoAsegurado;
}
