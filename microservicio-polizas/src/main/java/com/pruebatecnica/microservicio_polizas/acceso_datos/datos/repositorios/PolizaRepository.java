package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Poliza;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Poliza}.
 * <p>
 * Extiende {@link JpaRepository} y {@link JpaSpecificationExecutor} para soportar
 * operaciones CRUD estándar y consultas dinámicas mediante {@code Specification}.
 * </p>
 */
public interface PolizaRepository extends JpaRepository<Poliza, Long>, JpaSpecificationExecutor<Poliza> {

    /**
     * Verifica si existe una póliza con el número indicado.
     *
     * @param numeroPoliza número único de la póliza
     * @return {@code true} si existe; {@code false} en caso contrario
     */
    boolean existsByNumeroPoliza(String numeroPoliza);

    /**
     * Verifica si el cliente ya tiene una póliza activa del tipo indicado
     * que se solape con el rango de fechas dado.
     *
     * @param clienteId    identificador del cliente
     * @param tipoPolizaId identificador del tipo de póliza
     * @param fechaFin     fecha de fin del nuevo rango
     * @param fechaInicio  fecha de inicio del nuevo rango
     * @return {@code true} si existe solapamiento; {@code false} en caso contrario
     */
    @Query("""
        SELECT COUNT(p) > 0
        FROM Poliza p
        WHERE p.clienteId = :clienteId
          AND p.tipoPoliza.id = :tipoPolizaId
          AND p.estado = true
          AND p.fechaInicio <= :fechaFin
          AND p.fechaFin >= :fechaInicio
    """)
    boolean existePolizaUnicaXCliente(
            @Param("clienteId") Long clienteId,
            @Param("tipoPolizaId") Integer tipoPolizaId,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("fechaInicio") LocalDate fechaInicio
    );

    /**
     * Busca una póliza por su identificador.
     *
     * @param id identificador de la póliza
     * @return un {@link Optional} con la póliza encontrada, o vacío si no existe
     */
    @Query("SELECT p FROM Poliza p WHERE p.id = :id")
    Optional<Poliza> findPolizaById(@Param("id") Long id);
}
