package com.pruebatecnica.microservicio_polizas.api.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pruebatecnica.microservicio_polizas.api.middleware.GlobalExceptionHandler;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Poliza;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposPoliza;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaCompletoDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.PolizaResumenDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_base.ParametrosCrearPolizaBase;
import com.pruebatecnica.microservicio_polizas.dominio.enums.Enums;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaBaseService;
import com.pruebatecnica.microservicio_polizas.dominio.mapeos.IPolizaMapper;
import jakarta.validation.ValidationException;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para {@link PolizasBaseController}.
 * Usa MockMvc standalone (sin contexto de Spring completo).
 */
@ExtendWith(MockitoExtension.class)
class PolizasBaseControllerTest {

    @Mock private IPolizaBaseService service;
    @Mock private IPolizaMapper mapper;

    @InjectMocks
    private PolizasBaseController controller;

    private MockMvc mockMvc;
    private ObjectMapper json;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        json = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // ─── fixtures ────────────────────────────────────────────────────────────

    private Poliza polizaBase() {
        TiposPoliza tp = new TiposPoliza();
        tp.setId(Enums.TipoPoliza.SALUD.getValor());
        tp.setCodigo("SALUD");

        Poliza p = new Poliza();
        p.setClienteId(1);
        p.setTipoPoliza(tp);
        p.setFechaInicio(LocalDate.of(2025, 1, 1));
        p.setFechaFin(LocalDate.of(2026, 1, 1));
        p.setEstado(true);
        p.setNumeroPoliza("P-12345");
        return p;
    }

    private PolizaResumenDTO polizaResumenDTO() {
        PolizaResumenDTO dto = new PolizaResumenDTO();
        dto.setId(1L);
        dto.setNumeroPoliza("P-12345");
        dto.setTipoPolizaId(Enums.TipoPoliza.SALUD.getValor());
        dto.setTipoPolizaCodigo("SALUD");
        dto.setFechaInicio(LocalDate.of(2025, 1, 1));
        dto.setFechaFin(LocalDate.of(2026, 1, 1));
        dto.setEstado(true);
        return dto;
    }

    private PolizaDTO polizaDTO() {
        PolizaDTO dto = new PolizaDTO();
        dto.setId(1L);
        dto.setNumeroPoliza("P-12345");
        dto.setClienteId(1);
        dto.setTipoPolizaId(Enums.TipoPoliza.SALUD.getValor());
        dto.setTipoPolizaCodigo("SALUD");
        dto.setFechaInicio(LocalDate.of(2025, 1, 1));
        dto.setFechaFin(LocalDate.of(2026, 1, 1));
        dto.setEstado(true);
        return dto;
    }

    private PolizaCompletoDTO polizaCompletoDTO() {
        PolizaCompletoDTO dto = new PolizaCompletoDTO();
        dto.setId(1L);
        dto.setNumeroPoliza("P-12345");
        dto.setTipoPolizaId(Enums.TipoPoliza.SALUD.getValor());
        dto.setTipoPolizaCodigo("SALUD");
        dto.setFechaInicio(LocalDate.of(2025, 1, 1));
        dto.setFechaFin(LocalDate.of(2026, 1, 1));
        dto.setEstado(true);
        return dto;
    }

    // ─── GET /api/consultar-polizas-base ─────────────────────────────────────

    @Nested
    @DisplayName("GET /api/consultar-polizas-base")
    @ExtendWith(MockitoExtension.class)
    class ConsultarPolizasBase {

        @Test
        @DisplayName("retorna 200 con lista de pólizas en formato resumen")
        void retorna200ConLista() throws Exception {
            when(service.consultarPolizasBase(any())).thenReturn(List.of(polizaBase()));
            when(mapper.toResumenDto(any(Poliza.class))).thenReturn(polizaResumenDTO());

            mockMvc.perform(get("/api/consultar-polizas-base"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].numeroPoliza").value("P-12345"))
                    .andExpect(jsonPath("$[0].tipoPolizaCodigo").value("SALUD"))
                    .andExpect(jsonPath("$[0].estado").value(true));
        }

        @Test
        @DisplayName("retorna 200 con lista vacía cuando no hay pólizas")
        void retorna200ListaVacia() throws Exception {
            when(service.consultarPolizasBase(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/consultar-polizas-base"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("retorna 200 al filtrar por clienteId")
        void retorna200FiltrandoPorClienteId() throws Exception {
            when(service.consultarPolizasBase(any())).thenReturn(List.of(polizaBase()));
            when(mapper.toResumenDto(any(Poliza.class))).thenReturn(polizaResumenDTO());

            mockMvc.perform(get("/api/consultar-polizas-base")
                            .param("clienteId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].numeroPoliza").value("P-12345"));
        }

        @Test
        @DisplayName("retorna 200 al filtrar por tipoPolizaId y activo")
        void retorna200FiltrandoPorTipoYActivo() throws Exception {
            when(service.consultarPolizasBase(any())).thenReturn(List.of(polizaBase()));
            when(mapper.toResumenDto(any(Poliza.class))).thenReturn(polizaResumenDTO());

            mockMvc.perform(get("/api/consultar-polizas-base")
                            .param("tipoPolizaId", "3")
                            .param("activo", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].tipoPolizaId").value(3));
        }
    }

    // ─── POST /api/crear-poliza-base ─────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/crear-poliza-base")
    @ExtendWith(MockitoExtension.class)
    class CrearPolizaBase {

        @Test
        @DisplayName("retorna 200 con póliza creada")
        void retorna200PolizaCreada() throws Exception {
            ParametrosCrearPolizaBase params = new ParametrosCrearPolizaBase();
            params.setClienteId(1L);
            params.setTipoPolizaId(3);
            params.setFechaInicio(LocalDate.of(2025, 1, 1));
            params.setFechaFin(LocalDate.of(2026, 1, 1));

            when(service.crearPolizaBase(any())).thenReturn(polizaBase());
            when(mapper.toDto(any(Poliza.class))).thenReturn(polizaDTO());

            mockMvc.perform(post("/api/crear-poliza-base")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numeroPoliza").value("P-12345"))
                    .andExpect(jsonPath("$.clienteId").value(1))
                    .andExpect(jsonPath("$.tipoPolizaCodigo").value("SALUD"));
        }

        @Test
        @DisplayName("retorna 400 cuando el servicio lanza ValidationException")
        void propagaValidationException() throws Exception {
            ParametrosCrearPolizaBase params = new ParametrosCrearPolizaBase();
            params.setClienteId(1L);
            params.setTipoPolizaId(3);
            params.setFechaInicio(LocalDate.of(2025, 1, 1));
            params.setFechaFin(LocalDate.of(2026, 1, 1));

            when(service.crearPolizaBase(any()))
                    .thenThrow(new ValidationException("clienteId es requerido"));

            mockMvc.perform(post("/api/crear-poliza-base")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("retorna 500 cuando el servicio lanza RuntimeException")
        void propagaRuntimeException() throws Exception {
            ParametrosCrearPolizaBase params = new ParametrosCrearPolizaBase();
            params.setClienteId(1L);
            params.setTipoPolizaId(3);
            params.setFechaInicio(LocalDate.of(2025, 1, 1));
            params.setFechaFin(LocalDate.of(2026, 1, 1));

            when(service.crearPolizaBase(any()))
                    .thenThrow(new RuntimeException("Cliente no encontrado"));

            mockMvc.perform(post("/api/crear-poliza-base")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ─── GET /api/consultar-polizas-cliente ──────────────────────────────────

    @Nested
    @DisplayName("GET /api/consultar-polizas-cliente")
    @ExtendWith(MockitoExtension.class)
    class ConsultarPolizasCliente {

        @Test
        @DisplayName("retorna 200 con lista de pólizas completas")
        void retorna200ConListaCompleta() throws Exception {
            when(service.consultarPolizasCliente(any()))
                    .thenReturn(List.of(polizaCompletoDTO()));

            mockMvc.perform(get("/api/consultar-polizas-cliente")
                            .param("clienteId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].numeroPoliza").value("P-12345"))
                    .andExpect(jsonPath("$[0].tipoPolizaCodigo").value("SALUD"))
                    .andExpect(jsonPath("$[0].estado").value(true));
        }

        @Test
        @DisplayName("retorna 200 con lista vacía cuando no hay pólizas del cliente")
        void retorna200ListaVacia() throws Exception {
            when(service.consultarPolizasCliente(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/consultar-polizas-cliente")
                            .param("clienteId", "99"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("retorna 200 sin parámetros")
        void retorna200SinParametros() throws Exception {
            when(service.consultarPolizasCliente(any()))
                    .thenReturn(List.of(polizaCompletoDTO()));

            mockMvc.perform(get("/api/consultar-polizas-cliente"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }
    }
}

