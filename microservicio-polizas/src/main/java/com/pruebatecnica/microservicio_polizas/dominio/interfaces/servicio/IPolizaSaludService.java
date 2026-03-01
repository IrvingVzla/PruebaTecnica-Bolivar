package com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.BeneficiariosSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.PolizasSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosConsultarPolizaSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosCrearBeneficiarioSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosCrearPolizaSalud;

import java.util.List;

/**
 * Contrato de servicio para la gestión de pólizas de salud y sus beneficiarios.
 * <p>
 * Cada operación de creación valida las reglas de negocio específicas de la
 * cobertura de salud seleccionada antes de persistir los datos.
 * </p>
 */
public interface IPolizaSaludService {

    /**
     * Crea la especialización de salud sobre una póliza base de tipo SALUD.
     *
     * @param parametros datos de la póliza de salud a crear
     * @return la póliza de salud creada
     * @throws jakarta.validation.ValidationException si los parámetros no son válidos o la póliza base no es de tipo SALUD
     */
    PolizasSalud crearPolizaSalud(ParametrosCrearPolizaSalud parametros);

    /**
     * Registra un nuevo beneficiario en una póliza de salud existente.
     * <p>
     * Valida la cobertura vigente y las restricciones de parentesco antes de persistir.
     * </p>
     *
     * @param parametros datos del beneficiario a registrar
     * @return el beneficiario de salud creado
     * @throws jakarta.validation.ValidationException si los datos no cumplen las reglas de cobertura
     */
    BeneficiariosSalud crearBeneficiarioSalud(ParametrosCrearBeneficiarioSalud parametros);

    /**
     * Consulta pólizas de salud según los filtros indicados.
     *
     * @param parametrosConsultarPolizaSalud criterios de filtrado
     * @return lista de pólizas de salud encontradas
     * @throws jakarta.validation.ValidationException si no se encuentran resultados
     */
    List<PolizasSalud> consultarPolizaSalud(ParametrosConsultarPolizaSalud parametrosConsultarPolizaSalud);

    /**
     * Obtiene todos los beneficiarios asociados a una póliza de salud.
     *
     * @param polizaSaludId identificador de la póliza de salud
     * @return lista de beneficiarios de la póliza de salud
     * @throws jakarta.validation.ValidationException si la póliza de salud no existe
     */
    List<BeneficiariosSalud> consultarBeneficiariosSalud(Long polizaSaludId);
}
