package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasVehiculo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link PolizasVehiculo}.
 * <p>
 * Soporta operaciones CRUD estándar y consultas dinámicas mediante {@code Specification}.
 * </p>
 */
public interface PolizasVehiculoRepository extends JpaRepository<PolizasVehiculo, Long>, JpaSpecificationExecutor<PolizasVehiculo> {

    /**
     * Obtiene todas las pólizas de vehículo asociadas a una póliza base específica.
     *
     * @param polizaId identificador de la póliza base
     * @return lista de pólizas de vehículo encontradas
     */
    List<PolizasVehiculo> findAllByPoliza_Id(Long polizaId);
}
