package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente;

import lombok.Data;

/**
 * Parámetros de entrada para eliminar un cliente.
 * <p>
 * Contiene el identificador del cliente que se desea eliminar del sistema.
 * </p>
 */
@Data
public class ParametrosEliminarCliente {
    /** Identificador único del cliente a eliminar. */
    private Long id;
}
