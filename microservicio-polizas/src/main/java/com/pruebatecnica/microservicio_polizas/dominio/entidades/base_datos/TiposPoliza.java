package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un tipo de póliza disponible en el sistema.
 * <p>
 * Mapea la tabla {@code TiposPoliza} de la base de datos. Actúa como catálogo
 * de los distintos tipos de póliza (Vida, Vehículo, Salud) identificados por
 * un código único.
 * </p>
 */
@Entity
@Table(name = "TiposPoliza")
@Getter
@Setter
@NoArgsConstructor
public class TiposPoliza {

    /** Identificador único autogenerado del tipo de póliza. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Código único del tipo de póliza (p. ej. {@code VIDA}, {@code SALUD}, {@code VEHICULO}). */
    @Column(nullable = false, length = 50, unique = true, columnDefinition = "nvarchar(50)")
    private String codigo;

    /** Descripción legible del tipo de póliza. */
    @Column(length = 200, columnDefinition = "nvarchar(200)")
    private String descripcion;
}
