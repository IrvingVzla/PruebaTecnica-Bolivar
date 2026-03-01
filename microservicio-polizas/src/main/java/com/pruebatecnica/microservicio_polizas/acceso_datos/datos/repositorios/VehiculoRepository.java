package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Vehiculo;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Vehiculo}.
 * <p>
 * Proporciona consultas derivadas para buscar vehículos y verificar duplicidad de placa
 * dentro de una misma póliza de vehículo.
 * </p>
 */
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    /**
     * Verifica si existe un vehículo con la placa indicada dentro de una póliza de vehículo.
     *
     * @param polizaVehiculoId identificador de la póliza de vehículo
     * @param placa            placa del vehículo a verificar (en mayúsculas)
     * @return {@code true} si existe; {@code false} en caso contrario
     */
    boolean existsByPolizasVehiculo_IdAndPlaca(Long polizaVehiculoId, String placa);

    /**
     * Obtiene todos los vehículos asociados a una póliza de vehículo específica.
     *
     * @param polizaVehiculoId identificador de la póliza de vehículo
     * @return lista de vehículos encontrados
     */
    List<Vehiculo> findByPolizasVehiculo_Id(Long polizaVehiculoId);
}
