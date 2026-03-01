package com.pruebatecnica.microservicio_polizas.dominio.servicios;

import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosConsultarPolizaSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosCrearBeneficiarioSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosCrearPolizaSalud;
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
 * Pruebas unitarias para {@link PolizaSaludService}.
 */
@ExtendWith(MockitoExtension.class)
class PolizaSaludServiceTest {

    @Mock private PolizasSaludRepository polizasSaludRepository;
    @Mock private PolizaRepository polizaBaseRepository;
    @Mock private TiposCoberturaSaludRepository tiposCoberturaSaludRepository;
    @Mock private BeneficiariosSaludRepository beneficiariosSaludRepository;
    @Mock private TiposParentescoRepository tiposParentescoRepository;

    @InjectMocks private PolizaSaludService service;

    // ── fixtures ─────────────────────────────────────────────────────────────

    private Poliza polizaSaludActiva() {
        TiposPoliza tp = new TiposPoliza();
        tp.setId(Enums.TipoPoliza.SALUD.getValor());
        tp.setCodigo("SALUD");
        Poliza p = new Poliza();
        p.setTipoPoliza(tp);
        p.setEstado(true);
        return p;
    }

    private TiposCoberturaSalud cobertura(Enums.TipoCoberturaSalud e) {
        TiposCoberturaSalud tc = new TiposCoberturaSalud();
        tc.setId(e.getId());
        tc.setCodigo(e.name());
        return tc;
    }

    private PolizasSalud polizaSaludCon(Enums.TipoCoberturaSalud e) {
        PolizasSalud ps = new PolizasSalud();
        ps.setPoliza(polizaSaludActiva());
        ps.setTipoCobertura(cobertura(e));
        return ps;
    }

    private TiposParentesco parentesco(Enums.TipoParentesco e) {
        TiposParentesco tp = new TiposParentesco();
        tp.setId(e.getId());
        tp.setCodigo(e.name());
        return tp;
    }

    private ParametrosCrearPolizaSalud paramsCrearSalud() {
        ParametrosCrearPolizaSalud p = new ParametrosCrearPolizaSalud();
        p.setPolizaId(1L);
        p.setTipoCoberturaId(1);
        return p;
    }

    private ParametrosCrearBeneficiarioSalud paramsBeneficiario() {
        ParametrosCrearBeneficiarioSalud p = new ParametrosCrearBeneficiarioSalud();
        p.setPolizaSaludId(1L);
        p.setTipoParentescoId(1L);
        p.setNombreCompleto("Carlos López");
        p.setNumeroDocumento("11223344");
        return p;
    }

    // ── crearPolizaSalud ──────────────────────────────────────────────────────

    @Nested @DisplayName("crearPolizaSalud")
    class CrearPolizaSalud {

        @Test @DisplayName("crea póliza de salud exitosamente")
        void creaExitosamente() {
            when(polizaBaseRepository.findPolizaById(1L)).thenReturn(Optional.of(polizaSaludActiva()));
            when(tiposCoberturaSaludRepository.findById(1)).thenReturn(Optional.of(cobertura(Enums.TipoCoberturaSalud.SOLO_CLIENTE)));
            PolizasSalud guardada = polizaSaludCon(Enums.TipoCoberturaSalud.SOLO_CLIENTE);
            when(polizasSaludRepository.save(any())).thenReturn(guardada);

            assertThat(service.crearPolizaSalud(paramsCrearSalud())).isNotNull();
            verify(polizasSaludRepository).save(any(PolizasSalud.class));
        }

        @Test @DisplayName("lanza ValidationException cuando parámetros son null")
        void lanzaExcepcionParametrosNull() {
            assertThatThrownBy(() -> service.crearPolizaSalud(null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test @DisplayName("lanza ValidationException cuando polizaId es null")
        void lanzaExcepcionPolizaIdNull() {
            ParametrosCrearPolizaSalud p = paramsCrearSalud();
            p.setPolizaId(null);
            assertThatThrownBy(() -> service.crearPolizaSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("polizaId");
        }

        @Test @DisplayName("lanza ValidationException cuando póliza base no existe")
        void lanzaExcepcionPolizaNoEncontrada() {
            when(polizaBaseRepository.findPolizaById(1L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearPolizaSalud(paramsCrearSalud()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("Póliza base no encontrada");
        }

        @Test @DisplayName("lanza ValidationException cuando póliza no es tipo SALUD")
        void lanzaExcepcionNoEsSalud() {
            TiposPoliza tp = new TiposPoliza();
            tp.setId(Enums.TipoPoliza.VIDA.getValor());
            Poliza p = new Poliza();
            p.setTipoPoliza(tp);
            when(polizaBaseRepository.findPolizaById(1L)).thenReturn(Optional.of(p));
            assertThatThrownBy(() -> service.crearPolizaSalud(paramsCrearSalud()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("tipo SALUD");
        }

        @Test @DisplayName("lanza ValidationException cuando cobertura no existe")
        void lanzaExcepcionCoberturaNoEncontrada() {
            when(polizaBaseRepository.findPolizaById(1L)).thenReturn(Optional.of(polizaSaludActiva()));
            when(tiposCoberturaSaludRepository.findById(1)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearPolizaSalud(paramsCrearSalud()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("cobertura");
        }

        @Test @DisplayName("lanza ValidationException cuando tipoCoberturaId es null")
        void lanzaExcepcionTipoCoberturaIdNull() {
            ParametrosCrearPolizaSalud p = paramsCrearSalud();
            p.setTipoCoberturaId(null);
            assertThatThrownBy(() -> service.crearPolizaSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("tipoCoberturaId");
        }

        @Test @DisplayName("lanza ValidationException cuando tipoCoberturaId es 0")
        void lanzaExcepcionTipoCoberturaIdCero() {
            ParametrosCrearPolizaSalud p = paramsCrearSalud();
            p.setTipoCoberturaId(0);
            assertThatThrownBy(() -> service.crearPolizaSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("tipoCoberturaId");
        }

        @Test @DisplayName("lanza ValidationException cuando polizaId es 0")
        void lanzaExcepcionPolizaIdCero() {
            ParametrosCrearPolizaSalud p = paramsCrearSalud();
            p.setPolizaId(0L);
            assertThatThrownBy(() -> service.crearPolizaSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("polizaId");
        }
    }

    // ── crearBeneficiarioSalud ────────────────────────────────────────────────

    @Nested @DisplayName("crearBeneficiarioSalud")
    class CrearBeneficiarioSalud {

        /** Configura los stubs base (todos lenient para evitar "unnecessary stubbing"). */
        private void stubBase(PolizasSalud ps, boolean docDuplicado, Enums.TipoParentesco par) {
            lenient().when(polizasSaludRepository.findById(1L)).thenReturn(Optional.of(ps));
            lenient().when(beneficiariosSaludRepository
                    .existsByPolizaSalud_IdAndNumeroDocumento(any(), anyString()))
                    .thenReturn(docDuplicado);
            lenient().when(tiposParentescoRepository.findById(1))
                    .thenReturn(Optional.of(parentesco(par)));
            lenient().when(beneficiariosSaludRepository
                    .countByPolizaSalud_IdAndTipoParentesco_Id(any(), any()))
                    .thenReturn(0L);
            lenient().when(beneficiariosSaludRepository
                    .countByPolizaSalud_PolizaIdAndTipoParentesco_Id(any(), any()))
                    .thenReturn(0L);
        }

        @Test @DisplayName("lanza ValidationException con cobertura SOLO_CLIENTE")
        void noPermiteBeneficiarioSoloCliente() {
            stubBase(polizaSaludCon(Enums.TipoCoberturaSalud.SOLO_CLIENTE), false, Enums.TipoParentesco.PADRE);
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(paramsBeneficiario()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("SOLO_CLIENTE");
        }

        @Test @DisplayName("crea beneficiario PADRE con cobertura CLIENTE_PADRES")
        void creaExitosamenteClientePadres() {
            stubBase(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_PADRES), false, Enums.TipoParentesco.PADRE);
            BeneficiariosSalud guardado = new BeneficiariosSalud();
            guardado.setNombreCompleto("Carlos López");
            when(beneficiariosSaludRepository.save(any())).thenReturn(guardado);

            assertThat(service.crearBeneficiarioSalud(paramsBeneficiario())).isNotNull();
        }

        @Test @DisplayName("lanza ValidationException CLIENTE_PADRES con parentesco HIJO")
        void lanzaExcepcionClientePadresParentescoInvalido() {
            stubBase(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_PADRES), false, Enums.TipoParentesco.HIJO);
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(paramsBeneficiario()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("CLIENTE_PADRES");
        }

        @Test @DisplayName("lanza ValidationException CLIENTE_PADRES cuando ya hay 1 padre")
        void lanzaExcepcionClientePadresMaximoPadre() {
            // Usar lenient en todos para que Mockito no falle por stubs no usados en este flujo
            lenient().when(polizasSaludRepository.findById(1L))
                    .thenReturn(Optional.of(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_PADRES)));
            lenient().when(beneficiariosSaludRepository
                    .existsByPolizaSalud_IdAndNumeroDocumento(anyLong(), anyString()))
                    .thenReturn(false);
            lenient().when(tiposParentescoRepository.findById(1))
                    .thenReturn(Optional.of(parentesco(Enums.TipoParentesco.PADRE)));
            lenient().when(beneficiariosSaludRepository
                    .countByPolizaSalud_PolizaIdAndTipoParentesco_Id(any(), any()))
                    .thenReturn(0L);
            // Este sí se llama y debe retornar 1 para forzar el error
            when(beneficiariosSaludRepository
                    .countByPolizaSalud_IdAndTipoParentesco_Id(any(), any()))
                    .thenReturn(1L);

            assertThatThrownBy(() -> service.crearBeneficiarioSalud(paramsBeneficiario()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("Solo se permite un");
        }

        @Test @DisplayName("crea beneficiario HIJO con cobertura CLIENTE_CONYUGE_HIJOS")
        void creaExitosamenteConyugeHijos() {
            stubBase(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_CONYUGE_HIJOS), false, Enums.TipoParentesco.HIJO);
            BeneficiariosSalud guardado = new BeneficiariosSalud();
            guardado.setNombreCompleto("Carlos López");
            when(beneficiariosSaludRepository.save(any())).thenReturn(guardado);

            assertThat(service.crearBeneficiarioSalud(paramsBeneficiario())).isNotNull();
        }

        @Test @DisplayName("lanza ValidationException CLIENTE_CONYUGE_HIJOS con parentesco inválido")
        void lanzaExcepcionConyugeHijosParentescoInvalido() {
            stubBase(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_CONYUGE_HIJOS), false, Enums.TipoParentesco.PADRE);
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(paramsBeneficiario()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("CLIENTE_CONYUGE_HIJOS");
        }

        @Test @DisplayName("lanza ValidationException cuando parámetros son null")
        void lanzaExcepcionParametrosNull() {
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test @DisplayName("lanza ValidationException cuando póliza salud no existe")
        void lanzaExcepcionPolizaSaludNoEncontrada() {
            when(polizasSaludRepository.findById(1L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(paramsBeneficiario()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("Póliza de salud no encontrada");
        }

        @Test @DisplayName("lanza ValidationException cuando documento ya existe")
        void lanzaExcepcionDocumentoDuplicado() {
            lenient().when(tiposParentescoRepository.findById(any()))
                    .thenReturn(Optional.of(parentesco(Enums.TipoParentesco.PADRE)));
            when(polizasSaludRepository.findById(1L))
                    .thenReturn(Optional.of(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_PADRES)));
            when(beneficiariosSaludRepository
                    .existsByPolizaSalud_IdAndNumeroDocumento(any(), anyString()))
                    .thenReturn(true);
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(paramsBeneficiario()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("documento");
        }

        @Test @DisplayName("lanza ValidationException cuando polizaSaludId es 0")
        void lanzaExcepcionPolizaSaludIdCero() {
            ParametrosCrearBeneficiarioSalud p = paramsBeneficiario();
            p.setPolizaSaludId(0L);
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("polizaSaludId");
        }

        @Test @DisplayName("lanza ValidationException cuando tipoParentescoId es null")
        void lanzaExcepcionTipoParentescoIdNull() {
            ParametrosCrearBeneficiarioSalud p = paramsBeneficiario();
            p.setTipoParentescoId(null);
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("tipoParentescoId");
        }

        @Test @DisplayName("lanza ValidationException cuando tipoParentescoId es 0")
        void lanzaExcepcionTipoParentescoIdCero() {
            ParametrosCrearBeneficiarioSalud p = paramsBeneficiario();
            p.setTipoParentescoId(0L);
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("tipoParentescoId");
        }

        @Test @DisplayName("lanza ValidationException cuando nombreCompleto es null")
        void lanzaExcepcionNombreCompletoNull() {
            ParametrosCrearBeneficiarioSalud p = paramsBeneficiario();
            p.setNombreCompleto(null);
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("nombreCompleto");
        }

        @Test @DisplayName("lanza ValidationException cuando nombreCompleto está en blanco")
        void lanzaExcepcionNombreCompletoBlanco() {
            ParametrosCrearBeneficiarioSalud p = paramsBeneficiario();
            p.setNombreCompleto("  ");
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("nombreCompleto");
        }

        @Test @DisplayName("lanza ValidationException cuando montoAdicional es negativo")
        void lanzaExcepcionMontoAdicionalNegativo() {
            ParametrosCrearBeneficiarioSalud p = paramsBeneficiario();
            p.setMontoAdicional(BigDecimal.valueOf(-100));
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("montoAdicional");
        }

        @Test @DisplayName("crea beneficiario MADRE con cobertura CLIENTE_PADRES")
        void creaExitosamenteClientePadresMadre() {
            ParametrosCrearBeneficiarioSalud p = paramsBeneficiario();
            p.setTipoParentescoId((long) Enums.TipoParentesco.MADRE.getId());
            stubBase(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_PADRES), false, Enums.TipoParentesco.MADRE);
            lenient().when(tiposParentescoRepository.findById(Enums.TipoParentesco.MADRE.getId()))
                    .thenReturn(Optional.of(parentesco(Enums.TipoParentesco.MADRE)));
            BeneficiariosSalud guardado = new BeneficiariosSalud();
            guardado.setNombreCompleto("María López");
            when(beneficiariosSaludRepository.save(any())).thenReturn(guardado);

            assertThat(service.crearBeneficiarioSalud(p)).isNotNull();
        }

        @Test @DisplayName("crea beneficiario CONYUGE con cobertura CLIENTE_CONYUGE_HIJOS")
        void creaExitosamenteConyugeHijosConyuge() {
            ParametrosCrearBeneficiarioSalud p = paramsBeneficiario();
            p.setTipoParentescoId((long) Enums.TipoParentesco.CONYUGE.getId());
            stubBase(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_CONYUGE_HIJOS), false, Enums.TipoParentesco.CONYUGE);
            lenient().when(tiposParentescoRepository.findById(Enums.TipoParentesco.CONYUGE.getId()))
                    .thenReturn(Optional.of(parentesco(Enums.TipoParentesco.CONYUGE)));
            lenient().when(beneficiariosSaludRepository
                    .countByPolizaSalud_PolizaIdAndTipoParentesco_Id(any(), eq(Enums.TipoParentesco.CONYUGE.getId())))
                    .thenReturn(0L);
            BeneficiariosSalud guardado = new BeneficiariosSalud();
            guardado.setNombreCompleto("Ana López");
            when(beneficiariosSaludRepository.save(any())).thenReturn(guardado);

            assertThat(service.crearBeneficiarioSalud(p)).isNotNull();
        }

        @Test @DisplayName("lanza ValidationException CLIENTE_CONYUGE_HIJOS cuando ya hay 1 cónyuge")
        void lanzaExcepcionConyugeMaximo() {
            ParametrosCrearBeneficiarioSalud p = paramsBeneficiario();
            p.setTipoParentescoId((long) Enums.TipoParentesco.CONYUGE.getId());
            lenient().when(polizasSaludRepository.findById(1L))
                    .thenReturn(Optional.of(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_CONYUGE_HIJOS)));
            lenient().when(beneficiariosSaludRepository
                    .existsByPolizaSalud_IdAndNumeroDocumento(anyLong(), anyString()))
                    .thenReturn(false);
            lenient().when(tiposParentescoRepository.findById(Enums.TipoParentesco.CONYUGE.getId()))
                    .thenReturn(Optional.of(parentesco(Enums.TipoParentesco.CONYUGE)));
            when(beneficiariosSaludRepository
                    .countByPolizaSalud_PolizaIdAndTipoParentesco_Id(any(), eq(Enums.TipoParentesco.CONYUGE.getId())))
                    .thenReturn(1L);

            assertThatThrownBy(() -> service.crearBeneficiarioSalud(p))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("cónyuge");
        }

        @Test @DisplayName("crea beneficiario con montoAdicional null (se usa ZERO)")
        void creaConMontoAdicionalNull() {
            ParametrosCrearBeneficiarioSalud p = paramsBeneficiario();
            p.setMontoAdicional(null);
            stubBase(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_PADRES), false, Enums.TipoParentesco.PADRE);
            BeneficiariosSalud guardado = new BeneficiariosSalud();
            guardado.setNombreCompleto("Carlos López");
            when(beneficiariosSaludRepository.save(any())).thenReturn(guardado);

            assertThat(service.crearBeneficiarioSalud(p)).isNotNull();
        }

        @Test @DisplayName("lanza ValidationException cuando tipo parentesco no encontrado")
        void lanzaExcepcionTipoParentescoNoEncontrado() {
            when(polizasSaludRepository.findById(1L))
                    .thenReturn(Optional.of(polizaSaludCon(Enums.TipoCoberturaSalud.CLIENTE_PADRES)));
            when(beneficiariosSaludRepository
                    .existsByPolizaSalud_IdAndNumeroDocumento(any(), anyString()))
                    .thenReturn(false);
            when(tiposParentescoRepository.findById(1)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearBeneficiarioSalud(paramsBeneficiario()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("parentesco");
        }
    }

    // ── consultarPolizaSalud ──────────────────────────────────────────────────

    @Nested @DisplayName("consultarPolizaSalud")
    class ConsultarPolizaSalud {

        @Test @DisplayName("retorna lista de pólizas de salud")
        void retornaLista() {
            when(polizasSaludRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(polizaSaludCon(Enums.TipoCoberturaSalud.SOLO_CLIENTE)));
            ParametrosConsultarPolizaSalud p = new ParametrosConsultarPolizaSalud();
            p.setPolizaId(1L);
            assertThat(service.consultarPolizaSalud(p)).hasSize(1);
        }

        @Test @DisplayName("lanza ValidationException cuando no hay resultados")
        void lanzaExcepcionSinResultados() {
            when(polizasSaludRepository.findAll(any(Specification.class))).thenReturn(List.of());
            assertThatThrownBy(() -> service.consultarPolizaSalud(new ParametrosConsultarPolizaSalud()))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("No se encontraron");
        }

        @Test @DisplayName("filtra por id y retorna resultado")
        void filtraPorId() {
            ParametrosConsultarPolizaSalud p = new ParametrosConsultarPolizaSalud();
            p.setId(1L);
            when(polizasSaludRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(polizaSaludCon(Enums.TipoCoberturaSalud.SOLO_CLIENTE)));
            assertThat(service.consultarPolizaSalud(p)).hasSize(1);
        }

        @Test @DisplayName("filtra por tipoCoberturaId y retorna resultado")
        void filtraPorTipoCoberturaId() {
            ParametrosConsultarPolizaSalud p = new ParametrosConsultarPolizaSalud();
            p.setTipoCoberturaId(1L);
            when(polizasSaludRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(polizaSaludCon(Enums.TipoCoberturaSalud.SOLO_CLIENTE)));
            assertThat(service.consultarPolizaSalud(p)).hasSize(1);
        }

        @Test @DisplayName("filtra por todos los parámetros combinados")
        void filtraPorTodosLosParametros() {
            ParametrosConsultarPolizaSalud p = new ParametrosConsultarPolizaSalud();
            p.setId(1L);
            p.setPolizaId(1L);
            p.setTipoCoberturaId(1L);
            when(polizasSaludRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(polizaSaludCon(Enums.TipoCoberturaSalud.SOLO_CLIENTE)));
            assertThat(service.consultarPolizaSalud(p)).hasSize(1);
        }
    }

    // ── consultarBeneficiariosSalud ───────────────────────────────────────────

    @Nested @DisplayName("consultarBeneficiariosSalud")
    class ConsultarBeneficiariosSalud {

        @Test @DisplayName("retorna lista de beneficiarios")
        void retornaLista() {
            when(polizasSaludRepository.existsById(1L)).thenReturn(true);
            BeneficiariosSalud b = new BeneficiariosSalud();
            b.setNombreCompleto("Ana");
            when(beneficiariosSaludRepository.findByPolizaSalud_Id(1L)).thenReturn(List.of(b));
            assertThat(service.consultarBeneficiariosSalud(1L)).hasSize(1);
        }

        @Test @DisplayName("lanza ValidationException cuando id es null")
        void lanzaExcepcionIdNull() {
            assertThatThrownBy(() -> service.consultarBeneficiariosSalud(null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test @DisplayName("lanza ValidationException cuando póliza salud no existe")
        void lanzaExcepcionPolizaNoExiste() {
            when(polizasSaludRepository.existsById(99L)).thenReturn(false);
            assertThatThrownBy(() -> service.consultarBeneficiariosSalud(99L))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("Póliza de salud no encontrada");
        }
    }
}






