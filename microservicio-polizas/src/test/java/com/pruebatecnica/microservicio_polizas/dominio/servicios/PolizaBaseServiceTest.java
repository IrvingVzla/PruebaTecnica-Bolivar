package com.pruebatecnica.microservicio_polizas.dominio.servicios;

import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaCompletoDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base.ParametrosConsultarPolizasBase;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base.ParametrosCrearPolizaBase;
import com.pruebatecnica.microservicio_polizas.dominio.enums.Enums;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link PolizaBaseService}.
 */
@ExtendWith(MockitoExtension.class)
class PolizaBaseServiceTest {

    @Mock private PolizaRepository polizaRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private TiposPolizaRepository tiposPolizaRepository;
    @Mock private PolizasSaludRepository polizasSaludRepository;
    @Mock private PolizasVidaRepository polizasVidaRepository;
    @Mock private PolizasVehiculoRepository polizasVehiculoRepository;

    @InjectMocks private PolizaBaseService service;

    // ── fixtures ─────────────────────────────────────────────────────────────

    private TiposPoliza tipoPolizaVida() {
        TiposPoliza tp = new TiposPoliza();
        tp.setId(Enums.TipoPoliza.VIDA.getValor());
        tp.setCodigo("VIDA");
        return tp;
    }

    private TiposPoliza tipoPolizaSalud() {
        TiposPoliza tp = new TiposPoliza();
        tp.setId(Enums.TipoPoliza.SALUD.getValor());
        tp.setCodigo("SALUD");
        return tp;
    }

    private TiposPoliza tipoPolizaVehiculo() {
        TiposPoliza tp = new TiposPoliza();
        tp.setId(Enums.TipoPoliza.VEHICULO.getValor());
        tp.setCodigo("VEHICULO");
        return tp;
    }

    private Poliza polizaBase(TiposPoliza tipo) {
        Poliza p = new Poliza();
        p.setClienteId(1);
        p.setTipoPoliza(tipo);
        p.setFechaInicio(LocalDate.of(2025, 1, 1));
        p.setFechaFin(LocalDate.of(2026, 1, 1));
        p.setEstado(true);
        p.setNumeroPoliza("P-123");
        return p;
    }

    private Cliente clienteBase() {
        Cliente c = new Cliente();
        c.setNombres("Test");
        return c;
    }

    private ParametrosCrearPolizaBase paramsCrear() {
        ParametrosCrearPolizaBase p = new ParametrosCrearPolizaBase();
        p.setClienteId(1L);
        p.setTipoPolizaId(Enums.TipoPoliza.SALUD.getValor());
        p.setFechaInicio(LocalDate.of(2025, 1, 1));
        p.setFechaFin(LocalDate.of(2026, 1, 1));
        return p;
    }

    // ── consultarPolizasBase ──────────────────────────────────────────────────

    @Nested
    @DisplayName("consultarPolizasBase")
    @ExtendWith(MockitoExtension.class)
    class ConsultarPolizasBase {

        @Test
        @DisplayName("retorna todas las pólizas cuando parámetros es null")
        void retornaTodasConParametrosNull() {
            Poliza p = polizaBase(tipoPolizaSalud());
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(p));
            List<Poliza> resultado = service.consultarPolizasBase(null);
            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("retorna lista vacía cuando no hay coincidencias")
        void retornaListaVacia() {
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of());
            List<Poliza> resultado = service.consultarPolizasBase(new ParametrosConsultarPolizasBase());
            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("filtra por clienteId")
        void filtraPorClienteId() {
            ParametrosConsultarPolizasBase params = new ParametrosConsultarPolizasBase();
            params.setClienteId(1L);
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(polizaBase(tipoPolizaSalud())));
            assertThat(service.consultarPolizasBase(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por tipoPolizaId")
        void filtraPorTipoPolizaId() {
            ParametrosConsultarPolizasBase params = new ParametrosConsultarPolizasBase();
            params.setTipoPolizaId(Enums.TipoPoliza.VIDA.getValor());
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(polizaBase(tipoPolizaVida())));
            assertThat(service.consultarPolizasBase(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por estado activo")
        void filtraPorActivo() {
            ParametrosConsultarPolizasBase params = new ParametrosConsultarPolizasBase();
            params.setActivo(true);
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(polizaBase(tipoPolizaSalud())));
            assertThat(service.consultarPolizasBase(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por fechaInicioDesde")
        void filtraPorFechaInicioDesde() {
            ParametrosConsultarPolizasBase params = new ParametrosConsultarPolizasBase();
            params.setFechaInicioDesde(LocalDate.of(2024, 1, 1));
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(polizaBase(tipoPolizaSalud())));
            assertThat(service.consultarPolizasBase(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por fechaInicioHasta")
        void filtraPorFechaInicioHasta() {
            ParametrosConsultarPolizasBase params = new ParametrosConsultarPolizasBase();
            params.setFechaInicioHasta(LocalDate.of(2026, 12, 31));
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(polizaBase(tipoPolizaSalud())));
            assertThat(service.consultarPolizasBase(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por todos los parámetros combinados")
        void filtraPorTodosLosParametros() {
            ParametrosConsultarPolizasBase params = new ParametrosConsultarPolizasBase();
            params.setClienteId(1L);
            params.setTipoPolizaId(Enums.TipoPoliza.SALUD.getValor());
            params.setActivo(true);
            params.setFechaInicioDesde(LocalDate.of(2024, 1, 1));
            params.setFechaInicioHasta(LocalDate.of(2026, 12, 31));
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(polizaBase(tipoPolizaSalud())));
            assertThat(service.consultarPolizasBase(params)).hasSize(1);
        }
    }

    // ── crearPolizaBase ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("crearPolizaBase")
    @ExtendWith(MockitoExtension.class)
    class CrearPolizaBase {

        @Test
        @DisplayName("crea póliza base de tipo SALUD exitosamente")
        void creaExitosamenteSalud() {
            ParametrosCrearPolizaBase params = paramsCrear();
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteBase()));
            when(tiposPolizaRepository.findById(Enums.TipoPoliza.SALUD.getValor())).thenReturn(Optional.of(tipoPolizaSalud()));
            when(polizaRepository.existsByNumeroPoliza(anyString())).thenReturn(false);
            Poliza guardada = polizaBase(tipoPolizaSalud());
            when(polizaRepository.save(any(Poliza.class))).thenReturn(guardada);

            Poliza resultado = service.crearPolizaBase(params);
            assertThat(resultado).isNotNull();
            verify(polizaRepository).save(any(Poliza.class));
        }

        @Test
        @DisplayName("crea póliza base de tipo VIDA exitosamente cuando no hay solapamiento")
        void creaExitosamenteVida() {
            ParametrosCrearPolizaBase params = paramsCrear();
            params.setTipoPolizaId(Enums.TipoPoliza.VIDA.getValor());
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteBase()));
            when(tiposPolizaRepository.findById(Enums.TipoPoliza.VIDA.getValor())).thenReturn(Optional.of(tipoPolizaVida()));
            when(polizaRepository.existePolizaUnicaXCliente(anyLong(), anyInt(), any(), any())).thenReturn(false);
            when(polizaRepository.existsByNumeroPoliza(anyString())).thenReturn(false);
            Poliza guardada = polizaBase(tipoPolizaVida());
            when(polizaRepository.save(any(Poliza.class))).thenReturn(guardada);

            Poliza resultado = service.crearPolizaBase(params);
            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza de vida ya existe en rango de fechas")
        void lanzaExcepcionVidaSolapada() {
            ParametrosCrearPolizaBase params = paramsCrear();
            params.setTipoPolizaId(Enums.TipoPoliza.VIDA.getValor());
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteBase()));
            when(tiposPolizaRepository.findById(Enums.TipoPoliza.VIDA.getValor())).thenReturn(Optional.of(tipoPolizaVida()));
            when(polizaRepository.existePolizaUnicaXCliente(anyLong(), anyInt(), any(), any())).thenReturn(true);

            assertThatThrownBy(() -> service.crearPolizaBase(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("póliza de vida activa");
        }

        @Test
        @DisplayName("lanza ValidationException cuando parámetros son null")
        void lanzaExcepcionParametrosNull() {
            assertThatThrownBy(() -> service.crearPolizaBase(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("requeridos");
        }

        @Test
        @DisplayName("lanza ValidationException cuando clienteId es null")
        void lanzaExcepcionClienteIdNull() {
            ParametrosCrearPolizaBase params = paramsCrear();
            params.setClienteId(null);
            assertThatThrownBy(() -> service.crearPolizaBase(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("clienteId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando clienteId es 0")
        void lanzaExcepcionClienteIdCero() {
            ParametrosCrearPolizaBase params = paramsCrear();
            params.setClienteId(0L);
            assertThatThrownBy(() -> service.crearPolizaBase(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("clienteId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando tipoPolizaId es null")
        void lanzaExcepcionTipoPolizaIdNull() {
            ParametrosCrearPolizaBase params = paramsCrear();
            params.setTipoPolizaId(null);
            assertThatThrownBy(() -> service.crearPolizaBase(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tipoPolizaId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando tipoPolizaId es 0")
        void lanzaExcepcionTipoPolizaIdCero() {
            ParametrosCrearPolizaBase params = paramsCrear();
            params.setTipoPolizaId(0);
            assertThatThrownBy(() -> service.crearPolizaBase(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tipoPolizaId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando fechaInicio es null")
        void lanzaExcepcionFechaInicioNull() {
            ParametrosCrearPolizaBase params = paramsCrear();
            params.setFechaInicio(null);
            assertThatThrownBy(() -> service.crearPolizaBase(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("fechaInicio");
        }

        @Test
        @DisplayName("lanza ValidationException cuando fechaFin es null")
        void lanzaExcepcionFechaFinNull() {
            ParametrosCrearPolizaBase params = paramsCrear();
            params.setFechaFin(null);
            assertThatThrownBy(() -> service.crearPolizaBase(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("fechaFin");
        }

        @Test
        @DisplayName("lanza ValidationException cuando fechaInicio es posterior a fechaFin")
        void lanzaExcepcionFechasInvertidas() {
            ParametrosCrearPolizaBase params = paramsCrear();
            params.setFechaInicio(LocalDate.of(2027, 1, 1));
            params.setFechaFin(LocalDate.of(2026, 1, 1));
            assertThatThrownBy(() -> service.crearPolizaBase(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("fechaInicio");
        }

        @Test
        @DisplayName("lanza RuntimeException cuando cliente no encontrado")
        void lanzaExcepcionClienteNoEncontrado() {
            ParametrosCrearPolizaBase params = paramsCrear();
            when(clienteRepository.findById(1L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearPolizaBase(params))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Cliente no encontrado");
        }

        @Test
        @DisplayName("lanza RuntimeException cuando tipo de poliza no encontrado")
        void lanzaExcepcionTipoPolizaNoEncontrado() {
            ParametrosCrearPolizaBase params = paramsCrear();
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteBase()));
            when(tiposPolizaRepository.findById(Enums.TipoPoliza.SALUD.getValor())).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearPolizaBase(params))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Tipo de poliza no encontrado");
        }

        @Test
        @DisplayName("genera nuevo número de póliza cuando el primero ya existe")
        void reintentoNumeroPoliza() {
            ParametrosCrearPolizaBase params = paramsCrear();
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteBase()));
            when(tiposPolizaRepository.findById(Enums.TipoPoliza.SALUD.getValor())).thenReturn(Optional.of(tipoPolizaSalud()));
            // Primer intento duplicado, segundo OK
            when(polizaRepository.existsByNumeroPoliza(anyString())).thenReturn(true, false);
            Poliza guardada = polizaBase(tipoPolizaSalud());
            when(polizaRepository.save(any(Poliza.class))).thenReturn(guardada);

            Poliza resultado = service.crearPolizaBase(params);
            assertThat(resultado).isNotNull();
            verify(polizaRepository, atLeast(2)).existsByNumeroPoliza(anyString());
        }
    }

    // ── consultarPolizasCliente ───────────────────────────────────────────────

    @Nested
    @DisplayName("consultarPolizasCliente")
    @ExtendWith(MockitoExtension.class)
    class ConsultarPolizasCliente {

        @BeforeEach
        void setUp() {
            // Inyectar self como mock para evitar invocación al proxy real
            service.setSelf(service);
        }

        @Test
        @DisplayName("retorna pólizas completas con especialización VIDA")
        void retornaPolizasConVida() {
            Poliza p = polizaBase(tipoPolizaVida());
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(p));
            PolizasVida pv = new PolizasVida();
            when(polizasVidaRepository.findByPoliza_Id(any())).thenReturn(Optional.of(pv));

            List<PolizaCompletoDTO> resultado = service.consultarPolizasCliente(new ParametrosConsultarPolizasBase());
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getPolizaVida()).isNotNull();
        }

        @Test
        @DisplayName("retorna pólizas completas con especialización SALUD")
        void retornaPolizasConSalud() {
            Poliza p = polizaBase(tipoPolizaSalud());
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(p));
            when(polizasSaludRepository.findAllByPoliza_Id(any())).thenReturn(List.of(new PolizasSalud()));

            List<PolizaCompletoDTO> resultado = service.consultarPolizasCliente(new ParametrosConsultarPolizasBase());
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getPolizasSalud()).isNotNull();
        }

        @Test
        @DisplayName("retorna pólizas completas con especialización VEHICULO")
        void retornaPolizasConVehiculo() {
            Poliza p = polizaBase(tipoPolizaVehiculo());
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(p));
            when(polizasVehiculoRepository.findAllByPoliza_Id(any())).thenReturn(List.of(new PolizasVehiculo()));

            List<PolizaCompletoDTO> resultado = service.consultarPolizasCliente(new ParametrosConsultarPolizasBase());
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getPolizasVehiculo()).isNotNull();
        }

        @Test
        @DisplayName("retorna póliza sin especialización cuando tipoPoliza es null")
        void retornaPolizaSinTipoPoliza() {
            Poliza p = new Poliza();
            p.setNumeroPoliza("P-999");
            p.setEstado(true);
            p.setTipoPoliza(null);
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(p));

            List<PolizaCompletoDTO> resultado = service.consultarPolizasCliente(new ParametrosConsultarPolizasBase());
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getPolizaVida()).isNull();
            assertThat(resultado.get(0).getPolizasSalud()).isNull();
            assertThat(resultado.get(0).getPolizasVehiculo()).isNull();
        }

        @Test
        @DisplayName("retorna póliza sin especialización cuando tipoPolizaId es inválido")
        void retornaPolizaConTipoPolizaInvalido() {
            TiposPoliza tpInvalido = new TiposPoliza();
            tpInvalido.setId(999);
            tpInvalido.setCodigo("DESCONOCIDO");
            Poliza p = polizaBase(tpInvalido);
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of(p));

            List<PolizaCompletoDTO> resultado = service.consultarPolizasCliente(new ParametrosConsultarPolizasBase());
            assertThat(resultado).hasSize(1);
            // Se añade al resultado sin especialización, continúa sin lanzar excepción
            assertThat(resultado.get(0).getTipoPolizaId()).isEqualTo(999);
        }

        @Test
        @DisplayName("retorna lista vacía cuando no hay pólizas")
        void retornaListaVacia() {
            when(polizaRepository.findAll(any(Specification.class))).thenReturn(List.of());
            List<PolizaCompletoDTO> resultado = service.consultarPolizasCliente(new ParametrosConsultarPolizasBase());
            assertThat(resultado).isEmpty();
        }
    }
}

