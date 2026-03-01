package com.pruebatecnica.microservicio_polizas.dominio.mapeos;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.ClienteDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.TipoDocumentoDTO;
import org.mapstruct.Mapper;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Cliente;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposDocumento;

/**
 * Mapper de MapStruct para convertir entre las entidades de cliente y sus DTOs.
 * <p>
 * Gestionado como bean de Spring mediante {@code componentModel = "spring"}.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface IClienteMapper {

    /**
     * Convierte una entidad {@link Cliente} a su DTO {@link ClienteDTO}.
     *
     * @param cliente entidad a convertir
     * @return DTO resultante
     */
    ClienteDTO toDto(Cliente cliente);

    /**
     * Convierte un {@link ClienteDTO} a la entidad {@link Cliente}.
     *
     * @param dto DTO a convertir
     * @return entidad resultante
     */
    Cliente toEntity(ClienteDTO dto);

    /**
     * Convierte una entidad {@link TiposDocumento} a su DTO {@link TipoDocumentoDTO}.
     *
     * @param tiposDocumento entidad a convertir
     * @return DTO resultante
     */
    TipoDocumentoDTO tipoDocumentoToDto(TiposDocumento tiposDocumento);

    /**
     * Convierte un {@link TipoDocumentoDTO} a la entidad {@link TiposDocumento}.
     *
     * @param dto DTO a convertir
     * @return entidad resultante
     */
    TiposDocumento tipoDocumentoToEntity(TipoDocumentoDTO dto);
}
