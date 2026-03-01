package com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Vehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosConsultarPolizaVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosCrearPolizaVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosCrearVehiculo;

import java.util.List;

/**
 * Contrato de servicio para la gestión de pólizas de vehículo y sus vehículos asegurados.
 */
public interface IPolizaVehiculoService {

    /**
     * Crea la especialización de vehículo sobre una póliza base de tipo VEHICULO.
     *
     * @param parametros datos de la póliza de vehículo a crear
     * @return la póliza de vehículo creada
     * @throws jakarta.validation.ValidationException si la póliza base no existe, no es de tipo VEHICULO o no está activa
     */
    PolizasVehiculo crearPolizaVehiculo(ParametrosCrearPolizaVehiculo parametros);

    /**
     * Registra un nuevo vehículo en una póliza de vehículo existente.
     * <p>
     * La placa del vehículo debe ser única dentro de la misma póliza de vehículo.
     * </p>
     *
     * @param parametros datos del vehículo a registrar
     * @return el vehículo creado
     * @throws jakarta.validation.ValidationException si los parámetros no son válidos o la placa ya existe en la póliza
     */
    Vehiculo crearVehiculo(ParametrosCrearVehiculo parametros);

    /**
     * Consulta pólizas de vehículo según los filtros indicados.
     *
     * @param parametros criterios de filtrado
     * @return lista de pólizas de vehículo encontradas
     * @throws jakarta.validation.ValidationException si no se encuentran resultados
     */
    List<PolizasVehiculo> consultarPolizasVehiculo(ParametrosConsultarPolizaVehiculo parametros);

    /**
     * Obtiene todos los vehículos asociados a una póliza de vehículo.
     *
     * @param polizaVehiculoId identificador de la póliza de vehículo
     * @return lista de vehículos asegurados
     * @throws jakarta.validation.ValidationException si la póliza de vehículo no existe
     */
    List<Vehiculo> consultarVehiculos(Long polizaVehiculoId);
}
