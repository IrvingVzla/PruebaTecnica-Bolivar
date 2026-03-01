package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un tipo de parentesco entre beneficiarios y titulares.
 * <p>
 * Mapea la tabla {@code TiposParentesco} de la base de datos. Actúa como catálogo
 * de los parentescos reconocidos (p. ej. padre, madre, cónyuge, hijo).
 * </p>
 */
@Entity
@Table(name = "TiposParentesco")
@Getter
@Setter
@NoArgsConstructor
public class TiposParentesco {

    /** Identificador numérico del tipo de parentesco. */
    @Id
    private Integer id;

    /** Código único del tipo de parentesco (p. ej. {@code PADRE}, {@code HIJO}). */
    @Column(nullable = false, length = 20, unique = true, columnDefinition = "nvarchar(20)")
    private String codigo;

    /** Descripción legible del tipo de parentesco. */
    @Column(nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String descripcion;
}
