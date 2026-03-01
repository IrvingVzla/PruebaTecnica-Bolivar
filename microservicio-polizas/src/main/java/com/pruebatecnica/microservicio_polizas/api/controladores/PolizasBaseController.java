package com.pruebatecnica.microservicio_polizas.api.controladores;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaCompletoDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaResumenDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Poliza;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base.ParametrosConsultarPolizasBase;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base.ParametrosCrearPolizaBase;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaBaseService;
import com.pruebatecnica.microservicio_polizas.dominio.mapeos.IPolizaMapper;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pólizas base.
 * <p>
 * Expone los endpoints de consulta y creación de pólizas base bajo la ruta {@code /api},
 * así como la consulta completa de pólizas de un cliente con sus especializaciones.
 * </p>
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PolizasBaseController {
    /** Servicio de negocio para la gestión de pólizas base. */
    private final IPolizaBaseService service;
    /** Mapper para convertir entre entidades y DTOs de póliza. */
    private final IPolizaMapper mapper;

    /**
     * Lista las pólizas base aplicando los filtros opcionales indicados como query params.
     *
     * @param parametros criterios de búsqueda
     * @return lista de pólizas en formato de resumen
     */
    @GetMapping("/consultar-polizas-base")
    public List<PolizaResumenDTO> listarPolizasCliente(@ParameterObject @ModelAttribute ParametrosConsultarPolizasBase parametros) {
        List<Poliza> entidades = service.consultarPolizasBase(parametros);
        return entidades.stream().map(mapper::toResumenDto).toList();
    }

    /**
     * Crea una nueva póliza base con los datos del cuerpo de la solicitud.
     *
     * @param parametros datos de la póliza a crear
     * @return la póliza creada como DTO
     */
    @PostMapping("/crear-poliza-base")
    public PolizaDTO crearPolizaBase(@RequestBody ParametrosCrearPolizaBase parametros) {
        Poliza creado = service.crearPolizaBase(parametros);
        return mapper.toDto(creado);
    }

    /**
     * Consulta las pólizas de un cliente incluyendo los detalles completos de cada especialización.
     *
     * @param parametros criterios de filtrado, típicamente el {@code clienteId}
     * @return lista de pólizas completas con sus especializaciones
     */
    @GetMapping("/consultar-polizas-cliente")
    public List<PolizaCompletoDTO> consultarPolizasCliente(@ParameterObject @ModelAttribute ParametrosConsultarPolizasBase parametros) {
        return service.consultarPolizasCliente(parametros);
    }
}
