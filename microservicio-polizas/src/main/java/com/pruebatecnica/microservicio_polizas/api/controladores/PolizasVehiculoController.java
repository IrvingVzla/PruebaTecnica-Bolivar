package com.pruebatecnica.microservicio_polizas.api.controladores;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Vehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaVehiculoDto;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.VehiculoDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosConsultarPolizaVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosCrearPolizaVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosCrearVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaVehiculoService;
import com.pruebatecnica.microservicio_polizas.dominio.mapeos.IPolizaMapper;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de pólizas de vehículo y sus vehículos asegurados.
 * <p>
 * Expone los endpoints de creación y consulta bajo la ruta base {@code /api/polizas/vehiculo}.
 * </p>
 */
@RestController
@RequestMapping("/api/polizas/vehiculo")
@RequiredArgsConstructor
public class PolizasVehiculoController {
    /** Servicio de negocio para la gestión de pólizas de vehículo. */
    private final IPolizaVehiculoService service;
    /** Mapper para convertir entre entidades y DTOs de póliza. */
    private final IPolizaMapper mapper;

    /**
     * Crea una especialización de vehículo sobre una póliza base existente.
     *
     * @param parametros datos de la póliza de vehículo a crear
     * @return la póliza de vehículo creada como DTO
     */
    @PostMapping("/crear-poliza-vehiculo")
    public PolizaVehiculoDto crearPolizaVehiculo(@RequestBody ParametrosCrearPolizaVehiculo parametros) {
        PolizasVehiculo creado = service.crearPolizaVehiculo(parametros);
        return mapper.toDto(creado);
    }

    /**
     * Registra un nuevo vehículo en una póliza de vehículo existente.
     *
     * @param parametros datos del vehículo a registrar
     * @return el vehículo creado como DTO
     */
    @PostMapping("/crear-vehiculo")
    public VehiculoDTO crearVehiculo(@RequestBody ParametrosCrearVehiculo parametros) {
        Vehiculo creado = service.crearVehiculo(parametros);
        return mapper.toDto(creado);
    }

    /**
     * Consulta pólizas de vehículo según los filtros indicados como query params.
     *
     * @param parametros criterios de búsqueda
     * @return lista de pólizas de vehículo encontradas como DTOs
     */
    @GetMapping("/consultar-polizas-vehiculo")
    public List<PolizaVehiculoDto> consultarPolizasVehiculo(@ParameterObject @ModelAttribute ParametrosConsultarPolizaVehiculo parametros) {
        List<PolizasVehiculo> pol = service.consultarPolizasVehiculo(parametros);
        return mapper.toPolizaVehiculoDtoList(pol);
    }

    /**
     * Obtiene los vehículos asegurados de una póliza de vehículo por su identificador.
     *
     * @param polizaVehiculoId identificador de la póliza de vehículo
     * @return lista de vehículos asegurados como DTOs
     */
    @GetMapping("/consultar-vehiculos/{polizaVehiculoId}")
    public List<VehiculoDTO> consultarVehiculos(@PathVariable Long polizaVehiculoId) {
        List<Vehiculo> consulta = service.consultarVehiculos(polizaVehiculoId);
        return mapper.toVehiculoDtoList(consulta);
    }
}
