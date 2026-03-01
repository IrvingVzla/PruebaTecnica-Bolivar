package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasVehiculo;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO que representa un vehículo asegurado dentro de una póliza de vehículo.
 * <p>
 * Expone los datos del vehículo en las respuestas de la API, incluyendo
 * la referencia a la póliza de vehículo a la que pertenece.
 * </p>
 */
@Data
public class VehiculoDTO {
    /** Identificador único del vehículo. */
    private Long id;
    /** Póliza de vehículo a la que pertenece este vehículo. */
    private PolizasVehiculo polizasVehiculo;
    /** Número de placa del vehículo. */
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
