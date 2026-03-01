package com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Poliza;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaCompletoDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base.ParametrosConsultarPolizasBase;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base.ParametrosCrearPolizaBase;

import java.util.List;

/**
 * Contrato de servicio para la gestión de pólizas base.
 * <p>
 * Define las operaciones de consulta y creación sobre la entidad {@link Poliza},
 * así como la consulta enriquecida que incluye las especializaciones de cada póliza.
 * </p>
 */
public interface IPolizaBaseService {

    /**
     * Consulta pólizas base aplicando los filtros indicados.
     *
     * @param parametros criterios de filtrado; puede ser {@code null} para obtener todas
     * @return lista de pólizas que satisfacen los criterios
     */
    List<Poliza> consultarPolizasBase(ParametrosConsultarPolizasBase parametros);

    /**
     * Crea una nueva póliza base con los datos suministrados.
     * <p>
     * Valida que el cliente exista, que el tipo de póliza sea válido y que,
     * en el caso de pólizas de vida, no exista ya una póliza activa en el
     * mismo rango de fechas para ese cliente.
     * </p>
     *
     * @param parametros datos de la póliza a crear
     * @return la póliza creada y persistida
     * @throws jakarta.validation.ValidationException si los datos no son válidos
     */
    Poliza crearPolizaBase(ParametrosCrearPolizaBase parametros);

    /**
     * Consulta las pólizas de un cliente incluyendo los detalles de sus especializaciones.
     *
     * @param parametros criterios de filtrado, generalmente el {@code clienteId}
     * @return lista de DTOs completos con la información base y sus especializaciones
     */
    List<PolizaCompletoDTO> consultarPolizasCliente(ParametrosConsultarPolizasBase parametros);
}
