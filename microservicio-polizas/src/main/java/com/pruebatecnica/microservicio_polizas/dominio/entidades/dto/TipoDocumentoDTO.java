package com.pruebatecnica.microservicio_polizas.dominio.entidades.dto;

import lombok.Data;

/**
 * Objeto de transferencia de datos (DTO) que representa un tipo de documento de identidad.
 * <p>
 * Se utiliza como parte de la respuesta de la API para describir el tipo de documento
 * asociado a un cliente.
 * </p>
 */
@Data
public class TipoDocumentoDTO {
    /** Identificador numérico del tipo de documento. */
    private Integer id;
    /** Código corto del tipo de documento (p. ej. {@code CC}, {@code PAS}). */
    private String codigo;
    /** Descripción completa del tipo de documento. */
    private String descripcion;
    /** Indica si el tipo de documento está habilitado. */
    private Boolean activo;
}
