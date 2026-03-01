package com.pruebatecnica.microservicio_polizas.api.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.*;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosCrearPolizaVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.poliza_vehiculo.ParametrosCrearVehiculo;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IPolizaVehiculoService;
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
 * Pruebas unitarias para {@link PolizasVehiculoController}.
 */
@ExtendWith(MockitoExtension.class)
class PolizasVehiculoControllerTest {

    @Mock private IPolizaVehiculoService service;
    @Mock private IPolizaMapper mapper;

    @InjectMocks
    private PolizasVehiculoController controller;

    private MockMvc mockMvc;
    private ObjectMapper json;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        json = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // ─── POST /crear-poliza-vehiculo ──────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/polizas/vehiculo/crear-poliza-vehiculo")
    class CrearPolizaVehiculo {

        @Test
        @DisplayName("retorna 200 con póliza de vehículo creada")
        void retorna200() throws Exception {
            ParametrosCrearPolizaVehiculo params = new ParametrosCrearPolizaVehiculo();
            params.setPolizaId(1L);

            PolizasVehiculo entidad = new PolizasVehiculo();
            PolizaVehiculoDto dto = new PolizaVehiculoDto();
            dto.setId(1L);

            when(service.crearPolizaVehiculo(any())).thenReturn(entidad);
            when(mapper.toDto(any(PolizasVehiculo.class))).thenReturn(dto);

            mockMvc.perform(post("/api/polizas/vehiculo/crear-poliza-vehiculo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    // ─── POST /crear-vehiculo ─────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/polizas/vehiculo/crear-vehiculo")
    class CrearVehiculo {

        @Test
        @DisplayName("retorna 200 con vehículo creado")
        void retorna200() throws Exception {
            ParametrosCrearVehiculo params = new ParametrosCrearVehiculo();
            params.setPolizaVehiculoId(1L);
            params.setPlaca("ABC123");
            params.setMarca("Toyota");
            params.setModelo("Corolla");
            params.setAnio(2022);
            params.setValorAsegurado(BigDecimal.valueOf(50_000_000));

            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setPlaca("ABC123");
            VehiculoDTO dto = new VehiculoDTO();
            dto.setPlaca("ABC123");

            when(service.crearVehiculo(any())).thenReturn(vehiculo);
            when(mapper.toDto(any(Vehiculo.class))).thenReturn(dto);

            mockMvc.perform(post("/api/polizas/vehiculo/crear-vehiculo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.placa").value("ABC123"));
        }
    }

    // ─── GET /consultar-polizas-vehiculo ──────────────────────────────────────

    @Nested
    @DisplayName("GET /api/polizas/vehiculo/consultar-polizas-vehiculo")
    class ConsultarPolizasVehiculo {

        @Test
        @DisplayName("retorna 200 con lista de pólizas de vehículo")
        void retorna200() throws Exception {
            PolizasVehiculo pv = new PolizasVehiculo();
            PolizaVehiculoDto dto = new PolizaVehiculoDto();
            dto.setId(1L);

            when(service.consultarPolizasVehiculo(any())).thenReturn(List.of(pv));
            when(mapper.toPolizaVehiculoDtoList(any())).thenReturn(List.of(dto));

            mockMvc.perform(get("/api/polizas/vehiculo/consultar-polizas-vehiculo"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }
    }

    // ─── GET /consultar-vehiculos/{id} ────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/polizas/vehiculo/consultar-vehiculos/{polizaVehiculoId}")
    class ConsultarVehiculos {

        @Test
        @DisplayName("retorna 200 con lista de vehículos")
        void retorna200() throws Exception {
            Vehiculo v = new Vehiculo();
            v.setPlaca("XYZ999");
            VehiculoDTO dto = new VehiculoDTO();
            dto.setPlaca("XYZ999");

            when(service.consultarVehiculos(1L)).thenReturn(List.of(v));
            when(mapper.toVehiculoDtoList(any())).thenReturn(List.of(dto));

            mockMvc.perform(get("/api/polizas/vehiculo/consultar-vehiculos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].placa").value("XYZ999"));
        }
    }
}

