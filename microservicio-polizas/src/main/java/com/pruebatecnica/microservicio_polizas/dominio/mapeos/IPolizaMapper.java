package com.pruebatecnica.microservicio_polizas.dominio.mapeos;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper de MapStruct para convertir entre las entidades de pólizas y sus DTOs.
 * <p>
 * Gestionado como bean de Spring mediante {@code componentModel = "spring"}.
 * Incluye mapeos para pólizas base, especializaciones (vida, salud, vehículo),
 * beneficiarios y vehículos asegurados.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface IPolizaMapper {

    /**
     * Convierte una entidad {@link Poliza} a {@link PolizaDTO}.
     * Extrae el id y código del tipo de póliza mediante mapeos explícitos.
     *
     * @param poliza entidad a convertir
     * @return DTO resultante
     */
    @Mapping(target = "tipoPolizaId", source = "tipoPoliza.id")
    @Mapping(target = "tipoPolizaCodigo", source = "tipoPoliza", qualifiedByName = "tiposPolizaToCodigo")
    PolizaDTO toDto(Poliza poliza);

    /**
     * Convierte una entidad {@link Poliza} a {@link PolizaResumenDTO}.
     *
     * @param poliza entidad a convertir
     * @return DTO de resumen resultante
     */
    @Mapping(target = "tipoPolizaId", source = "tipoPoliza.id")
    @Mapping(target = "tipoPolizaCodigo", source = "tipoPoliza", qualifiedByName = "tiposPolizaToCodigo")
    PolizaResumenDTO toResumenDto(Poliza poliza);

    /**
     * Convierte una entidad {@link Poliza} a {@link PolizaCompletoDTO}.
     *
     * @param poliza entidad a convertir
     * @return DTO completo resultante
     */
    @Mapping(target = "tipoPolizaId", source = "tipoPoliza.id")
    @Mapping(target = "tipoPolizaCodigo", source = "tipoPoliza", qualifiedByName = "tiposPolizaToCodigo")
    PolizaCompletoDTO toCompletoDto(Poliza poliza);

    /**
     * Convierte una entidad {@link PolizasSalud} a {@link PolizaSaludDto}.
     *
     * @param p entidad a convertir
     * @return DTO resultante
     */
    PolizaSaludDto toDto(PolizasSalud p);

    /**
     * Convierte una entidad {@link PolizasVehiculo} a {@link PolizaVehiculoDto}.
     *
     * @param p entidad a convertir
     * @return DTO resultante
     */
    PolizaVehiculoDto toDto(PolizasVehiculo p);

    /**
     * Convierte una entidad {@link PolizasVida} a {@link PolizaVidaDTO}.
     *
     * @param p entidad a convertir
     * @return DTO resultante
     */
    @Mapping(target = "id", source = "id")
    PolizaVidaDTO toDto(PolizasVida p);

    /**
     * Convierte una entidad {@link BeneficiariosSalud} a {@link BeneficiarioSaludDTO}.
     *
     * @param b entidad a convertir
     * @return DTO resultante
     */
    BeneficiarioSaludDTO toDto(BeneficiariosSalud b);

    /**
     * Convierte una entidad {@link BeneficiariosVida} a {@link BeneficiarioVidaDTO}.
     *
     * @param b entidad a convertir
     * @return DTO resultante
     */
    BeneficiarioVidaDTO toDto(BeneficiariosVida b);

    /**
     * Convierte una entidad {@link Vehiculo} a {@link VehiculoDTO}.
     *
     * @param v entidad a convertir
     * @return DTO resultante
     */
    VehiculoDTO toDto(Vehiculo v);

    /**
     * Convierte una lista de {@link PolizasSalud} a lista de {@link PolizaSaludDto}.
     *
     * @param lista lista de entidades
     * @return lista de DTOs
     */
    List<PolizaSaludDto> toPolizaSaludDtoList(List<PolizasSalud> lista);

    /**
     * Convierte una lista de {@link PolizasVehiculo} a lista de {@link PolizaVehiculoDto}.
     *
     * @param listaVehiculosPoliza lista de entidades
     * @return lista de DTOs
     */
    List<PolizaVehiculoDto> toPolizaVehiculoDtoList(List<PolizasVehiculo> listaVehiculosPoliza);

    /**
     * Convierte una lista de {@link PolizasVida} a lista de {@link PolizaVidaDTO}.
     *
     * @param listaVida lista de entidades
     * @return lista de DTOs
     */
    List<PolizaVidaDTO> toPolizaVidaDtoList(List<PolizasVida> listaVida);

    /**
     * Convierte una lista de {@link BeneficiariosSalud} a lista de {@link BeneficiarioSaludDTO}.
     *
     * @param listaBeneficiariosSalud lista de entidades
     * @return lista de DTOs
     */
    List<BeneficiarioSaludDTO> toBeneficiarioSaludDtoList(List<BeneficiariosSalud> listaBeneficiariosSalud);

    /**
     * Convierte una lista de {@link BeneficiariosVida} a lista de {@link BeneficiarioVidaDTO}.
     *
     * @param listaBeneficiariosVida lista de entidades
     * @return lista de DTOs
     */
    List<BeneficiarioVidaDTO> toBeneficiarioVidaDtoList(List<BeneficiariosVida> listaBeneficiariosVida);

    /**
     * Convierte una lista de {@link Vehiculo} a lista de {@link VehiculoDTO}.
     *
     * @param listaVehiculos lista de entidades
     * @return lista de DTOs
     */
    List<VehiculoDTO> toVehiculoDtoList(List<Vehiculo> listaVehiculos);

    /**
     * Convierte un {@link TiposParentesco} a su descripción textual.
     *
     * @param value entidad de tipo parentesco; puede ser {@code null}
     * @return descripción del parentesco, o {@code null} si la entidad es nula
     */
    @Named("tiposParentescoToDescripcion")
    default String tiposParentescoToDescripcion(TiposParentesco value) {
        return value == null ? null : value.getDescripcion();
    }

    /**
     * Convierte un {@link TiposPoliza} a su código.
     *
     * @param value entidad de tipo póliza; puede ser {@code null}
     * @return código del tipo de póliza, o {@code null} si la entidad es nula
     */
    @Named("tiposPolizaToCodigo")
    default String tiposPolizaToCodigo(TiposPoliza value) {
        return value == null ? null : value.getCodigo();
    }

    /**
     * Convierte un {@link TiposCoberturaSalud} a su código.
     *
     * @param value entidad de cobertura de salud; puede ser {@code null}
     * @return código de la cobertura, o {@code null} si la entidad es nula
     */
    @Named("tiposCoberturaSaludToCodigo")
    default String tiposCoberturaSaludToCodigo(TiposCoberturaSalud value) {
        return value == null ? null : value.getCodigo();
    }
}
