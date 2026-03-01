package com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Cliente;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Cliente}.
 * <p>
 * Extiende {@link JpaRepository} y {@link JpaSpecificationExecutor} para soportar
 * operaciones CRUD estándar y consultas dinámicas mediante {@code Specification}.
 * </p>
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

    /**
     * Busca un cliente por tipo de documento e identificador de documento.
     *
     * @param tipoDocumentoId identificador del tipo de documento
     * @param numeroDocumento número del documento de identidad
     * @return un {@link Optional} con el cliente encontrado, o vacío si no existe
     */
    Optional<Cliente> findByTipoDocumentoIdAndNumeroDocumento(Integer tipoDocumentoId, String numeroDocumento);

    /**
     * Verifica si existe un cliente con el tipo y número de documento indicados.
     *
     * @param tipoDocumentoId identificador del tipo de documento
     * @param numeroDocumento número del documento de identidad
     * @return {@code true} si existe; {@code false} en caso contrario
     */
    boolean existsByTipoDocumento_IdAndNumeroDocumento(Integer tipoDocumentoId, String numeroDocumento);
}