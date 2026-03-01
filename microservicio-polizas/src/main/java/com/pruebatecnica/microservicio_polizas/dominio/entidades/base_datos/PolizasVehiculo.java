package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;
import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa la especialización de una póliza de vehículo.
 * <p>
 * Mapea la tabla {@code PolizasVehiculo} de la base de datos. Cada registro
 * está vinculado a una {@link Poliza} base de tipo VEHICULO y puede tener
 * uno o más vehículos asegurados asociados.
 * </p>
 */
@Entity
@Table(name = "PolizasVehiculo")
@Getter
@Setter
@NoArgsConstructor
public class PolizasVehiculo {

    /** Identificador único autogenerado de la póliza de vehículo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    /** Póliza base asociada a esta especialización de vehículo. */
    @ManyToOne
    @JoinColumn(name = "PolizaId", nullable = false)
    private Poliza poliza;
}
