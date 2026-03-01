package com.pruebatecnica.microservicio_polizas.api.controladores;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosConsultarPolizaVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosCrearBeneficiarioVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosCrearPolizaVida;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaVidaService;
import com.pruebatecnica.microservicio_polizas.dominio.mapeos.IPolizaMapper;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pólizas de vida y sus beneficiarios.
 * <p>
 * Expone los endpoints de creación y consulta bajo la ruta base {@code /api/polizas/vida}.
 * </p>
 */
@RestController
@RequestMapping("/api/polizas/vida")
@RequiredArgsConstructor
public class PolizasVidaController {
    /** Servicio de negocio para la gestión de pólizas de vida. */
    private final IPolizaVidaService service;
    /** Mapper para convertir entre entidades y DTOs de póliza. */
    private final IPolizaMapper mapper;

    /**
     * Crea una especialización de vida sobre una póliza base existente.
     *
     * @param parametros datos de la póliza de vida a crear
     * @return la póliza de vida creada como DTO
     */
    @PostMapping("/crear-poliza-vida")
    public PolizaVidaDTO crearPolizaVida(@RequestBody ParametrosCrearPolizaVida parametros) {
        PolizasVida creado = service.crearPolizaVida(parametros);
        return mapper.toDto(creado);
    }

    /**
     * Registra un nuevo beneficiario en una póliza de vida existente.
     *
     * @param parametros datos del beneficiario a registrar
     * @return el beneficiario creado como DTO
     */
    @PostMapping("/crear-beneficiario-vida")
    public BeneficiarioVidaDTO crearBeneficiarioVida(@RequestBody ParametrosCrearBeneficiarioVida parametros) {
        BeneficiariosVida creado = service.crearBeneficiarioVida(parametros);
        return mapper.toDto(creado);
    }

    /**
     * Consulta pólizas de vida según los filtros indicados como query params.
     *
     * @param parametros criterios de búsqueda
     * @return lista de pólizas de vida encontradas como DTOs
     */
    @GetMapping("/consultar-polizas-vida")
    public List<PolizaVidaDTO> consultarPolizaVida(@ParameterObject @ModelAttribute ParametrosConsultarPolizaVida parametros) {
        List<PolizasVida> pol = service.consultarPolizaVida(parametros);
        return mapper.toPolizaVidaDtoList(pol);
    }

    /**
     * Obtiene los beneficiarios de una póliza de vida por su identificador.
     *
     * @param polizaVidaId identificador de la póliza de vida
     * @return lista de beneficiarios de vida como DTOs
     */
    @GetMapping("/consultar-beneficiarios-vida/{polizaVidaId}")
    public List<BeneficiarioVidaDTO> consultarBeneficiariosVida(@PathVariable Long polizaVidaId) {
        List<BeneficiariosVida> beneficiarios = service.consultarBeneficiariosVida(polizaVidaId);
        return mapper.toBeneficiarioVidaDtoList(beneficiarios);
    }
}
