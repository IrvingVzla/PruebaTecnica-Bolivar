package com.pruebatecnica.microservicio_polizas.dominio.servicios;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposDocumento;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.ClienteRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.TiposDocumentoRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Cliente;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IClienteService;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosConsultarClientes;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosCrearCliente;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosActualizarCliente;

import jakarta.validation.ValidationException;
import java.time.LocalDate;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * Implementación del servicio de gestión de clientes.
 * <p>
 * Provee las operaciones CRUD sobre la entidad {@link Cliente}, incluyendo
 * validaciones de negocio como unicidad de documento, formato de email y
 * coherencia de fechas de nacimiento.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ClienteService implements IClienteService {
    /** Repositorio de acceso a datos para clientes. */
    private final ClienteRepository clienteRepository;
    /** Repositorio de acceso a datos para tipos de documento. */
    private final TiposDocumentoRepository tiposDocumentoRepository;

    /** Expresión regular básica para validar el formato de correo electrónico. */
    private static final Pattern REGEX_EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    /**
     * {@inheritDoc}
     * <p>
     * Construye dinámicamente la especificación JPA según los criterios provistos.
     * Las búsquedas por nombres y apellidos son insensibles a mayúsculas y usan LIKE.
     * </p>
     */
    @Override
    public List<Cliente> consultarClientes(ParametrosConsultarClientes parametros) {

        Specification<Cliente> spec = Specification.allOf();

        if (parametros != null) {

            if (parametros.getId() != null) {
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("id"), parametros.getId()));
            }

            if (parametros.getTipoDocumentoId() != null) {
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("tipoDocumento").get("id"),
                                parametros.getTipoDocumentoId()));
            }

            if (parametros.getNumeroDocumento() != null &&
                    !parametros.getNumeroDocumento().isBlank()) {

                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("numeroDocumento"),
                                parametros.getNumeroDocumento()));
            }

            if (parametros.getNombres() != null &&
                    !parametros.getNombres().isBlank()) {

                spec = spec.and((root, query, cb) ->
                        cb.like(
                                cb.lower(root.get("nombres")),
                                "%" + parametros.getNombres().toLowerCase() + "%"
                        ));
            }

            if (parametros.getApellidos() != null &&
                    !parametros.getApellidos().isBlank()) {

                spec = spec.and((root, query, cb) ->
                        cb.like(
                                cb.lower(root.get("apellidos")),
                                "%" + parametros.getApellidos().toLowerCase() + "%"
                        ));
            }

            if (parametros.getEmail() != null &&
                    !parametros.getEmail().isBlank()) {

                spec = spec.and((root, query, cb) ->
                        cb.equal(
                                cb.lower(root.get("email")),
                                parametros.getEmail().toLowerCase()
                        ));
            }

            if (parametros.getTelefono() != null &&
                    !parametros.getTelefono().isBlank()) {

                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("telefono"),
                                parametros.getTelefono()));
            }
        }

        return clienteRepository.findAll(spec);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Valida los parámetros, verifica que el tipo de documento exista y que no haya
     * duplicidad de documento antes de persistir el nuevo cliente.
     * </p>
     */
    @Override
    public Cliente crearCliente(ParametrosCrearCliente parametros) {
        // validar parámetros y lanzar ValidationException con listado de errores si aplica
        validarParametrosCrearCliente(parametros);

        Integer tipoDocumentoId = Math.toIntExact(parametros.getTipoDocumentoId());

        TiposDocumento tipoDocumento = tiposDocumentoRepository
                .findById(Math.toIntExact(parametros.getTipoDocumentoId()))
                .orElseThrow(() -> new RuntimeException(
                        "Tipo de documento no encontrado con id: " + parametros.getTipoDocumentoId())
                );

        boolean yaExiste = clienteRepository
                .existsByTipoDocumento_IdAndNumeroDocumento(
                        tipoDocumentoId,
                        parametros.getNumeroDocumento()
                );

        if (yaExiste) {
            throw new ValidationException(
                "Ya existe un cliente con el tipo y número de documento ingresado"
            );
        }

        Cliente cliente = new Cliente();
        cliente.setTipoDocumento(tipoDocumento);
        cliente.setNumeroDocumento(parametros.getNumeroDocumento());
        cliente.setNombres(parametros.getNombres());
        cliente.setApellidos(parametros.getApellidos());
        cliente.setEmail(parametros.getEmail());
        cliente.setTelefono(parametros.getTelefono());
        cliente.setFechaNacimiento(parametros.getFechaNacimiento());

        return crearEntidad(cliente);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cliente actualizarCliente(Long id, ParametrosActualizarCliente parametros) {
        // validar id y parámetros
        validarParametrosActualizarCliente(id, parametros);

        Cliente cliente = new Cliente();
        cliente.setNombres(parametros.getNombres());
        cliente.setApellidos(parametros.getApellidos());
        cliente.setEmail(parametros.getEmail());
        cliente.setTelefono(parametros.getTelefono());
        cliente.setFechaNacimiento(parametros.getFechaNacimiento());

        return actualizarEntidad(id, cliente);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminarCliente(Long id) {
        validarParametrosEliminarCliente(id);
        clienteRepository.deleteById(id);
    }

    // helpers privados

    /**
     * Persiste un cliente nuevo previa validación de unicidad de documento.
     *
     * @param cliente entidad a guardar
     * @return el cliente persistido
     */
    private Cliente crearEntidad(Cliente cliente) {
        if (cliente.getTipoDocumento() == null || cliente.getTipoDocumento().getId() == null) {
            throw new RuntimeException("Tipo de documento es requerido");
        }
        clienteRepository.findByTipoDocumentoIdAndNumeroDocumento(cliente.getTipoDocumento().getId(), cliente.getNumeroDocumento())
                .ifPresent(c -> { throw new RuntimeException("Ya existe un cliente con ese tipo y número de documento"); });
        return clienteRepository.save(cliente);
    }

    /**
     * Actualiza los campos modificables de un cliente existente.
     *
     * @param id      identificador del cliente a actualizar
     * @param cliente entidad con los nuevos valores
     * @return el cliente actualizado
     * @throws RuntimeException si no existe un cliente con el id indicado
     */
    private Cliente actualizarEntidad(Long id, Cliente cliente) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        existente.setNombres(cliente.getNombres());
        existente.setApellidos(cliente.getApellidos());
        existente.setEmail(cliente.getEmail());
        existente.setTelefono(cliente.getTelefono());
        existente.setFechaNacimiento(cliente.getFechaNacimiento());
        return clienteRepository.save(existente);
    }

    // ---------------------- Validaciones ----------------------

    /**
     * Valida los parámetros de creación de un cliente.
     *
     * @param parametros parámetros a validar
     * @throws jakarta.validation.ValidationException con todos los errores encontrados si la validación falla
     */
    private void validarParametrosCrearCliente(ParametrosCrearCliente parametros) {
        if (parametros == null) {
            throw new ValidationException("Parámetros de creación de cliente son requeridos");
        }
        StringJoiner errores = new StringJoiner("; ");

        if (parametros.getTipoDocumentoId() == null) {
            errores.add("tipoDocumentoId es requerido");
        } else if (parametros.getTipoDocumentoId() <= 0) {
            errores.add("tipoDocumentoId debe ser un entero positivo");
        }

        if (parametros.getNumeroDocumento() == null || parametros.getNumeroDocumento().isBlank()) {
            errores.add("numeroDocumento es requerido");
        }

        if (parametros.getNombres() == null || parametros.getNombres().isBlank()) {
            errores.add("nombres es requerido");
        }

        if (parametros.getApellidos() == null || parametros.getApellidos().isBlank()) {
            errores.add("apellidos es requerido");
        }

        if (parametros.getEmail() == null || parametros.getEmail().isBlank()) {
            errores.add("email es requerido");
        } else if (!REGEX_EMAIL.matcher(parametros.getEmail()).matches()) {
            errores.add("email no tiene un formato válido");
        }

        if (parametros.getTelefono() != null && parametros.getTelefono().isBlank()) {
            errores.add("telefono, si se provee, no puede estar en blanco");
        }

        // Validar fecha de nacimiento si se provee
        try {
            LocalDate fecha = parametros.getFechaNacimiento();
            if (fecha != null && fecha.isAfter(LocalDate.now())) {
                errores.add("fechaNacimiento no puede ser una fecha futura");
            }
        } catch (Exception ex) {
            // Si el tipo no es LocalDate u otro problema, omitir la validación de fecha
        }

        if (errores.length() > 0) {
            throw new ValidationException(errores.toString());
        }
    }

    /**
     * Valida el identificador y los parámetros de actualización de un cliente.
     *
     * @param id         identificador del cliente
     * @param parametros campos de actualización
     * @throws jakarta.validation.ValidationException si el id no es válido o no se provee ningún campo
     */
    private void validarParametrosActualizarCliente(Long id, ParametrosActualizarCliente parametros) {
        StringJoiner errores = new StringJoiner("; ");
        if (id == null || id <= 0) {
            errores.add("id es requerido y debe ser mayor a 0");
        }
        if (parametros == null) {
            errores.add("Parámetros de actualización son requeridos");
            throw new ValidationException(errores.toString());
        }

        boolean anyFieldPresent = false;
        if (parametros.getNombres() != null) {
            anyFieldPresent = true;
            if (parametros.getNombres().isBlank()) errores.add("nombres, si se provee, no puede estar en blanco");
        }
        if (parametros.getApellidos() != null) {
            anyFieldPresent = true;
            if (parametros.getApellidos().isBlank()) errores.add("apellidos, si se provee, no puede estar en blanco");
        }
        if (parametros.getEmail() != null) {
            anyFieldPresent = true;
            if (parametros.getEmail().isBlank()) errores.add("email, si se provee, no puede estar en blanco");
            else if (!REGEX_EMAIL.matcher(parametros.getEmail()).matches()) errores.add("email no tiene un formato válido");
        }
        if (parametros.getTelefono() != null) {
            anyFieldPresent = true;
            if (parametros.getTelefono().isBlank()) errores.add("telefono, si se provee, no puede estar en blanco");
        }
        try {
            LocalDate fecha = parametros.getFechaNacimiento();
            if (fecha != null) {
                anyFieldPresent = true;
                if (fecha.isAfter(LocalDate.now())) errores.add("fechaNacimiento no puede ser una fecha futura");
            }
        } catch (Exception ex) {
            // ignore
        }

        if (!anyFieldPresent) errores.add("Al menos un campo para actualizar debe ser provisto");

        if (errores.length() > 0) {
            throw new ValidationException(errores.toString());
        }
    }

    /**
     * Valida que el identificador para eliminar un cliente sea positivo y no nulo.
     *
     * @param id identificador a validar
     * @throws jakarta.validation.ValidationException si el id es nulo o menor a 1
     */
    private void validarParametrosEliminarCliente(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("id es requerido y debe ser mayor a 0");
        }
    }

}

