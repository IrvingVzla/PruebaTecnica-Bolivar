package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposParentesco;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para el catálogo {@link TiposParentesco}.
 * <p>
 * Hereda las operaciones CRUD estándar de {@link JpaRepository}.
 * </p>
 */
public interface TiposParentescoRepository extends JpaRepository<TiposParentesco, Integer> {
}