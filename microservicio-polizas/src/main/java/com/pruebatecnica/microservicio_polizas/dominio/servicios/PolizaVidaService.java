package com.pruebatecnica.microservicio_polizas.dominio.servicios;

import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.BeneficiariosVidaRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.PolizaRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.PolizasVidaRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.TiposParentescoRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosConsultarPolizaVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosCrearBeneficiarioVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosCrearPolizaVida;
import com.pruebatecnica.microservicio_polizas.dominio.enums.Enums;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaVidaService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación del servicio de gestión de pólizas de vida y sus beneficiarios.
 * <p>
 * Garantiza que solo exista una especialización de vida por póliza base,
 * que la póliza base esté activa y que no se supere el límite de dos
 * beneficiarios por póliza de vida.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PolizaVidaService implements IPolizaVidaService {
    /** Repositorio de pólizas de vida. */
    private final PolizasVidaRepository polizasVidaRepository;
    /** Repositorio de beneficiarios de vida. */
    private final BeneficiariosVidaRepository beneficiariosVidaRepository;
    /** Repositorio de pólizas base. */
    private final PolizaRepository polizaRepository;
    /** Repositorio de tipos de parentesco. */
    private final TiposParentescoRepository tiposParentescoRepository;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public PolizasVida crearPolizaVida(ParametrosCrearPolizaVida parametros) {

        validarParametrosCrearPolizaVida(parametros);

        // Obtener póliza base
        Poliza poliza = polizaRepository
                .findById(parametros.getPolizaId())
                .orElseThrow(() -> new ValidationException("Póliza base no encontrada"));

        // Validar tipo VIDA
        if (!poliza.getTipoPoliza().getId()
                .equals(Enums.TipoPoliza.VIDA.getValor())) {
            throw new ValidationException("La póliza base no es de tipo VIDA");
        }

        // Validar que esté activa
        if (!Boolean.TRUE.equals(poliza.getEstado())) {
            throw new ValidationException("La póliza base no está activa");
        }

        // Validar que no exista especialización ya creada
        if (polizasVidaRepository.existsByPoliza_Id(parametros.getPolizaId())) {
            throw new ValidationException("Ya existe una póliza de vida asociada a esta póliza base");
        }

        // Crear especialización
        PolizasVida polizaVida = new PolizasVida();
        polizaVida.setPoliza(poliza); // @MapsId asigna el id automáticamente
        polizaVida.setMontoAsegurado(parametros.getMontoAsegurado());

        return polizasVidaRepository.save(polizaVida);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public BeneficiariosVida crearBeneficiarioVida(
            ParametrosCrearBeneficiarioVida parametros) {

        validarParametrosCrearBeneficiarioVida(parametros);

        // Obtener póliza vida
        PolizasVida polizaVida = polizasVidaRepository
                .findById(parametros.getPolizaVidaId())
                .orElseThrow(() ->
                        new ValidationException("Póliza de vida no encontrada"));

        // Validar que póliza base esté activa
        if (!Boolean.TRUE.equals(polizaVida.getPoliza().getEstado())) {
            throw new ValidationException("La póliza base no está activa");
        }

        // Validar máximo 2 beneficiarios
        long totalBeneficiarios = beneficiariosVidaRepository
                .countByPolizasVida_PolizaId(parametros.getPolizaVidaId());

        if (totalBeneficiarios >= 2) {
            throw new ValidationException(
                    "Una póliza de vida solo puede tener máximo 2 beneficiarios"
            );
        }

        // Validar documento duplicado
        if (parametros.getNumeroDocumento() != null &&
                !parametros.getNumeroDocumento().isBlank()) {

            boolean existe = beneficiariosVidaRepository
                    .existsByPolizasVida_PolizaIdAndNumeroDocumento(
                            parametros.getPolizaVidaId(),
                            parametros.getNumeroDocumento()
                    );

            if (existe) {
                throw new ValidationException(
                        "Ya existe un beneficiario con ese documento en esta póliza"
                );
            }
        }

        // Obtener tipo parentesco
        TiposParentesco tipoParentesco = tiposParentescoRepository
                .findById(parametros.getTipoParentescoId().intValue())
                .orElseThrow(() ->
                        new ValidationException("Tipo de parentesco no encontrado"));

        // Crear entidad
        BeneficiariosVida beneficiario = new BeneficiariosVida();
        beneficiario.setPolizasVida(polizaVida);
        beneficiario.setTipoParentesco(tipoParentesco);
        beneficiario.setNombreCompleto(parametros.getNombreCompleto());
        beneficiario.setNumeroDocumento(parametros.getNumeroDocumento());

        return beneficiariosVidaRepository.save(beneficiario);
    }

    /** {@inheritDoc} */
    @Override
    public List<PolizasVida> consultarPolizaVida(
            ParametrosConsultarPolizaVida parametros) {

        Specification<PolizasVida> spec = Specification.allOf();

        if (parametros.getId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("id"), parametros.getId()));
        }

        if (parametros.getPolizaId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("poliza").get("id"),
                            parametros.getPolizaId()));
        }

        List<PolizasVida> resultado =
                polizasVidaRepository.findAll(spec);

        if (resultado.isEmpty()) {
            throw new ValidationException("No se encontraron pólizas de vida");
        }

        return resultado;
    }

    /** {@inheritDoc} */
    @Override
    public List<BeneficiariosVida> consultarBeneficiariosVida(Long polizaVidaId) {

        if (polizaVidaId == null || polizaVidaId <= 0)
            throw new ValidationException("polizaVidaId es requerido y debe ser mayor a 0");

        if (!polizasVidaRepository.existsById(polizaVidaId))
            throw new ValidationException("Póliza de vida no encontrada");

        return beneficiariosVidaRepository.findByPolizasVida_PolizaId(polizaVidaId);
    }

    // Validaciones

    /**
     * Valida los parámetros de creación de una póliza de vida.
     *
     * @param parametros parámetros a validar
     * @throws jakarta.validation.ValidationException si el id de póliza es nulo o el monto asegurado no es positivo
     */
    private void validarParametrosCrearPolizaVida(ParametrosCrearPolizaVida parametros) {

        if (parametros == null)
            throw new ValidationException("Parámetros requeridos");

        if (parametros.getPolizaId() == null || parametros.getPolizaId() <= 0)
            throw new ValidationException("polizaId es requerido y debe ser mayor a 0");

        if (parametros.getMontoAsegurado() == null ||
                parametros.getMontoAsegurado().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("montoAsegurado debe ser mayor a 0");
    }

    /**
     * Valida los parámetros de creación de un beneficiario de vida.
     *
     * @param parametros parámetros a validar
     * @throws jakarta.validation.ValidationException si el id de póliza, el tipo de parentesco
     *                                               o el nombre completo son inválidos
     */
    private void validarParametrosCrearBeneficiarioVida(
            ParametrosCrearBeneficiarioVida parametros) {

        if (parametros == null)
            throw new ValidationException("Parámetros requeridos");

        if (parametros.getPolizaVidaId() == null ||
                parametros.getPolizaVidaId() <= 0)
            throw new ValidationException("polizaVidaId es requerido");

        if (parametros.getTipoParentescoId() == null ||
                parametros.getTipoParentescoId() <= 0)
            throw new ValidationException("tipoParentescoId es requerido");

        if (parametros.getNombreCompleto() == null ||
                parametros.getNombreCompleto().isBlank())
            throw new ValidationException("nombreCompleto es requerido");
    }
}
