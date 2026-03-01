package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un tipo de cobertura para las pólizas de salud.
 * <p>
 * Mapea la tabla {@code TiposCoberturaSalud} de la base de datos. Actúa como
 * catálogo de las coberturas disponibles (p. ej. solo cliente, cliente y padres,
 * cliente con cónyuge e hijos).
 * </p>
 */
@Entity
@Table(name = "TiposCoberturaSalud")
@Getter
@Setter
@NoArgsConstructor
public class TiposCoberturaSalud {

    /** Identificador numérico del tipo de cobertura de salud. */
    @Id
    private Integer id;

    /** Código único de la cobertura de salud (p. ej. {@code SOLO_CLIENTE}). */
    @Column(nullable = false, length = 30, unique = true, columnDefinition = "nvarchar(30)")
    private String codigo;

    /** Descripción legible del tipo de cobertura de salud. */
    @Column(nullable = false, length = 150, columnDefinition = "nvarchar(150)")
    private String descripcion;
}
