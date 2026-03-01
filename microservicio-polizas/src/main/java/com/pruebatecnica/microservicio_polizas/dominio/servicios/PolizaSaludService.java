package com.pruebatecnica.microservicio_polizas.dominio.servicios;

import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosConsultarPolizaSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosCrearBeneficiarioSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosCrearPolizaSalud;
import com.pruebatecnica.microservicio_polizas.dominio.enums.Enums;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaSaludService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación del servicio de gestión de pólizas de salud y sus beneficiarios.
 * <p>
 * Aplica las reglas de negocio según el tipo de cobertura de salud seleccionado
 * (solo cliente, cliente y padres, cliente cónyuge e hijos) para validar
 * la creación de beneficiarios.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PolizaSaludService implements IPolizaSaludService {
    /** Repositorio de pólizas de salud. */
    private final PolizasSaludRepository polizasSaludRepository;
    /** Repositorio de pólizas base. */
    private final PolizaRepository polizaBaseRepository;
    /** Repositorio de tipos de cobertura de salud. */
    private final TiposCoberturaSaludRepository tiposCoberturaSaludRepository;
    /** Repositorio de beneficiarios de salud. */
    private final BeneficiariosSaludRepository beneficiariosSaludRepository;
    /** Repositorio de tipos de parentesco. */
    private final TiposParentescoRepository tiposParentescoRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public PolizasSalud crearPolizaSalud(ParametrosCrearPolizaSalud parametros) {

        validarParametrosCrearPolizaSalud(parametros);

        // Obtener póliza base
        Poliza poliza = polizaBaseRepository.findPolizaById(parametros.getPolizaId())
                .orElseThrow(() -> new ValidationException("Póliza base no encontrada"));

        // Validar que sea tipo SALUD
        Integer tipoPolizaId = poliza.getTipoPoliza().getId();

        if (!tipoPolizaId.equals(Enums.TipoPoliza.SALUD.getValor())) {
            throw new ValidationException(
                    "La póliza base no es de tipo SALUD"
            );
        }

        //Obtener tipo cobertura
        TiposCoberturaSalud tipoCobertura = tiposCoberturaSaludRepository
                .findById(parametros.getTipoCoberturaId())
                .orElseThrow(() -> new ValidationException("Tipo de cobertura no encontrado"));

        // Crear entidad especialización
        PolizasSalud polizaSalud = new PolizasSalud();
        polizaSalud.setPoliza(poliza);
        polizaSalud.setTipoCobertura(tipoCobertura);

        return polizasSaludRepository.save(polizaSalud);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public BeneficiariosSalud crearBeneficiarioSalud(ParametrosCrearBeneficiarioSalud parametros) {

        validarParametrosCrearBeneficiarioSalud(parametros);

        // Obtener póliza salud
        PolizasSalud polizaSalud = polizasSaludRepository
                .findById(parametros.getPolizaSaludId())
                .orElseThrow(() -> new ValidationException("Póliza de salud no encontrada"));

        // Validar documento duplicado
        boolean documentoExiste = beneficiariosSaludRepository
                .existsByPolizaSalud_IdAndNumeroDocumento(
                        polizaSalud.getId(),
                        parametros.getNumeroDocumento()
                );

        if (documentoExiste) {
            throw new ValidationException(
                    "Ya existe un beneficiario con ese número de documento en esta póliza"
            );
        }

        // Obtener tipo parentesco
        TiposParentesco tipoParentesco = tiposParentescoRepository
                .findById(parametros.getTipoParentescoId().intValue())
                .orElseThrow(() -> new ValidationException("Tipo de parentesco no encontrado"));

        // Convertir a enums
        Enums.TipoCoberturaSalud coberturaEnum = Enums.TipoCoberturaSalud.fromId(polizaSalud.getTipoCobertura().getId());

        Enums.TipoParentesco parentescoEnum = Enums.TipoParentesco.fromId(tipoParentesco.getId());

        // Validar según cobertura
        switch (coberturaEnum) {

            case SOLO_CLIENTE:
                throw new ValidationException(
                        "La cobertura SOLO_CLIENTE no permite beneficiarios"
                );

            case CLIENTE_PADRES:

                if (!parentescoEnum.esPadreOMadre()) {
                    throw new ValidationException(
                            "La cobertura CLIENTE_PADRES solo permite PADRE o MADRE"
                    );
                }

                // Validar 1 padre y 1 madre máximo
                long totalMismoParentesco = beneficiariosSaludRepository
                        .countByPolizaSalud_IdAndTipoParentesco_Id(
                                polizaSalud.getId(),
                                tipoParentesco.getId()
                        );

                if (totalMismoParentesco >= 1) {
                    throw new ValidationException(
                            "Solo se permite un " + parentescoEnum.name() + " en esta cobertura"
                    );
                }

                break;

            case CLIENTE_CONYUGE_HIJOS:

                if (!parentescoEnum.esConyugeOHijo()) {
                    throw new ValidationException(
                            "La cobertura CLIENTE_CONYUGE_HIJOS solo permite CONYUGE o HIJO"
                    );
                }

                // Solo 1 cónyuge
                if (parentescoEnum == Enums.TipoParentesco.CONYUGE) {

                    long totalConyuge = beneficiariosSaludRepository
                            .countByPolizaSalud_PolizaIdAndTipoParentesco_Id(
                                    polizaSalud.getId(),
                                    Enums.TipoParentesco.CONYUGE.getId()
                            );

                    if (totalConyuge >= 1) {
                        throw new ValidationException(
                                "Solo se permite un cónyuge en esta cobertura"
                        );
                    }
                }

                break;

            default:
                throw new ValidationException("Cobertura no soportada");
        }

        // 5️⃣ Crear entidad
        BeneficiariosSalud beneficiario = new BeneficiariosSalud();
        beneficiario.setPolizaSalud(polizaSalud);
        beneficiario.setTipoParentesco(tipoParentesco);
        beneficiario.setNombreCompleto(parametros.getNombreCompleto());
        beneficiario.setNumeroDocumento(parametros.getNumeroDocumento());
        beneficiario.setMontoAdicional(
                parametros.getMontoAdicional() != null
                        ? parametros.getMontoAdicional()
                        : BigDecimal.ZERO
        );

        return beneficiariosSaludRepository.save(beneficiario);
    }

    /** {@inheritDoc} */
    @Override
    public List<PolizasSalud> consultarPolizaSalud(
            ParametrosConsultarPolizaSalud parametros) {

        Specification<PolizasSalud> spec = Specification.allOf();

        if (parametros.getId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("id"), parametros.getId()));
        }

        if (parametros.getPolizaId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("poliza").get("id"),
                            parametros.getPolizaId()));
        }

        if (parametros.getTipoCoberturaId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("tipoCobertura").get("id"),
                            parametros.getTipoCoberturaId()));
        }

        List<PolizasSalud> resultado = polizasSaludRepository.findAll(spec);

        if (resultado.isEmpty()) {
            throw new ValidationException("No se encontraron pólizas de salud");
        }

        return resultado;
    }

    /** {@inheritDoc} */
    @Override
    public List<BeneficiariosSalud> consultarBeneficiariosSalud(Long polizaSaludId) {

        if (polizaSaludId == null || polizaSaludId <= 0)
            throw new ValidationException("polizaSaludId es requerido y debe ser mayor a 0");

        // Validar que exista la póliza salud
        if (!polizasSaludRepository.existsById(polizaSaludId)) {
            throw new ValidationException("Póliza de salud no encontrada");
        }

        // Buscar por polizaSalud.id (coincide con el parámetro que recibe el servicio)
        return beneficiariosSaludRepository
                .findByPolizaSalud_Id(polizaSaludId);
    }

    // VALIDACIONES

    /**
     * Valida los parámetros de creación de una póliza de salud.
     *
     * @param parametros parámetros a validar
     * @throws jakarta.validation.ValidationException si algún campo obligatorio falta o es inválido
     */
    private void validarParametrosCrearPolizaSalud(ParametrosCrearPolizaSalud parametros) {

        if (parametros == null)
            throw new ValidationException("Parámetros requeridos");

        if (parametros.getPolizaId() == null || parametros.getPolizaId() <= 0)
            throw new ValidationException("polizaId es requerido y debe ser mayor a 0");

        if (parametros.getTipoCoberturaId() == null || parametros.getTipoCoberturaId() <= 0)
            throw new ValidationException("tipoCoberturaId es requerido y debe ser mayor a 0");
    }

    /**
     * Valida los parámetros de creación de un beneficiario de salud.
     *
     * @param parametros parámetros a validar
     * @throws jakarta.validation.ValidationException si algún campo obligatorio falta o es inválido
     */
    private void validarParametrosCrearBeneficiarioSalud(ParametrosCrearBeneficiarioSalud parametros) {

        if (parametros == null)
            throw new ValidationException("Parámetros requeridos");

        if (parametros.getPolizaSaludId() == null || parametros.getPolizaSaludId() <= 0)
            throw new ValidationException("polizaSaludId es requerido y debe ser mayor a 0");

        if (parametros.getTipoParentescoId() == null || parametros.getTipoParentescoId() <= 0)
            throw new ValidationException("tipoParentescoId es requerido y debe ser mayor a 0");

        if (parametros.getNombreCompleto() == null || parametros.getNombreCompleto().isBlank())
            throw new ValidationException("nombreCompleto es requerido");

        if (parametros.getMontoAdicional() != null
                && parametros.getMontoAdicional().compareTo(BigDecimal.ZERO) < 0)
            throw new ValidationException("montoAdicional no puede ser negativo");
    }
}
