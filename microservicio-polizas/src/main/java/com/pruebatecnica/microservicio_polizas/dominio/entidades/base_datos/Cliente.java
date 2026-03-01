package com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa a un cliente en el sistema de pólizas.
 * <p>
 * Mapea la tabla {@code Clientes} de la base de datos. Cada cliente está
 * identificado de forma única por la combinación de {@code tipo_documento_id}
 * y {@code numero_documento}.
 * </p>
 */
@Entity
@Table(name = "Clientes", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Clientes_Documento", columnNames = {"tipo_documento_id", "numero_documento"})
})
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    /** Identificador único autogenerado del cliente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    /** Tipo de documento de identidad asociado al cliente. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_documento_id", nullable = false)
    private TiposDocumento tipoDocumento;

    /** Número de documento de identidad del cliente. */
    @Column(name = "numero_documento", nullable = false, length = 30, columnDefinition = "nvarchar(30)")
    private String numeroDocumento;

    /** Nombres del cliente. */
    @Column(nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String nombres;

    /** Apellidos del cliente. */
    @Column(nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String apellidos;

    /** Correo electrónico del cliente. Puede ser nulo. */
    @Column(length = 150, columnDefinition = "nvarchar(150)")
    private String email;

    /** Número de teléfono del cliente. Puede ser nulo. */
    @Column(length = 30, columnDefinition = "nvarchar(30)")
    private String telefono;

    /** Fecha de nacimiento del cliente. */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /** Fecha y hora en que fue creado el registro. Se asigna automáticamente al persistir. */
    @Column(name = "fecha_creacion", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime fechaCreacion;

    /** Fecha y hora de la última actualización del registro. */
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Callback JPA ejecutado antes de persistir un nuevo registro.
     * <p>
     * Asigna automáticamente los campos {@code fechaCreacion} y {@code fechaActualizacion}
     * con la fecha y hora actuales.
     * </p>
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaCreacion = now;
        this.fechaActualizacion = now;
    }

    /**
     * Callback JPA ejecutado antes de actualizar un registro existente.
     * <p>
     * Actualiza el campo {@code fechaActualizacion} con la fecha y hora actuales.
     * </p>
     */
    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}