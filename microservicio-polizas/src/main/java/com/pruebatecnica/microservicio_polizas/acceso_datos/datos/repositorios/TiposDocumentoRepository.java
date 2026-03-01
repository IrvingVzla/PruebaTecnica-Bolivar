package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposDocumento;

/**
 * Repositorio JPA para el catálogo {@link TiposDocumento}.
 * <p>
 * Hereda las operaciones CRUD estándar de {@link JpaRepository}.
 * </p>
 */
public interface TiposDocumentoRepository extends JpaRepository<TiposDocumento, Integer> {
}
