package com.pruebatecnica.microservicio_polizas.api.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosCrearBeneficiarioVida;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vida.ParametrosCrearPolizaVida;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaVidaService;
import com.pruebatecnica.microservicio_polizas.dominio.mapeos.IPolizaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para {@link PolizasVidaController}.
 */
@ExtendWith(MockitoExtension.class)
class PolizasVidaControllerTest {

    @Mock private IPolizaVidaService service;
    @Mock private IPolizaMapper mapper;

    @InjectMocks
    private PolizasVidaController controller;

    private MockMvc mockMvc;
    private ObjectMapper json;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        json = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // ─── fixtures ────────────────────────────────────────────────────────────

    private PolizasVida polizaVidaBase() {
        PolizasVida pv = new PolizasVida();
        pv.setMontoAsegurado(BigDecimal.valueOf(100_000));
        return pv;
    }

    private PolizaVidaDTO polizaVidaDTO() {
        PolizaVidaDTO dto = new PolizaVidaDTO();
        dto.setId(1L);
        dto.setMontoAsegurado(BigDecimal.valueOf(100_000));
        return dto;
    }

    // ─── POST /crear-poliza-vida ─────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/polizas/vida/crear-poliza-vida")
    class CrearPolizaVida {

        @Test
        @DisplayName("retorna 200 con póliza de vida creada")
        void retorna200() throws Exception {
            ParametrosCrearPolizaVida params = new ParametrosCrearPolizaVida();
            params.setPolizaId(1L);
            params.setMontoAsegurado(BigDecimal.valueOf(100_000));

            when(service.crearPolizaVida(any())).thenReturn(polizaVidaBase());
            when(mapper.toDto(any(PolizasVida.class))).thenReturn(polizaVidaDTO());

            mockMvc.perform(post("/api/polizas/vida/crear-poliza-vida")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.montoAsegurado").value(100000));
        }
    }

    // ─── POST /crear-beneficiario-vida ───────────────────────────────────────

    @Nested
    @DisplayName("POST /api/polizas/vida/crear-beneficiario-vida")
    class CrearBeneficiarioVida {

        @Test
        @DisplayName("retorna 200 con beneficiario creado")
        void retorna200() throws Exception {
            ParametrosCrearBeneficiarioVida params = new ParametrosCrearBeneficiarioVida();
            params.setPolizaVidaId(1L);
            params.setTipoParentescoId(1L);
            params.setNombreCompleto("Ana García");

            BeneficiariosVida benef = new BeneficiariosVida();
            benef.setNombreCompleto("Ana García");

            BeneficiarioVidaDTO dto = new BeneficiarioVidaDTO();
            dto.setNombreCompleto("Ana García");

            when(service.crearBeneficiarioVida(any())).thenReturn(benef);
            when(mapper.toDto(any(BeneficiariosVida.class))).thenReturn(dto);

            mockMvc.perform(post("/api/polizas/vida/crear-beneficiario-vida")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombreCompleto").value("Ana García"));
        }
    }

    // ─── GET /consultar-polizas-vida ─────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/polizas/vida/consultar-polizas-vida")
    class ConsultarPolizasVida {

        @Test
        @DisplayName("retorna 200 con lista de pólizas")
        void retorna200() throws Exception {
            when(service.consultarPolizaVida(any())).thenReturn(List.of(polizaVidaBase()));
            when(mapper.toPolizaVidaDtoList(any())).thenReturn(List.of(polizaVidaDTO()));

            mockMvc.perform(get("/api/polizas/vida/consultar-polizas-vida"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].montoAsegurado").value(100000));
        }
    }

    // ─── GET /consultar-beneficiarios-vida/{id} ──────────────────────────────

    @Nested
    @DisplayName("GET /api/polizas/vida/consultar-beneficiarios-vida/{polizaVidaId}")
    class ConsultarBeneficiariosVida {

        @Test
        @DisplayName("retorna 200 con lista de beneficiarios")
        void retorna200() throws Exception {
            BeneficiariosVida benef = new BeneficiariosVida();
            benef.setNombreCompleto("Ana García");
            BeneficiarioVidaDTO dto = new BeneficiarioVidaDTO();
            dto.setNombreCompleto("Ana García");

            when(service.consultarBeneficiariosVida(1L)).thenReturn(List.of(benef));
            when(mapper.toBeneficiarioVidaDtoList(any())).thenReturn(List.of(dto));

            mockMvc.perform(get("/api/polizas/vida/consultar-beneficiarios-vida/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombreCompleto").value("Ana García"));
        }
    }
}

