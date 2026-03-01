package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasSalud;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link PolizasSalud}.
 * <p>
 * Soporta operaciones CRUD estándar y consultas dinámicas mediante {@code Specification}.
 * </p>
 */
public interface PolizasSaludRepository extends JpaRepository<PolizasSalud, Long>, JpaSpecificationExecutor<PolizasSalud> {

    /**
     * Obtiene todas las pólizas de salud asociadas a una póliza base específica.
     *
     * @param polizaId identificador de la póliza base
     * @return lista de pólizas de salud encontradas
     */
    List<PolizasSalud> findAllByPoliza_Id(Long polizaId);
}
