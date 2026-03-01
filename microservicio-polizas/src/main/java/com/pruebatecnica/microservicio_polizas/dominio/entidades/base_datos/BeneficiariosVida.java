package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa a un beneficiario de una póliza de vida.
 * <p>
 * Mapea la tabla {@code BeneficiariosVida} de la base de datos. Cada beneficiario
 * está vinculado a una {@link PolizasVida} y posee un tipo de parentesco con
 * respecto al titular. Una póliza de vida admite un máximo de dos beneficiarios.
 * </p>
 */
@Entity
@Table(name = "BeneficiariosVida")
@Getter
@Setter
@NoArgsConstructor
public class BeneficiariosVida {

    /** Identificador único autogenerado del beneficiario de vida. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Póliza de vida a la que pertenece este beneficiario. */
    @ManyToOne
    @JoinColumn(name = "PolizaVidaId", nullable = false)
    private PolizasVida polizasVida;

    /** Nombre completo del beneficiario. */
    @Column(name = "NombreCompleto", nullable = false, length = 200, columnDefinition = "nvarchar(200)")
    private String nombreCompleto;

    /** Número de documento de identidad del beneficiario. Puede ser nulo. */
    @Column(name = "NumeroDocumento", length = 50)
    private String numeroDocumento;

    /** Tipo de parentesco del beneficiario con respecto al titular de la póliza. */
    @ManyToOne
    @JoinColumn(name = "TipoParentescoId", nullable = false)
    private TiposParentesco tipoParentesco;
}
