package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasVehiculo;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Parámetros de entrada para registrar un vehículo en una póliza de vehículo.
 * <p>
 * La placa del vehículo se almacena en mayúsculas y debe ser única dentro
 * de la misma póliza de vehículo.
 * </p>
 */
@Data
public class ParametrosCrearVehiculo {
    /** Identificador de la póliza de vehículo a la que se asociará el vehículo. */
    private Long polizaVehiculoId;
    /** Número de placa del vehículo. Se convertirá a mayúsculas automáticamente. */
    private String placa;
    /** Marca del vehículo. */
    private String marca;
    /** Modelo del vehículo. */
    private String modelo;
    /** Año de fabricación del vehículo. */
    private Integer anio;
    /** Valor monetario asegurado del vehículo. */
    private BigDecimal valorAsegurado;
}
