package com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio;

import java.util.List;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosConsultarClientes;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosCrearCliente;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosActualizarCliente;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Cliente;

/**
 * Contrato de servicio para la gestión de clientes.
 * <p>
 * Define las operaciones CRUD disponibles sobre la entidad {@link Cliente}.
 * </p>
 */
public interface IClienteService {

    /**
     * Consulta clientes aplicando los filtros indicados.
     *
     * @param parametros criterios de filtrado; puede ser {@code null} para obtener todos
     * @return lista de clientes que satisfacen los criterios
     */
    List<Cliente> consultarClientes(ParametrosConsultarClientes parametros);

    /**
     * Crea un nuevo cliente con los datos suministrados.
     *
     * @param parametros datos del cliente a crear
     * @return el cliente creado y persistido
     * @throws jakarta.validation.ValidationException si los parámetros no son válidos o ya existe un cliente con el mismo documento
     */
    Cliente crearCliente(ParametrosCrearCliente parametros);

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param id         identificador del cliente a actualizar
     * @param parametros campos a modificar; solo los no nulos serán aplicados
     * @return el cliente actualizado
     * @throws jakarta.validation.ValidationException si el id o los parámetros no son válidos
     */
    Cliente actualizarCliente(Long id, ParametrosActualizarCliente parametros);

    /**
     * Elimina un cliente del sistema por su identificador.
     *
     * @param id identificador del cliente a eliminar
     * @throws jakarta.validation.ValidationException si el id no es válido
     */
    void eliminarCliente(Long id);
}