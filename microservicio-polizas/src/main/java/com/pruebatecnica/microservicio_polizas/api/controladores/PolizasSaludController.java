package com.pruebatecnica.microservicio_polizas.api.controladores;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.BeneficiariosSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.BeneficiarioSaludDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaSaludDto;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosConsultarPolizaSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosCrearBeneficiarioSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosCrearPolizaSalud;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaSaludService;
import com.pruebatecnica.microservicio_polizas.dominio.mapeos.IPolizaMapper;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pólizas de salud y sus beneficiarios.
 * <p>
 * Expone los endpoints de creación y consulta bajo la ruta base {@code /api/polizas/salud}.
 * </p>
 */
@RestController
@RequestMapping("/api/polizas/salud")
@RequiredArgsConstructor
public class PolizasSaludController {
    /** Servicio de negocio para la gestión de pólizas de salud. */
    private final IPolizaSaludService service;
    /** Mapper para convertir entre entidades y DTOs de póliza. */
    private final IPolizaMapper mapper;

    /**
     * Crea una especialización de salud sobre una póliza base existente.
     *
     * @param parametros datos de la póliza de salud a crear
     * @return la póliza de salud creada como DTO
     */
    @PostMapping("/crear-poliza-salud")
    public PolizaSaludDto crearPolizaSalud(@RequestBody ParametrosCrearPolizaSalud parametros) {
        PolizasSalud creado = service.crearPolizaSalud(parametros);
        return mapper.toDto(creado);
    }

    /**
     * Registra un nuevo beneficiario en una póliza de salud existente.
     *
     * @param parametros datos del beneficiario a registrar
     * @return el beneficiario creado como DTO
     */
    @PostMapping("/crear-beneficiario-salud")
    public BeneficiarioSaludDTO crearBeneficiarioSalud(@RequestBody ParametrosCrearBeneficiarioSalud parametros) {
        BeneficiariosSalud creado = service.crearBeneficiarioSalud(parametros);
        return mapper.toDto(creado);
    }

    /**
     * Consulta pólizas de salud según los filtros indicados como query params.
     *
     * @param parametrosConsultarPolizaSalud criterios de búsqueda
     * @return lista de pólizas de salud encontradas como DTOs
     */
    @GetMapping("/consultar-polizas-salud")
    public List<PolizaSaludDto> consultarPolizaSalud(@ParameterObject @ModelAttribute ParametrosConsultarPolizaSalud parametrosConsultarPolizaSalud) {
       List<PolizasSalud> pol = service.consultarPolizaSalud(parametrosConsultarPolizaSalud);
        return mapper.toPolizaSaludDtoList(pol);
    }

    /**
     * Obtiene los beneficiarios de una póliza de salud por su identificador.
     *
     * @param polizaSaludId identificador de la póliza de salud
     * @return lista de beneficiarios de salud como DTOs
     */
    @GetMapping("/consultar-beneficiarios-salud/{polizaSaludId}")
    public List<BeneficiarioSaludDTO> consultarBeneficiariosSalud(@PathVariable Long polizaSaludId) {
         List<BeneficiariosSalud> beneficiarios = service.consultarBeneficiariosSalud(polizaSaludId);
        return mapper.toBeneficiarioSaludDtoList(beneficiarios);
    }
}
