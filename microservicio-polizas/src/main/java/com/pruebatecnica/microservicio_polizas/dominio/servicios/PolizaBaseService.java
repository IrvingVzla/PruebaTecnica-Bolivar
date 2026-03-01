package com.pruebatecnica.microservicio_polizas.dominio.servicios;

import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.ClienteRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.PolizaRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.PolizasSaludRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.PolizasVehiculoRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.PolizasVidaRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.TiposPolizaRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Poliza;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposPoliza;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaCompletoDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base.ParametrosConsultarPolizasBase;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base.ParametrosCrearPolizaBase;
import com.pruebatecnica.microservicio_polizas.dominio.enums.Enums;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaBaseService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Implementación del servicio de gestión de pólizas base.
 * <p>
 * Proporciona las operaciones de consulta y creación de pólizas base,
 * así como la consulta enriquecida que incluye las especializaciones
 * (vida, salud, vehículo) de cada póliza.
 * Utiliza {@code @Lazy} sobre sí mismo para poder invocar métodos transaccionales
 * a través del proxy de Spring.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PolizaBaseService implements IPolizaBaseService {
    private final PolizaRepository polizaRepository;
    private final ClienteRepository clienteRepository;
    private final TiposPolizaRepository tiposPolizaRepository;
    private final PolizasSaludRepository polizasSaludRepository;
    private final PolizasVidaRepository polizasVidaRepository;
    private final PolizasVehiculoRepository polizasVehiculoRepository;

    private IPolizaBaseService self;

    /**
     * Inyección lazy de sí mismo para garantizar el paso por el proxy transaccional
     * al llamar a {@link #consultarPolizasBase(ParametrosConsultarPolizasBase)} desde
     * {@link #consultarPolizasCliente(ParametrosConsultarPolizasBase)}.
     *
     * @param self referencia al proxy Spring de este mismo servicio
     */
    @Lazy
    @Autowired
    public void setSelf(IPolizaBaseService self) {
        this.self = self;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Construye dinámicamente la especificación JPA según los filtros recibidos.
     * </p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<Poliza> consultarPolizasBase(ParametrosConsultarPolizasBase parametros) {

        Specification<Poliza> spec = Specification.allOf();

        if (parametros != null) {

            if (parametros.getClienteId() != null) {
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("clienteId"), parametros.getClienteId()));
            }

            if (parametros.getTipoPolizaId() != null) {
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("tipoPoliza").get("id"),
                                parametros.getTipoPolizaId()));
            }

            if (parametros.getActivo() != null) {
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("estado"), parametros.getActivo()));
            }

            if (parametros.getFechaInicioDesde() != null) {
                spec = spec.and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(
                                root.get("fechaInicio"),
                                parametros.getFechaInicioDesde()));
            }

            if (parametros.getFechaInicioHasta() != null) {
                spec = spec.and((root, query, cb) ->
                        cb.lessThanOrEqualTo(
                                root.get("fechaInicio"),
                                parametros.getFechaInicioHasta()));
            }
        }

        return polizaRepository.findAll(spec);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Genera un número de póliza único basado en el timestamp del sistema.
     * Para pólizas de vida valida que no exista solapamiento de fechas con
     * otras pólizas de vida activas del mismo cliente.
     * </p>
     */
    @Override
    @Transactional
    public Poliza crearPolizaBase(ParametrosCrearPolizaBase parametros) {
        validarParametrosCrearPoliza(parametros);

        clienteRepository.findById(parametros.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        TiposPoliza tipoPoliza = tiposPolizaRepository.findById(parametros.getTipoPolizaId())
                .orElseThrow(() -> new RuntimeException("Tipo de poliza no encontrado"));

        // VALIDACIÓN VIDA
        if (tipoPoliza.getId().equals(Enums.TipoPoliza.VIDA.getValor())) {

            boolean existeVidaActiva = polizaRepository
                    .existePolizaUnicaXCliente(
                        parametros.getClienteId(),
                        tipoPoliza.getId(),
                        parametros.getFechaFin(),
                        parametros.getFechaInicio()
            );

            if (existeVidaActiva) {
                throw new ValidationException(
                        "El cliente ya tiene una póliza de vida activa en el rango de fechas indicado"
                );
            }
        }

        Poliza poliza = new Poliza();
        poliza.setClienteId(Math.toIntExact(parametros.getClienteId()));
        poliza.setTipoPoliza(tipoPoliza);
        poliza.setFechaInicio(parametros.getFechaInicio());
        poliza.setFechaFin(parametros.getFechaFin());
        poliza.setEstado(true);

        String numero;
        do {
            numero = "P-" + System.currentTimeMillis();
        } while (polizaRepository.existsByNumeroPoliza(numero));
        poliza.setNumeroPoliza(numero);

        return polizaRepository.save(poliza);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Por cada póliza base encontrada consulta y adjunta la especialización
     * correspondiente según el tipo de póliza (VIDA, SALUD, VEHICULO).
     * </p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<PolizaCompletoDTO> consultarPolizasCliente(ParametrosConsultarPolizasBase parametros) {

        List<Poliza> polizasBase = self.consultarPolizasBase(parametros);
        List<PolizaCompletoDTO> resultado = new ArrayList<>();

        for (Poliza poliza : polizasBase) {
            PolizaCompletoDTO dto = new PolizaCompletoDTO();
            dto.setId(poliza.getId());
            dto.setNumeroPoliza(poliza.getNumeroPoliza());
            dto.setFechaInicio(poliza.getFechaInicio());
            dto.setFechaFin(poliza.getFechaFin());
            dto.setEstado(poliza.getEstado());

            if (poliza.getTipoPoliza() != null) {
                dto.setTipoPolizaId(poliza.getTipoPoliza().getId());
                dto.setTipoPolizaCodigo(poliza.getTipoPoliza().getCodigo());

                Enums.TipoPoliza tipo;
                try {
                    tipo = Enums.TipoPoliza.fromId(poliza.getTipoPoliza().getId());
                } catch (IllegalArgumentException e) {
                    resultado.add(dto);
                    continue;
                }

                switch (tipo) {
                    case VIDA ->
                        polizasVidaRepository.findByPoliza_Id(poliza.getId())
                                .ifPresent(dto::setPolizaVida);
                    case SALUD ->
                        dto.setPolizasSalud(polizasSaludRepository.findAllByPoliza_Id(poliza.getId()));
                    case VEHICULO ->
                        dto.setPolizasVehiculo(polizasVehiculoRepository.findAllByPoliza_Id(poliza.getId()));
                }
            }

            resultado.add(dto);
        }

        return resultado;
    }

    /**
     * Valida los parámetros de creación de una póliza base.
     * <p>
     * Lanza {@link jakarta.validation.ValidationException} con la lista de errores
     * concatenada si algún campo obligatorio falta o es inválido.
     * </p>
     *
     * @param parametros parámetros a validar
     * @throws jakarta.validation.ValidationException si existen errores de validación
     */
    private void validarParametrosCrearPoliza(ParametrosCrearPolizaBase parametros) {
        if (parametros == null) throw new ValidationException("Parametros de creacion de poliza son requeridos");
        StringJoiner errores = new StringJoiner("; ");
        if (parametros.getClienteId() == null || parametros.getClienteId() <= 0) errores.add("clienteId es requerido y debe ser mayor a 0");
        if (parametros.getTipoPolizaId() == null || parametros.getTipoPolizaId() <= 0) errores.add("tipoPolizaId es requerido y debe ser mayor a 0");
        if (parametros.getFechaInicio() == null) errores.add("fechaInicio es requerida");
        if (parametros.getFechaFin() == null) errores.add("fechaFin es requerida");
        if (parametros.getFechaInicio() != null && parametros.getFechaFin() != null && parametros.getFechaInicio().isAfter(parametros.getFechaFin())) errores.add("fechaInicio no puede ser posterior a fechaFin");

        if (errores.length() > 0) throw new ValidationException(errores.toString());
    }
}
