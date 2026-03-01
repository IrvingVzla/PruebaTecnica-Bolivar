package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa la especialización de una póliza de vida.
 * <p>
 * Mapea la tabla {@code PolizasVida} de la base de datos. Cada registro
 * está vinculado de forma única a una {@link Poliza} base de tipo VIDA y
 * almacena el monto asegurado correspondiente.
 * </p>
 */
@Entity
@Table(name = "PolizasVida")
@Getter
@Setter
@NoArgsConstructor
public class PolizasVida {

    /** Identificador único autogenerado de la póliza de vida. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    /** Póliza base asociada a esta especialización de vida. */
    @OneToOne
    @JoinColumn(name = "PolizaId")
    private Poliza poliza;

    /** Monto total asegurado para la póliza de vida. */
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal montoAsegurado;
}
