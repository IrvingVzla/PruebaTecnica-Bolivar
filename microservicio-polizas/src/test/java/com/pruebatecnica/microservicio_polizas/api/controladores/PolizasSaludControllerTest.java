package com.pruebatecnica.microservicio_polizas.api.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosCrearBeneficiarioSalud;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_salud.ParametrosCrearPolizaSalud;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaSaludService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para {@link PolizasSaludController}.
 */
@ExtendWith(MockitoExtension.class)
class PolizasSaludControllerTest {

    @Mock private IPolizaSaludService service;
    @Mock private IPolizaMapper mapper;

    @InjectMocks
    private PolizasSaludController controller;

    private MockMvc mockMvc;
    private ObjectMapper json;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        json = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // ─── POST /crear-poliza-salud ─────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/polizas/salud/crear-poliza-salud")
    class CrearPolizaSalud {

        @Test
        @DisplayName("retorna 200 con póliza de salud creada")
        void retorna200() throws Exception {
            ParametrosCrearPolizaSalud params = new ParametrosCrearPolizaSalud();
            params.setPolizaId(1L);
            params.setTipoCoberturaId(1);

            PolizasSalud entidad = new PolizasSalud();
            PolizaSaludDto dto = new PolizaSaludDto();
            dto.setId(1L);

            when(service.crearPolizaSalud(any())).thenReturn(entidad);
            when(mapper.toDto(any(PolizasSalud.class))).thenReturn(dto);

            mockMvc.perform(post("/api/polizas/salud/crear-poliza-salud")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    // ─── POST /crear-beneficiario-salud ──────────────────────────────────────

    @Nested
    @DisplayName("POST /api/polizas/salud/crear-beneficiario-salud")
    class CrearBeneficiarioSalud {

        @Test
        @DisplayName("retorna 200 con beneficiario creado")
        void retorna200() throws Exception {
            ParametrosCrearBeneficiarioSalud params = new ParametrosCrearBeneficiarioSalud();
            params.setPolizaSaludId(1L);
            params.setTipoParentescoId(1L);
            params.setNombreCompleto("Luis Torres");

            BeneficiariosSalud benef = new BeneficiariosSalud();
            benef.setNombreCompleto("Luis Torres");
            BeneficiarioSaludDTO dto = new BeneficiarioSaludDTO();
            dto.setNombreCompleto("Luis Torres");

            when(service.crearBeneficiarioSalud(any())).thenReturn(benef);
            when(mapper.toDto(any(BeneficiariosSalud.class))).thenReturn(dto);

            mockMvc.perform(post("/api/polizas/salud/crear-beneficiario-salud")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombreCompleto").value("Luis Torres"));
        }
    }

    // ─── GET /consultar-polizas-salud ─────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/polizas/salud/consultar-polizas-salud")
    class ConsultarPolizasSalud {

        @Test
        @DisplayName("retorna 200 con lista de pólizas de salud")
        void retorna200() throws Exception {
            PolizasSalud ps = new PolizasSalud();
            PolizaSaludDto dto = new PolizaSaludDto();
            dto.setId(1L);

            when(service.consultarPolizaSalud(any())).thenReturn(List.of(ps));
            when(mapper.toPolizaSaludDtoList(any())).thenReturn(List.of(dto));

            mockMvc.perform(get("/api/polizas/salud/consultar-polizas-salud"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }
    }

    // ─── GET /consultar-beneficiarios-salud/{id} ─────────────────────────────

    @Nested
    @DisplayName("GET /api/polizas/salud/consultar-beneficiarios-salud/{polizaSaludId}")
    class ConsultarBeneficiariosSalud {

        @Test
        @DisplayName("retorna 200 con lista de beneficiarios")
        void retorna200() throws Exception {
            BeneficiariosSalud benef = new BeneficiariosSalud();
            benef.setNombreCompleto("Luis Torres");
            BeneficiarioSaludDTO dto = new BeneficiarioSaludDTO();
            dto.setNombreCompleto("Luis Torres");

            when(service.consultarBeneficiariosSalud(1L)).thenReturn(List.of(benef));
            when(mapper.toBeneficiarioSaludDtoList(any())).thenReturn(List.of(dto));

            mockMvc.perform(get("/api/polizas/salud/consultar-beneficiarios-salud/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombreCompleto").value("Luis Torres"));
        }
    }
}

