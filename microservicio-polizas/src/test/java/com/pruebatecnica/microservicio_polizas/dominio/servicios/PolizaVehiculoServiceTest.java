package com.pruebatecnica.microservicio_polizas.dominio.servicios;

import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.PolizaRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.PolizasVehiculoRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.VehiculoRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosConsultarPolizaVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosCrearPolizaVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosCrearVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.enums.Enums;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link PolizaVehiculoService}.
 */
@ExtendWith(MockitoExtension.class)
class PolizaVehiculoServiceTest {

    @Mock private PolizasVehiculoRepository polizasVehiculoRepository;
    @Mock private PolizaRepository polizaRepository;
    @Mock private VehiculoRepository vehiculoRepository;

    @InjectMocks private PolizaVehiculoService service;

    // ── fixtures ─────────────────────────────────────────────────────────────

    private TiposPoliza tipoPolizaVehiculo() {
        TiposPoliza tp = new TiposPoliza();
        tp.setId(Enums.TipoPoliza.VEHICULO.getValor());
        tp.setCodigo("VEHICULO");
        return tp;
    }

    private TiposPoliza tipoPolizaVida() {
        TiposPoliza tp = new TiposPoliza();
        tp.setId(Enums.TipoPoliza.VIDA.getValor());
        tp.setCodigo("VIDA");
        return tp;
    }

    private Poliza polizaActivaVehiculo() {
        Poliza p = new Poliza();
        p.setTipoPoliza(tipoPolizaVehiculo());
        p.setEstado(true);
        return p;
    }

    private Poliza polizaInactivaVehiculo() {
        Poliza p = new Poliza();
        p.setTipoPoliza(tipoPolizaVehiculo());
        p.setEstado(false);
        return p;
    }

    private PolizasVehiculo polizaVehiculoBase() {
        PolizasVehiculo pv = new PolizasVehiculo();
        pv.setPoliza(polizaActivaVehiculo());
        return pv;
    }

    private PolizasVehiculo polizaVehiculoInactiva() {
        PolizasVehiculo pv = new PolizasVehiculo();
        pv.setPoliza(polizaInactivaVehiculo());
        return pv;
    }

    private ParametrosCrearPolizaVehiculo paramsCrearPolizaVehiculo() {
        ParametrosCrearPolizaVehiculo p = new ParametrosCrearPolizaVehiculo();
        p.setPolizaId(1L);
        return p;
    }

    private ParametrosCrearVehiculo paramsCrearVehiculo() {
        ParametrosCrearVehiculo p = new ParametrosCrearVehiculo();
        p.setPolizaVehiculoId(1L);
        p.setPlaca("abc123");
        p.setMarca("Toyota");
        p.setModelo("Corolla");
        p.setAnio(2023);
        p.setValorAsegurado(BigDecimal.valueOf(50000));
        return p;
    }

    // ── crearPolizaVehiculo ──────────────────────────────────────────────────

    @Nested
    @DisplayName("crearPolizaVehiculo")
    @ExtendWith(MockitoExtension.class)
    class CrearPolizaVehiculo {

        @Test
        @DisplayName("crea póliza de vehículo exitosamente")
        void creaExitosamente() {
            when(polizaRepository.findById(1L)).thenReturn(Optional.of(polizaActivaVehiculo()));
            PolizasVehiculo guardada = polizaVehiculoBase();
            when(polizasVehiculoRepository.save(any())).thenReturn(guardada);

            PolizasVehiculo resultado = service.crearPolizaVehiculo(paramsCrearPolizaVehiculo());
            assertThat(resultado).isNotNull();
            verify(polizasVehiculoRepository).save(any(PolizasVehiculo.class));
        }

        @Test
        @DisplayName("lanza ValidationException cuando parámetros son null")
        void lanzaExcepcionParametrosNull() {
            assertThatThrownBy(() -> service.crearPolizaVehiculo(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("requeridos");
        }

        @Test
        @DisplayName("lanza ValidationException cuando polizaId es null")
        void lanzaExcepcionPolizaIdNull() {
            ParametrosCrearPolizaVehiculo p = paramsCrearPolizaVehiculo();
            p.setPolizaId(null);
            assertThatThrownBy(() -> service.crearPolizaVehiculo(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("polizaId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando polizaId es 0")
        void lanzaExcepcionPolizaIdCero() {
            ParametrosCrearPolizaVehiculo p = paramsCrearPolizaVehiculo();
            p.setPolizaId(0L);
            assertThatThrownBy(() -> service.crearPolizaVehiculo(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("polizaId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza base no existe")
        void lanzaExcepcionPolizaNoEncontrada() {
            when(polizaRepository.findById(1L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearPolizaVehiculo(paramsCrearPolizaVehiculo()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Póliza base no encontrada");
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza no es tipo VEHICULO")
        void lanzaExcepcionNoEsVehiculo() {
            Poliza p = new Poliza();
            p.setTipoPoliza(tipoPolizaVida());
            p.setEstado(true);
            when(polizaRepository.findById(1L)).thenReturn(Optional.of(p));
            assertThatThrownBy(() -> service.crearPolizaVehiculo(paramsCrearPolizaVehiculo()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tipo VEHICULO");
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza base no está activa")
        void lanzaExcepcionPolizaInactiva() {
            when(polizaRepository.findById(1L)).thenReturn(Optional.of(polizaInactivaVehiculo()));
            assertThatThrownBy(() -> service.crearPolizaVehiculo(paramsCrearPolizaVehiculo()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("activa");
        }
    }

    // ── crearVehiculo ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("crearVehiculo")
    @ExtendWith(MockitoExtension.class)
    class CrearVehiculo {

        @Test
        @DisplayName("crea vehículo exitosamente")
        void creaExitosamente() {
            when(polizasVehiculoRepository.findById(1L)).thenReturn(Optional.of(polizaVehiculoBase()));
            when(vehiculoRepository.existsByPolizasVehiculo_IdAndPlaca(1L, "ABC123")).thenReturn(false);
            Vehiculo guardado = new Vehiculo();
            guardado.setPlaca("ABC123");
            when(vehiculoRepository.save(any())).thenReturn(guardado);

            Vehiculo resultado = service.crearVehiculo(paramsCrearVehiculo());
            assertThat(resultado).isNotNull();
            assertThat(resultado.getPlaca()).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("crea vehículo exitosamente sin valorAsegurado (se usa ZERO)")
        void creaExitosamenteSinValorAsegurado() {
            ParametrosCrearVehiculo params = paramsCrearVehiculo();
            params.setValorAsegurado(null);
            when(polizasVehiculoRepository.findById(1L)).thenReturn(Optional.of(polizaVehiculoBase()));
            when(vehiculoRepository.existsByPolizasVehiculo_IdAndPlaca(1L, "ABC123")).thenReturn(false);
            Vehiculo guardado = new Vehiculo();
            guardado.setPlaca("ABC123");
            when(vehiculoRepository.save(any())).thenReturn(guardado);

            Vehiculo resultado = service.crearVehiculo(params);
            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("lanza ValidationException cuando parámetros son null")
        void lanzaExcepcionParametrosNull() {
            assertThatThrownBy(() -> service.crearVehiculo(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("requeridos");
        }

        @Test
        @DisplayName("lanza ValidationException cuando polizaVehiculoId es null")
        void lanzaExcepcionPolizaVehiculoIdNull() {
            ParametrosCrearVehiculo params = paramsCrearVehiculo();
            params.setPolizaVehiculoId(null);
            assertThatThrownBy(() -> service.crearVehiculo(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("polizaVehiculoId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando polizaVehiculoId es 0")
        void lanzaExcepcionPolizaVehiculoIdCero() {
            ParametrosCrearVehiculo params = paramsCrearVehiculo();
            params.setPolizaVehiculoId(0L);
            assertThatThrownBy(() -> service.crearVehiculo(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("polizaVehiculoId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando placa es null")
        void lanzaExcepcionPlacaNull() {
            ParametrosCrearVehiculo params = paramsCrearVehiculo();
            params.setPlaca(null);
            assertThatThrownBy(() -> service.crearVehiculo(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("placa");
        }

        @Test
        @DisplayName("lanza ValidationException cuando placa está en blanco")
        void lanzaExcepcionPlacaBlanco() {
            ParametrosCrearVehiculo params = paramsCrearVehiculo();
            params.setPlaca("  ");
            assertThatThrownBy(() -> service.crearVehiculo(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("placa");
        }

        @Test
        @DisplayName("lanza ValidationException cuando año es menor a 1900")
        void lanzaExcepcionAnioMenor1900() {
            ParametrosCrearVehiculo params = paramsCrearVehiculo();
            params.setAnio(1800);
            assertThatThrownBy(() -> service.crearVehiculo(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("año inválido");
        }

        @Test
        @DisplayName("lanza ValidationException cuando año es mayor al próximo año")
        void lanzaExcepcionAnioFuturo() {
            ParametrosCrearVehiculo params = paramsCrearVehiculo();
            params.setAnio(java.time.Year.now().getValue() + 2);
            assertThatThrownBy(() -> service.crearVehiculo(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("año inválido");
        }

        @Test
        @DisplayName("lanza ValidationException cuando valorAsegurado es negativo")
        void lanzaExcepcionValorAseguradoNegativo() {
            ParametrosCrearVehiculo params = paramsCrearVehiculo();
            params.setValorAsegurado(BigDecimal.valueOf(-1));
            assertThatThrownBy(() -> service.crearVehiculo(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("valorAsegurado");
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza de vehículo no existe")
        void lanzaExcepcionPolizaVehiculoNoEncontrada() {
            when(polizasVehiculoRepository.findById(1L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearVehiculo(paramsCrearVehiculo()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Póliza de vehículo no encontrada");
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza base no está activa")
        void lanzaExcepcionPolizaBaseInactiva() {
            when(polizasVehiculoRepository.findById(1L)).thenReturn(Optional.of(polizaVehiculoInactiva()));
            assertThatThrownBy(() -> service.crearVehiculo(paramsCrearVehiculo()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("activa");
        }

        @Test
        @DisplayName("lanza ValidationException cuando placa duplicada en la misma póliza")
        void lanzaExcepcionPlacaDuplicada() {
            when(polizasVehiculoRepository.findById(1L)).thenReturn(Optional.of(polizaVehiculoBase()));
            when(vehiculoRepository.existsByPolizasVehiculo_IdAndPlaca(1L, "ABC123")).thenReturn(true);
            assertThatThrownBy(() -> service.crearVehiculo(paramsCrearVehiculo()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("placa");
        }

        @Test
        @DisplayName("crea vehículo sin año (año null no lanza excepción)")
        void creaExitosamenteSinAnio() {
            ParametrosCrearVehiculo params = paramsCrearVehiculo();
            params.setAnio(null);
            when(polizasVehiculoRepository.findById(1L)).thenReturn(Optional.of(polizaVehiculoBase()));
            when(vehiculoRepository.existsByPolizasVehiculo_IdAndPlaca(1L, "ABC123")).thenReturn(false);
            Vehiculo guardado = new Vehiculo();
            guardado.setPlaca("ABC123");
            when(vehiculoRepository.save(any())).thenReturn(guardado);

            Vehiculo resultado = service.crearVehiculo(params);
            assertThat(resultado).isNotNull();
        }
    }

    // ── consultarPolizasVehiculo ──────────────────────────────────────────────

    @Nested
    @DisplayName("consultarPolizasVehiculo")
    @ExtendWith(MockitoExtension.class)
    class ConsultarPolizasVehiculo {

        @Test
        @DisplayName("retorna lista de pólizas de vehículo")
        void retornaLista() {
            when(polizasVehiculoRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(polizaVehiculoBase()));
            ParametrosConsultarPolizaVehiculo p = new ParametrosConsultarPolizaVehiculo();
            p.setPolizaId(1L);
            assertThat(service.consultarPolizasVehiculo(p)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por id")
        void filtraPorId() {
            ParametrosConsultarPolizaVehiculo p = new ParametrosConsultarPolizaVehiculo();
            p.setId(1L);
            when(polizasVehiculoRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(polizaVehiculoBase()));
            assertThat(service.consultarPolizasVehiculo(p)).hasSize(1);
        }

        @Test
        @DisplayName("lanza ValidationException cuando no hay resultados")
        void lanzaExcepcionSinResultados() {
            when(polizasVehiculoRepository.findAll(any(Specification.class))).thenReturn(List.of());
            assertThatThrownBy(() -> service.consultarPolizasVehiculo(new ParametrosConsultarPolizaVehiculo()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("No se encontraron");
        }
    }

    // ── consultarVehiculos ───────────────────────────────────────────────────

    @Nested
    @DisplayName("consultarVehiculos")
    @ExtendWith(MockitoExtension.class)
    class ConsultarVehiculos {

        @Test
        @DisplayName("retorna lista de vehículos")
        void retornaLista() {
            when(polizasVehiculoRepository.existsById(1L)).thenReturn(true);
            Vehiculo v = new Vehiculo();
            v.setPlaca("ABC123");
            when(vehiculoRepository.findByPolizasVehiculo_Id(1L)).thenReturn(List.of(v));
            assertThat(service.consultarVehiculos(1L)).hasSize(1);
        }

        @Test
        @DisplayName("lanza ValidationException cuando id es null")
        void lanzaExcepcionIdNull() {
            assertThatThrownBy(() -> service.consultarVehiculos(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("polizaVehiculoId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando id es 0")
        void lanzaExcepcionIdCero() {
            assertThatThrownBy(() -> service.consultarVehiculos(0L))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("polizaVehiculoId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando id es negativo")
        void lanzaExcepcionIdNegativo() {
            assertThatThrownBy(() -> service.consultarVehiculos(-1L))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("polizaVehiculoId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza de vehículo no existe")
        void lanzaExcepcionPolizaNoExiste() {
            when(polizasVehiculoRepository.existsById(99L)).thenReturn(false);
            assertThatThrownBy(() -> service.consultarVehiculos(99L))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Póliza de vehículo no encontrada");
        }
    }
}

