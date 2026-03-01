package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasVida;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link PolizasVida}.
 * <p>
 * Soporta operaciones CRUD estándar y consultas dinámicas mediante {@code Specification}.
 * </p>
 */
public interface PolizasVidaRepository extends JpaRepository<PolizasVida, Long>, JpaSpecificationExecutor<PolizasVida> {

    /**
     * Verifica si existe una póliza de vida asociada a la póliza base indicada.
     *
     * @param polizaId identificador de la póliza base
     * @return {@code true} si existe; {@code false} en caso contrario
     */
    boolean existsByPoliza_Id(Long polizaId);

    /**
     * Busca la póliza de vida asociada a una póliza base específica.
     *
     * @param polizaId identificador de la póliza base
     * @return un {@link Optional} con la póliza de vida encontrada, o vacío si no existe
     */
    Optional<PolizasVida> findByPoliza_Id(Long polizaId);
}
