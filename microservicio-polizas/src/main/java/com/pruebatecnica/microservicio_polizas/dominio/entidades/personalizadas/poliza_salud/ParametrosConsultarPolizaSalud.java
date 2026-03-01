package com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud;

import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Poliza;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposCoberturaSalud;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * Parámetros de filtro para consultar pólizas de salud.
 * <p>
 * Todos los campos son opcionales y se combinan como condiciones AND
 * para filtrar los resultados.
 * </p>
 */
@Data
public class ParametrosConsultarPolizaSalud {
    /** Filtro por identificador de la póliza de salud. */
    private Long id;
    /** Filtro por identificador de la póliza base asociada. */
    private Long polizaId;
    /** Filtro por identificador del tipo de cobertura de salud. */
    private Long tipoCoberturaId;
}
