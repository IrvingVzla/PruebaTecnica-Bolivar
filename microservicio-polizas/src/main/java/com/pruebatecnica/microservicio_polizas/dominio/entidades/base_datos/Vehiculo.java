package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un vehículo asegurado dentro de una póliza de vehículo.
 * <p>
 * Mapea la tabla {@code Vehiculos} de la base de datos. Cada vehículo está
 * vinculado a una {@link PolizasVehiculo} y contiene los datos del automóvil
 * junto con su valor asegurado.
 * </p>
 */
@Entity
@Table(name = "Vehiculos")
@Getter
@Setter
@NoArgsConstructor
public class Vehiculo {

    /** Identificador único autogenerado del vehículo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Póliza de vehículo a la que pertenece este vehículo. */
    @ManyToOne
    @JoinColumn(name = "PolizaVehiculoId", nullable = false)
    private PolizasVehiculo polizasVehiculo;

    /** Número de placa del vehículo. Se almacena en mayúsculas. */
    @Column(nullable = false, length = 20, columnDefinition = "nvarchar(20)")
    private String placa;

    /** Marca del vehículo (p. ej. Toyota, Chevrolet). */
    @Column(length = 100, columnDefinition = "nvarchar(100)")
    private String marca;

    /** Modelo del vehículo (p. ej. Corolla, Spark). */
    @Column(length = 100, columnDefinition = "nvarchar(100)")
    private String modelo;

    /** Año de fabricación del vehículo. */
    private Integer anio;

    /** Valor monetario asegurado del vehículo. */
    @Column(precision = 18, scale = 2)
    private BigDecimal valorAsegurado;
}
