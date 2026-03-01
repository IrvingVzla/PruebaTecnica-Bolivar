package com.pruebatecnica.microservicio_polizas.dominio.servicios;

import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosConsultarPolizaVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosCrearBeneficiarioVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosCrearPolizaVida;
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
 * Pruebas unitarias para {@link PolizaVidaService}.
 */
@ExtendWith(MockitoExtension.class)
class PolizaVidaServiceTest {

    @Mock private PolizasVidaRepository polizasVidaRepository;
    @Mock private BeneficiariosVidaRepository beneficiariosVidaRepository;
    @Mock private PolizaRepository polizaRepository;
    @Mock private TiposParentescoRepository tiposParentescoRepository;

    @InjectMocks
    private PolizaVidaService service;

    // ─── fixtures ───────────────────────────────────────────────────────────

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

    private Poliza polizaActivaVida() {
        Poliza p = new Poliza();
        p.setTipoPoliza(tipoPolizaVida());
        p.setEstado(true);
        return p;
    }

    private PolizasVida polizaVidaBase() {
        PolizasVida pv = new PolizasVida();
        pv.setPoliza(polizaActivaVida());
        pv.setMontoAsegurado(BigDecimal.valueOf(50_000));
        return pv;
    }

    private TiposParentesco tipoParentescoPadre() {
        TiposParentesco tp = new TiposParentesco();
        tp.setId(Enums.TipoParentesco.PADRE.getId());
        tp.setCodigo("PADRE");
        return tp;
    }

    private ParametrosCrearPolizaVida paramsCrearVida() {
        ParametrosCrearPolizaVida p = new ParametrosCrearPolizaVida();
        p.setPolizaId(1L);
        p.setMontoAsegurado(BigDecimal.valueOf(100_000));
        return p;
    }

    private ParametrosCrearBeneficiarioVida paramsCrearBeneficiario() {
        ParametrosCrearBeneficiarioVida p = new ParametrosCrearBeneficiarioVida();
        p.setPolizaVidaId(1L);
        p.setTipoParentescoId(1L);
        p.setNombreCompleto("Ana Pérez");
        p.setNumeroDocumento("98765432");
        return p;
    }

    // ─── crearPolizaVida ────────────────────────────────────────────────────

    @Nested
    @DisplayName("crearPolizaVida")
    class CrearPolizaVida {

        @Test
        @DisplayName("crea póliza de vida exitosamente")
        void creaExitosamente() {
            when(polizaRepository.findById(1L)).thenReturn(Optional.of(polizaActivaVida()));
            when(polizasVidaRepository.existsByPoliza_Id(1L)).thenReturn(false);
            PolizasVida guardada = polizaVidaBase();
            when(polizasVidaRepository.save(any())).thenReturn(guardada);

            PolizasVida resultado = service.crearPolizaVida(paramsCrearVida());

            assertThat(resultado).isNotNull();
            assertThat(resultado.getMontoAsegurado()).isEqualByComparingTo("50000");
            verify(polizasVidaRepository).save(any(PolizasVida.class));
        }

        @Test
        @DisplayName("lanza ValidationException cuando parámetros son null")
        void lanzaExcepcionParametrosNull() {
            assertThatThrownBy(() -> service.crearPolizaVida(null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("lanza ValidationException cuando polizaId es null")
        void lanzaExcepcionPolizaIdNull() {
            ParametrosCrearPolizaVida p = paramsCrearVida();
            p.setPolizaId(null);
            assertThatThrownBy(() -> service.crearPolizaVida(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("polizaId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando monto asegurado es cero")
        void lanzaExcepcionMontoCero() {
            ParametrosCrearPolizaVida p = paramsCrearVida();
            p.setMontoAsegurado(BigDecimal.ZERO);
            assertThatThrownBy(() -> service.crearPolizaVida(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("montoAsegurado");
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza base no existe")
        void lanzaExcepcionPolizaNoEncontrada() {
            when(polizaRepository.findById(1L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearPolizaVida(paramsCrearVida()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Póliza base no encontrada");
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza base no es tipo VIDA")
        void lanzaExcepcionNoEsVida() {
            Poliza p = new Poliza();
            p.setTipoPoliza(tipoPolizaSalud());
            p.setEstado(true);
            when(polizaRepository.findById(1L)).thenReturn(Optional.of(p));
            assertThatThrownBy(() -> service.crearPolizaVida(paramsCrearVida()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tipo VIDA");
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza base no está activa")
        void lanzaExcepcionPolizaInactiva() {
            Poliza p = polizaActivaVida();
            p.setEstado(false);
            when(polizaRepository.findById(1L)).thenReturn(Optional.of(p));
            assertThatThrownBy(() -> service.crearPolizaVida(paramsCrearVida()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("activa");
        }

        @Test
        @DisplayName("lanza ValidationException cuando ya existe especialización de vida")
        void lanzaExcepcionYaExiste() {
            when(polizaRepository.findById(1L)).thenReturn(Optional.of(polizaActivaVida()));
            when(polizasVidaRepository.existsByPoliza_Id(1L)).thenReturn(true);
            assertThatThrownBy(() -> service.crearPolizaVida(paramsCrearVida()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Ya existe una póliza de vida");
        }

        @Test
        @DisplayName("lanza ValidationException cuando polizaId es 0")
        void lanzaExcepcionPolizaIdCero() {
            ParametrosCrearPolizaVida p = paramsCrearVida();
            p.setPolizaId(0L);
            assertThatThrownBy(() -> service.crearPolizaVida(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("polizaId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando monto asegurado es null")
        void lanzaExcepcionMontoNull() {
            ParametrosCrearPolizaVida p = paramsCrearVida();
            p.setMontoAsegurado(null);
            assertThatThrownBy(() -> service.crearPolizaVida(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("montoAsegurado");
        }

        @Test
        @DisplayName("lanza ValidationException cuando monto asegurado es negativo")
        void lanzaExcepcionMontoNegativo() {
            ParametrosCrearPolizaVida p = paramsCrearVida();
            p.setMontoAsegurado(BigDecimal.valueOf(-100));
            assertThatThrownBy(() -> service.crearPolizaVida(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("montoAsegurado");
        }
    }

    // ─── crearBeneficiarioVida ──────────────────────────────────────────────

    @Nested
    @DisplayName("crearBeneficiarioVida")
    class CrearBeneficiarioVida {

        @Test
        @DisplayName("crea beneficiario exitosamente")
        void creaExitosamente() {
            when(polizasVidaRepository.findById(1L)).thenReturn(Optional.of(polizaVidaBase()));
            when(beneficiariosVidaRepository.countByPolizasVida_PolizaId(1L)).thenReturn(0L);
            when(beneficiariosVidaRepository.existsByPolizasVida_PolizaIdAndNumeroDocumento(anyLong(), anyString())).thenReturn(false);
            when(tiposParentescoRepository.findById(1)).thenReturn(Optional.of(tipoParentescoPadre()));
            BeneficiariosVida guardado = new BeneficiariosVida();
            guardado.setNombreCompleto("Ana Pérez");
            when(beneficiariosVidaRepository.save(any())).thenReturn(guardado);

            BeneficiariosVida resultado = service.crearBeneficiarioVida(paramsCrearBeneficiario());

            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombreCompleto()).isEqualTo("Ana Pérez");
        }

        @Test
        @DisplayName("lanza ValidationException cuando ya hay 2 beneficiarios")
        void lanzaExcepcionMaximoBeneficiarios() {
            when(polizasVidaRepository.findById(1L)).thenReturn(Optional.of(polizaVidaBase()));
            when(beneficiariosVidaRepository.countByPolizasVida_PolizaId(1L)).thenReturn(2L);
            assertThatThrownBy(() -> service.crearBeneficiarioVida(paramsCrearBeneficiario()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("máximo 2");
        }

        @Test
        @DisplayName("lanza ValidationException cuando documento duplicado")
        void lanzaExcepcionDocumentoDuplicado() {
            when(polizasVidaRepository.findById(1L)).thenReturn(Optional.of(polizaVidaBase()));
            when(beneficiariosVidaRepository.countByPolizasVida_PolizaId(1L)).thenReturn(0L);
            when(beneficiariosVidaRepository.existsByPolizasVida_PolizaIdAndNumeroDocumento(anyLong(), anyString())).thenReturn(true);
            assertThatThrownBy(() -> service.crearBeneficiarioVida(paramsCrearBeneficiario()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("documento");
        }

        @Test
        @DisplayName("lanza ValidationException cuando tipo parentesco no encontrado")
        void lanzaExcepcionTipoParentescoNoEncontrado() {
            when(polizasVidaRepository.findById(1L)).thenReturn(Optional.of(polizaVidaBase()));
            when(beneficiariosVidaRepository.countByPolizasVida_PolizaId(1L)).thenReturn(0L);
            when(beneficiariosVidaRepository.existsByPolizasVida_PolizaIdAndNumeroDocumento(anyLong(), anyString())).thenReturn(false);
            when(tiposParentescoRepository.findById(1)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearBeneficiarioVida(paramsCrearBeneficiario()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("parentesco");
        }

        @Test
        @DisplayName("lanza ValidationException cuando parámetros son null")
        void lanzaExcepcionParametrosNull() {
            assertThatThrownBy(() -> service.crearBeneficiarioVida(null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("lanza ValidationException cuando polizaVida no encontrada")
        void lanzaExcepcionPolizaVidaNoEncontrada() {
            when(polizasVidaRepository.findById(1L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.crearBeneficiarioVida(paramsCrearBeneficiario()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Póliza de vida no encontrada");
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza base no está activa al crear beneficiario")
        void lanzaExcepcionPolizaBaseInactivaBeneficiario() {
            PolizasVida pvInactiva = new PolizasVida();
            Poliza pInactiva = polizaActivaVida();
            pInactiva.setEstado(false);
            pvInactiva.setPoliza(pInactiva);
            pvInactiva.setMontoAsegurado(BigDecimal.valueOf(50_000));
            when(polizasVidaRepository.findById(1L)).thenReturn(Optional.of(pvInactiva));
            assertThatThrownBy(() -> service.crearBeneficiarioVida(paramsCrearBeneficiario()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("activa");
        }

        @Test
        @DisplayName("lanza ValidationException cuando polizaVidaId es 0")
        void lanzaExcepcionPolizaVidaIdCero() {
            ParametrosCrearBeneficiarioVida p = paramsCrearBeneficiario();
            p.setPolizaVidaId(0L);
            assertThatThrownBy(() -> service.crearBeneficiarioVida(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("polizaVidaId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando tipoParentescoId es null")
        void lanzaExcepcionTipoParentescoIdNull() {
            ParametrosCrearBeneficiarioVida p = paramsCrearBeneficiario();
            p.setTipoParentescoId(null);
            assertThatThrownBy(() -> service.crearBeneficiarioVida(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tipoParentescoId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando tipoParentescoId es 0")
        void lanzaExcepcionTipoParentescoIdCero() {
            ParametrosCrearBeneficiarioVida p = paramsCrearBeneficiario();
            p.setTipoParentescoId(0L);
            assertThatThrownBy(() -> service.crearBeneficiarioVida(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tipoParentescoId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando nombreCompleto es null")
        void lanzaExcepcionNombreCompletoNull() {
            ParametrosCrearBeneficiarioVida p = paramsCrearBeneficiario();
            p.setNombreCompleto(null);
            assertThatThrownBy(() -> service.crearBeneficiarioVida(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("nombreCompleto");
        }

        @Test
        @DisplayName("lanza ValidationException cuando nombreCompleto está en blanco")
        void lanzaExcepcionNombreCompletoBlanco() {
            ParametrosCrearBeneficiarioVida p = paramsCrearBeneficiario();
            p.setNombreCompleto("  ");
            assertThatThrownBy(() -> service.crearBeneficiarioVida(p))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("nombreCompleto");
        }

        @Test
        @DisplayName("crea beneficiario con documento null (no valida duplicado)")
        void creaExitosamenteConDocumentoNull() {
            ParametrosCrearBeneficiarioVida p = paramsCrearBeneficiario();
            p.setNumeroDocumento(null);
            when(polizasVidaRepository.findById(1L)).thenReturn(Optional.of(polizaVidaBase()));
            when(beneficiariosVidaRepository.countByPolizasVida_PolizaId(1L)).thenReturn(0L);
            when(tiposParentescoRepository.findById(1)).thenReturn(Optional.of(tipoParentescoPadre()));
            BeneficiariosVida guardado = new BeneficiariosVida();
            guardado.setNombreCompleto("Ana Pérez");
            when(beneficiariosVidaRepository.save(any())).thenReturn(guardado);

            BeneficiariosVida resultado = service.crearBeneficiarioVida(p);
            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("crea beneficiario con documento en blanco (no valida duplicado)")
        void creaExitosamenteConDocumentoBlanco() {
            ParametrosCrearBeneficiarioVida p = paramsCrearBeneficiario();
            p.setNumeroDocumento("  ");
            when(polizasVidaRepository.findById(1L)).thenReturn(Optional.of(polizaVidaBase()));
            when(beneficiariosVidaRepository.countByPolizasVida_PolizaId(1L)).thenReturn(0L);
            when(tiposParentescoRepository.findById(1)).thenReturn(Optional.of(tipoParentescoPadre()));
            BeneficiariosVida guardado = new BeneficiariosVida();
            guardado.setNombreCompleto("Ana Pérez");
            when(beneficiariosVidaRepository.save(any())).thenReturn(guardado);

            BeneficiariosVida resultado = service.crearBeneficiarioVida(p);
            assertThat(resultado).isNotNull();
        }
    }

    // ─── consultarPolizaVida ────────────────────────────────────────────────

    @Nested
    @DisplayName("consultarPolizaVida")
    class ConsultarPolizaVida {

        @Test
        @DisplayName("retorna lista de pólizas de vida")
        void retornaLista() {
            when(polizasVidaRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(polizaVidaBase()));
            ParametrosConsultarPolizaVida p = new ParametrosConsultarPolizaVida();
            p.setPolizaId(1L);
            List<PolizasVida> resultado = service.consultarPolizaVida(p);
            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("lanza ValidationException cuando no encuentra resultados")
        void lanzaExcepcionSinResultados() {
            when(polizasVidaRepository.findAll(any(Specification.class))).thenReturn(List.of());
            assertThatThrownBy(() -> service.consultarPolizaVida(new ParametrosConsultarPolizaVida()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("No se encontraron");
        }

        @Test
        @DisplayName("filtra por id y retorna resultado")
        void filtraPorId() {
            ParametrosConsultarPolizaVida p = new ParametrosConsultarPolizaVida();
            p.setId(1L);
            when(polizasVidaRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(polizaVidaBase()));
            assertThat(service.consultarPolizaVida(p)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por polizaId y id combinados")
        void filtraPorPolizaIdYId() {
            ParametrosConsultarPolizaVida p = new ParametrosConsultarPolizaVida();
            p.setId(1L);
            p.setPolizaId(1L);
            when(polizasVidaRepository.findAll(any(Specification.class)))
                    .thenReturn(List.of(polizaVidaBase()));
            assertThat(service.consultarPolizaVida(p)).hasSize(1);
        }
    }

    // ─── consultarBeneficiariosVida ─────────────────────────────────────────

    @Nested
    @DisplayName("consultarBeneficiariosVida")
    class ConsultarBeneficiariosVida {

        @Test
        @DisplayName("retorna beneficiarios exitosamente")
        void retornaBeneficiarios() {
            when(polizasVidaRepository.existsById(1L)).thenReturn(true);
            BeneficiariosVida b = new BeneficiariosVida();
            b.setNombreCompleto("Ana Pérez");
            when(beneficiariosVidaRepository.findByPolizasVida_PolizaId(1L)).thenReturn(List.of(b));
            List<BeneficiariosVida> resultado = service.consultarBeneficiariosVida(1L);
            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("lanza ValidationException cuando id es null")
        void lanzaExcepcionIdNull() {
            assertThatThrownBy(() -> service.consultarBeneficiariosVida(null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("lanza ValidationException cuando id es 0")
        void lanzaExcepcionIdCero() {
            assertThatThrownBy(() -> service.consultarBeneficiariosVida(0L))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("lanza ValidationException cuando id es negativo")
        void lanzaExcepcionIdNegativo() {
            assertThatThrownBy(() -> service.consultarBeneficiariosVida(-1L))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("lanza ValidationException cuando póliza de vida no existe")
        void lanzaExcepcionPolizaNoExiste() {
            when(polizasVidaRepository.existsById(99L)).thenReturn(false);
            assertThatThrownBy(() -> service.consultarBeneficiariosVida(99L))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Póliza de vida no encontrada");
        }
    }
}





