package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente;

import lombok.Data;
import java.time.LocalDate;

/**
 * Parámetros de entrada para la creación de un nuevo cliente.
 * <p>
 * Todos los campos marcados con su descripción son obligatorios salvo
 * {@code email} y {@code telefono} que pueden ser nulos.
 * </p>
 */
@Data
public class ParametrosCrearCliente {
    /** Identificador del tipo de documento del cliente. */
    private Long tipoDocumentoId;
    /** Número de documento de identidad del cliente. */
    private String numeroDocumento;
    /** Nombres del cliente. */
    private String nombres;
    /** Apellidos del cliente. */
    private String apellidos;
    /** Correo electrónico del cliente. Opcional. */
    private String email;
    /** Número de teléfono del cliente. Opcional. */
    private String telefono;
    /** Fecha de nacimiento del cliente. No puede ser fecha futura. */
    private LocalDate fechaNacimiento;
}
