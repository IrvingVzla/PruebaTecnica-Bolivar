package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para el catálogo {@link TiposPoliza}.
 * <p>
 * Hereda las operaciones CRUD estándar de {@link JpaRepository}.
 * </p>
 */
@Repository
public interface TiposPolizaRepository extends JpaRepository<TiposPoliza, Integer> {
}
