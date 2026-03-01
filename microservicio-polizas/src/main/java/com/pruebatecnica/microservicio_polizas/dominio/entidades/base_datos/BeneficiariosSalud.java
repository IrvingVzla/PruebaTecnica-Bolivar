package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad JPA que representa a un beneficiario de una póliza de salud.
 * <p>
 * Mapea la tabla {@code BeneficiariosSalud} de la base de datos. Cada beneficiario
 * está vinculado a una {@link PolizasSalud} y cuenta con un tipo de parentesco
 * con respecto al titular de la póliza.
 * </p>
 */
@Entity
@Table(name = "BeneficiariosSalud")
@Getter
@Setter
@NoArgsConstructor
public class BeneficiariosSalud {

    /** Identificador único autogenerado del beneficiario de salud. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Póliza de salud a la que pertenece este beneficiario. */
    @ManyToOne
    @JoinColumn(name = "PolizaSaludId", nullable = false)
    private PolizasSalud polizaSalud;

    /** Tipo de parentesco del beneficiario con respecto al titular de la póliza. */
    @ManyToOne
    @JoinColumn(name = "TipoParentescoId", nullable = false)
    private TiposParentesco tipoParentesco;

    /** Nombre completo del beneficiario. */
    @Column(name = "NombreCompleto", nullable = false, length = 200, columnDefinition = "nvarchar(200)")
    private String nombreCompleto;

    /** Número de documento de identidad del beneficiario. Puede ser nulo. */
    @Column(name = "NumeroDocumento", length = 50)
    private String numeroDocumento;

    /** Monto adicional asegurado para el beneficiario. Por defecto es cero. */
    @Column(precision = 18, scale = 2)
    private BigDecimal montoAdicional = BigDecimal.ZERO;
}