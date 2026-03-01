package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.BeneficiariosVida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link BeneficiariosVida}.
 * <p>
 * Proporciona consultas derivadas para buscar y contar beneficiarios de pólizas de vida.
 * </p>
 */
@Repository
public interface BeneficiariosVidaRepository extends JpaRepository<BeneficiariosVida, Long> {

    /**
     * Obtiene los beneficiarios de una póliza de vida por el identificador de la póliza base.
     *
     * @param polizaVidaId identificador de la póliza base asociada a la póliza de vida
     * @return lista de beneficiarios encontrados
     */
    List<BeneficiariosVida> findByPolizasVida_PolizaId(Long polizaVidaId);

    /**
     * Cuenta los beneficiarios de una póliza de vida por el identificador de la póliza base.
     *
     * @param polizaVidaId identificador de la póliza base asociada a la póliza de vida
     * @return número de beneficiarios
     */
    long countByPolizasVida_PolizaId(Long polizaVidaId);

    /**
     * Verifica si existe un beneficiario con el documento dado en una póliza de vida específica.
     *
     * @param polizaVidaId    identificador de la póliza base asociada a la póliza de vida
     * @param numeroDocumento número de documento a verificar
     * @return {@code true} si existe; {@code false} en caso contrario
     */
    boolean existsByPolizasVida_PolizaIdAndNumeroDocumento(Long polizaVidaId, String numeroDocumento);
}
