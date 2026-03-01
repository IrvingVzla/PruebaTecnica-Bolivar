package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.BeneficiariosSalud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link BeneficiariosSalud}.
 * <p>
 * Proporciona consultas derivadas para buscar y contar beneficiarios de pólizas de salud
 * según póliza, parentesco y número de documento.
 * </p>
 */
public interface BeneficiariosSaludRepository extends JpaRepository<BeneficiariosSalud, Long> {

    /**
     * Obtiene los beneficiarios cuya póliza de salud esté asociada a una póliza base específica.
     *
     * @param polizaId identificador de la póliza base
     * @return lista de beneficiarios encontrados
     */
    List<BeneficiariosSalud> findByPolizaSalud_PolizaId(Long polizaId);

    /**
     * Obtiene los beneficiarios de una póliza de salud específica.
     *
     * @param polizaSaludId identificador de la póliza de salud
     * @return lista de beneficiarios encontrados
     */
    List<BeneficiariosSalud> findByPolizaSalud_Id(Long polizaSaludId);

    /**
     * Cuenta los beneficiarios de una póliza de salud por su póliza base.
     *
     * @param polizaId identificador de la póliza base
     * @return número de beneficiarios
     */
    long countByPolizaSalud_PolizaId(Long polizaId);

    /**
     * Cuenta los beneficiarios de un tipo de parentesco específico en una póliza base.
     *
     * @param polizaId         identificador de la póliza base
     * @param tipoParentescoId identificador del tipo de parentesco
     * @return número de beneficiarios del parentesco indicado
     */
    long countByPolizaSalud_PolizaIdAndTipoParentesco_Id(Long polizaId, Integer tipoParentescoId);

    /**
     * Verifica si existe un beneficiario con el documento dado en una póliza de salud específica.
     *
     * @param polizaSaludId   identificador de la póliza de salud
     * @param numeroDocumento número de documento a verificar
     * @return {@code true} si existe; {@code false} en caso contrario
     */
    boolean existsByPolizaSalud_IdAndNumeroDocumento(Long polizaSaludId, String numeroDocumento);

    /**
     * Cuenta los beneficiarios de un tipo de parentesco en una póliza de salud específica.
     *
     * @param polizaSaludId    identificador de la póliza de salud
     * @param tipoParentescoId identificador del tipo de parentesco
     * @return número de beneficiarios del parentesco indicado
     */
    long countByPolizaSalud_IdAndTipoParentesco_Id(Long polizaSaludId, Integer tipoParentescoId);
}