package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;
import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa la especialización de una póliza de salud.
 * <p>
 * Mapea la tabla {@code PolizasSalud} de la base de datos. Cada registro
 * está asociado a una {@link Poliza} base de tipo SALUD y contiene el
 * tipo de cobertura seleccionado para dicha póliza.
 * </p>
 */
@Entity
@Table(name = "PolizasSalud")
@Getter
@Setter
@NoArgsConstructor
public class PolizasSalud {

    /** Identificador único autogenerado de la póliza de salud. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    /** Póliza base asociada a esta especialización de salud. */
    @ManyToOne
    @JoinColumn(name = "PolizaId")
    private Poliza poliza;

    /** Tipo de cobertura de salud elegido para la póliza. */
    @ManyToOne
    @JoinColumn(name = "TipoCoberturaId", nullable = false)
    private TiposCoberturaSalud tipoCobertura;
}
