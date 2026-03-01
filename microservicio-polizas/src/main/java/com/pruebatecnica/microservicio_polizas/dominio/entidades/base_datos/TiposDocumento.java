package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un tipo de documento de identidad.
 * <p>
 * Mapea la tabla {@code TiposDocumento} de la base de datos. Actúa como catálogo
 * de los documentos de identidad aceptados en el sistema (p. ej. cédula, pasaporte).
 * </p>
 */
@Entity
@Table(name = "TiposDocumento")
@Getter
@Setter
@NoArgsConstructor
public class TiposDocumento {

    /** Identificador numérico del tipo de documento. */
    @Id
    private Integer id;

    /** Código único del tipo de documento (p. ej. {@code CC}, {@code PAS}). */
    @Column(nullable = false, length = 10, unique = true, columnDefinition = "nvarchar(10)")
    private String codigo;

    /** Descripción completa del tipo de documento. */
    @Column(nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String descripcion;

    /** Indica si el tipo de documento está habilitado para su uso ({@code true}) o no. */
    @Column(nullable = false)
    private Boolean activo = true;
}
