package com.pruebatecnica.microservicio_polizas.dominio.servicios;

import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.PolizaRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.PolizasVehiculoRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.VehiculoRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosConsultarPolizaVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosCrearPolizaVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosCrearVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.enums.Enums;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaVehiculoService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación del servicio de gestión de pólizas de vehículo y sus vehículos asegurados.
 * <p>
 * Valida que la póliza base sea de tipo VEHICULO y esté activa antes de crear
 * la especialización. Garantiza que la placa del vehículo sea única dentro de
 * la misma póliza.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PolizaVehiculoService implements IPolizaVehiculoService {
    /** Repositorio de pólizas de vehículo. */
    private final PolizasVehiculoRepository polizasVehiculoRepository;
    /** Repositorio de pólizas base. */
    private final PolizaRepository polizaRepository;
    /** Repositorio de vehículos. */
    private final VehiculoRepository vehiculoRepository;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public PolizasVehiculo crearPolizaVehiculo(ParametrosCrearPolizaVehiculo parametros) {

        validarParametrosCrearPolizaVehiculo(parametros);

        // Obtener póliza base
        Poliza poliza = polizaRepository
                .findById(parametros.getPolizaId())
                .orElseThrow(() -> new ValidationException("Póliza base no encontrada"));

        // Validar que sea tipo VEHICULO
        if (!poliza.getTipoPoliza().getId()
                .equals(Enums.TipoPoliza.VEHICULO.getValor())) {
            throw new ValidationException("La póliza base no es de tipo VEHICULO");
        }

        // Validar que esté activa
        if (!Boolean.TRUE.equals(poliza.getEstado())) {
            throw new ValidationException("La póliza base no está activa");
        }

        // Crear entidad
        PolizasVehiculo polizaVehiculo = new PolizasVehiculo();
        polizaVehiculo.setPoliza(poliza);

        return polizasVehiculoRepository.save(polizaVehiculo);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public Vehiculo crearVehiculo(ParametrosCrearVehiculo parametros) {

        validarParametrosCrearVehiculo(parametros);

        // Obtener póliza vehículo
        PolizasVehiculo polizaVehiculo = polizasVehiculoRepository
                .findById(parametros.getPolizaVehiculoId())
                .orElseThrow(() -> new ValidationException("Póliza de vehículo no encontrada"));

        // Validar que póliza base esté activa
        if (!Boolean.TRUE.equals(polizaVehiculo.getPoliza().getEstado())) {
            throw new ValidationException("La póliza base no está activa");
        }

        // Validar placa duplicada dentro de la misma póliza
        if (vehiculoRepository.existsByPolizasVehiculo_IdAndPlaca(
                parametros.getPolizaVehiculoId(),
                parametros.getPlaca().toUpperCase())) {

            throw new ValidationException("Ya existe un vehículo con esa placa en esta póliza");
        }

        // 4️⃣ Crear entidad
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPolizasVehiculo(polizaVehiculo);
        vehiculo.setPlaca(parametros.getPlaca().toUpperCase());
        vehiculo.setMarca(parametros.getMarca());
        vehiculo.setModelo(parametros.getModelo());
        vehiculo.setAnio(parametros.getAnio());
        vehiculo.setValorAsegurado(
                parametros.getValorAsegurado() != null
                        ? parametros.getValorAsegurado()
                        : BigDecimal.ZERO
        );

        return vehiculoRepository.save(vehiculo);
    }

    /** {@inheritDoc} */
    @Override
    public List<PolizasVehiculo> consultarPolizasVehiculo(ParametrosConsultarPolizaVehiculo parametros) {
        Specification<PolizasVehiculo> spec = Specification.allOf();

        if (parametros.getId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("id"), parametros.getId()));
        }

        if (parametros.getPolizaId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("poliza").get("id"),
                            parametros.getPolizaId()));
        }

        List<PolizasVehiculo> resultado = polizasVehiculoRepository.findAll(spec);

        if (resultado.isEmpty()) {
            throw new ValidationException("No se encontraron pólizas de vehículo");
        }

        return resultado;
    }

    /** {@inheritDoc} */
    @Override
    public List<Vehiculo> consultarVehiculos(Long polizaVehiculoId) {

        if (polizaVehiculoId == null || polizaVehiculoId <= 0)
            throw new ValidationException("polizaVehiculoId es requerido y debe ser mayor a 0");

        if (!polizasVehiculoRepository.existsById(polizaVehiculoId))
            throw new ValidationException("Póliza de vehículo no encontrada");

        return vehiculoRepository.findByPolizasVehiculo_Id(polizaVehiculoId);
    }


    // VALIDACIONES

    /**
     * Valida los parámetros de creación de una póliza de vehículo.
     *
     * @param parametros parámetros a validar
     * @throws jakarta.validation.ValidationException si algún campo obligatorio falta o es inválido
     */
    private void validarParametrosCrearPolizaVehiculo(ParametrosCrearPolizaVehiculo parametros) {

        if (parametros == null)
            throw new ValidationException("Parámetros requeridos");

        if (parametros.getPolizaId() == null || parametros.getPolizaId() <= 0)
            throw new ValidationException("polizaId es requerido y debe ser mayor a 0");
    }

    /**
     * Valida los parámetros de creación de un vehículo asegurado.
     *
     * @param parametros parámetros a validar
     * @throws jakarta.validation.ValidationException si la placa está vacía, el año es inválido
     *                                               o el valor asegurado es negativo
     */
    private void validarParametrosCrearVehiculo(ParametrosCrearVehiculo parametros) {

        if (parametros == null)
            throw new ValidationException("Parámetros requeridos");

        if (parametros.getPolizaVehiculoId() == null || parametros.getPolizaVehiculoId() <= 0)
            throw new ValidationException("polizaVehiculoId es requerido y debe ser mayor a 0");

        if (parametros.getPlaca() == null || parametros.getPlaca().isBlank())
            throw new ValidationException("placa es requerida");

        if (parametros.getAnio() != null) {
            int anioActual = java.time.Year.now().getValue();
            if (parametros.getAnio() < 1900 || parametros.getAnio() > anioActual + 1)
                throw new ValidationException("año inválido");
        }

        if (parametros.getValorAsegurado() != null &&
                parametros.getValorAsegurado().compareTo(BigDecimal.ZERO) < 0)
            throw new ValidationException("valorAsegurado no puede ser negativo");
    }
}

