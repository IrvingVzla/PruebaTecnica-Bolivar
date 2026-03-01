package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

/**
 * Entidad JPA que representa una póliza base en el sistema.
 * <p>
 * Mapea la tabla {@code Polizas} de la base de datos. Esta entidad es la raíz
 * de la jerarquía de pólizas; las especializaciones (vida, salud, vehículo)
 * referencian a esta entidad mediante relaciones uno a uno o muchos a uno.
 * </p>
 */
@Entity
@Table(name = "Polizas")
@Getter
@Setter
@NoArgsConstructor
public class Poliza {

    /** Identificador único autogenerado de la póliza. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    /** Número único de la póliza utilizado como referencia de negocio. */
    @Column(nullable = false, length = 50, unique = true, columnDefinition = "nvarchar(50)")
    private String numeroPoliza;

    /** Identificador del cliente propietario de la póliza. */
    @Column(name = "ClienteId", nullable = false)
    private Integer clienteId;

    /** Tipo de póliza al que pertenece este registro. */
    @ManyToOne
    @JoinColumn(name = "TipoPolizaId", nullable = false)
    private TiposPoliza tipoPoliza;

    /** Fecha de inicio de vigencia de la póliza. */
    @Column(nullable = false)
    private LocalDate fechaInicio;

    /** Fecha de fin de vigencia de la póliza. */
    @Column(nullable = false)
    private LocalDate fechaFin;

    /** Indica si la póliza se encuentra activa ({@code true}) o inactiva ({@code false}). */
    @Column(nullable = false)
    private Boolean estado;

    /** Fecha y hora de creación del registro. Se asigna automáticamente al persistir. */
    @Setter(AccessLevel.NONE)
    private LocalDateTime fechaCreacion;

    /**
     * Callback JPA ejecutado antes de persistir un nuevo registro.
     * <p>
     * Asigna automáticamente {@code fechaCreacion} con la fecha y hora actuales.
     * </p>
     */
    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
