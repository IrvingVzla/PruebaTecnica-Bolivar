package com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.BeneficiariosVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosConsultarPolizaVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosCrearBeneficiarioVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosCrearPolizaVida;

import java.util.List;

/**
 * Contrato de servicio para la gestión de pólizas de vida y sus beneficiarios.
 * <p>
 * Una póliza de vida es única por cliente y rango de fechas. Admite un máximo
 * de dos beneficiarios por póliza.
 * </p>
 */
public interface IPolizaVidaService {

    /**
     * Crea la especialización de vida sobre una póliza base de tipo VIDA.
     *
     * @param parametros datos de la póliza de vida a crear
     * @return la póliza de vida creada
     * @throws jakarta.validation.ValidationException si la póliza base no existe, no es de tipo VIDA, no está activa o ya tiene una especialización de vida
     */
    PolizasVida crearPolizaVida(ParametrosCrearPolizaVida parametros);

    /**
     * Registra un nuevo beneficiario en una póliza de vida existente.
     * <p>
     * Se permiten máximo dos beneficiarios por póliza. El número de documento
     * debe ser único dentro de la misma póliza.
     * </p>
     *
     * @param parametros datos del beneficiario a registrar
     * @return el beneficiario de vida creado
     * @throws jakarta.validation.ValidationException si los datos no son válidos o se supera el límite de beneficiarios
     */
    BeneficiariosVida crearBeneficiarioVida(ParametrosCrearBeneficiarioVida parametros);

    /**
     * Consulta pólizas de vida según los filtros indicados.
     *
     * @param parametros criterios de filtrado
     * @return lista de pólizas de vida encontradas
     * @throws jakarta.validation.ValidationException si no se encuentran resultados
     */
    List<PolizasVida> consultarPolizaVida(ParametrosConsultarPolizaVida parametros);

    /**
     * Obtiene todos los beneficiarios asociados a una póliza de vida.
     *
     * @param polizaVidaId identificador de la póliza de vida
     * @return lista de beneficiarios de la póliza de vida
     * @throws jakarta.validation.ValidationException si la póliza de vida no existe
     */
    List<BeneficiariosVida> consultarBeneficiariosVida(Long polizaVidaId);
}
