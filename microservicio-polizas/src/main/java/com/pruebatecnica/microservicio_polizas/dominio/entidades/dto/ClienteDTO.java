package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Objeto de transferencia de datos (DTO) que representa a un cliente.
 * <p>
 * Se utiliza para exponer la información del cliente en las respuestas de la API,
 * evitando exponer directamente la entidad JPA {@code Cliente}.
 * </p>
 */
@Data
public class ClienteDTO {
    /** Identificador único del cliente. */
    private Long id;
    /** Tipo de documento de identidad del cliente. */
    private TipoDocumentoDTO tipoDocumento;
    /** Número de documento de identidad del cliente. */
    private String numeroDocumento;
    /** Nombres del cliente. */
    private String nombres;
    /** Apellidos del cliente. */
    private String apellidos;
    /** Correo electrónico del cliente. */
    private String email;
    /** Número de teléfono del cliente. */
    private String telefono;
    /** Fecha de nacimiento del cliente. */
    private LocalDate fechaNacimiento;
}
