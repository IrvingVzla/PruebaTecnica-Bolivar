package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente;

import lombok.Data;
import java.time.LocalDate;

/**
 * Parámetros de entrada para actualizar los datos de un cliente existente.
 * <p>
 * Todos los campos son opcionales; solo los campos no nulos serán aplicados
 * durante la actualización. Se requiere al menos un campo para proceder.
 * </p>
 */
@Data
public class ParametrosActualizarCliente {
    /** Nuevos nombres del cliente. */
    private String nombres;
    /** Nuevos apellidos del cliente. */
    private String apellidos;
    /** Nuevo correo electrónico del cliente. */
    private String email;
    /** Nuevo número de teléfono del cliente. */
    private String telefono;
    /** Nueva fecha de nacimiento del cliente. */
    private LocalDate fechaNacimiento;
}
