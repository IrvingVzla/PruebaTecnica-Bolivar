package com.pruebatecnica.microservicio_polizas.api.controladores;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.ClienteDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosActualizarCliente;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosConsultarClientes;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosCrearCliente;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Cliente;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.pruebatecnica.microservicio_polizas.dominio.mapeos.IClienteMapper;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IClienteService;

/**
 * Controlador REST para la gestión de clientes.
 * <p>
 * Expone los endpoints CRUD de clientes bajo la ruta base {@code /api/clientes}.
 * Las respuestas se devuelven como {@link ClienteDTO} para evitar exponer la entidad JPA directamente.
 * </p>
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {
    /** Servicio de negocio para la gestión de clientes. */
    private final IClienteService service;
    /** Mapper para convertir entre entidades y DTOs de cliente. */
    private final IClienteMapper mapper;

    /**
     * Consulta clientes aplicando los filtros opcionales indicados como query params.
     *
     * @param parametros criterios de búsqueda
     * @return lista de clientes que cumplen los criterios
     */
    @GetMapping("/consultar-clientes")
    public List<ClienteDTO> consultarClientes(@ParameterObject @ModelAttribute ParametrosConsultarClientes parametros) {
        List<Cliente> entidades = service.consultarClientes(parametros);
        return entidades.stream().map(mapper::toDto).toList();
    }

    /**
     * Crea un nuevo cliente con los datos del cuerpo de la solicitud.
     *
     * @param parametros datos del cliente a crear
     * @return el cliente creado como DTO
     */
    @PostMapping("/crear-cliente")
    public ClienteDTO crearCliente(@RequestBody ParametrosCrearCliente parametros) {
        Cliente creado = service.crearCliente(parametros);
        return mapper.toDto(creado);
    }

    /**
     * Actualiza los datos de un cliente existente identificado por su {@code id}.
     *
     * @param id         identificador del cliente a actualizar
     * @param parametros campos a modificar
     * @return el cliente actualizado como DTO
     */
    @PutMapping("actualizar-cliente/{id}")
    public ClienteDTO actualizarCliente(@PathVariable Long id,  @RequestBody ParametrosActualizarCliente parametros) {
        Cliente actualizado = service.actualizarCliente(id, parametros);
        return mapper.toDto(actualizado);
    }

    /**
     * Elimina el cliente identificado por {@code id}.
     *
     * @param id identificador del cliente a eliminar
     */
    @DeleteMapping("eliminar-cliente/{id}")
    public void eliminarCliente(@PathVariable Long id) {
        service.eliminarCliente(id);
    }

}