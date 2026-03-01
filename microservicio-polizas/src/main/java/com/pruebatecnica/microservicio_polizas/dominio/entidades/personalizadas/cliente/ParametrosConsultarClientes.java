package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente;

import lombok.Data;

/**
 * Parámetros de filtro para consultar clientes.
 * <p>
 * Todos los campos son opcionales; los campos informados se combinan como
 * condiciones AND para filtrar los resultados de la consulta.
 * </p>
 */
@Data
public class ParametrosConsultarClientes {
    /** Filtro por identificador único del cliente. */
    private Long id;
    /** Filtro por identificador del tipo de documento. */
    private Long tipoDocumentoId;
    /** Filtro por número exacto de documento. */
    private String numeroDocumento;
    /** Filtro por nombres del cliente (búsqueda parcial). */
    private String nombres;
    /** Filtro por apellidos del cliente (búsqueda parcial). */
    private String apellidos;
    /** Filtro por correo electrónico del cliente. */
    private String email;
    /** Filtro por número de teléfono del cliente. */
    private String telefono;
}
