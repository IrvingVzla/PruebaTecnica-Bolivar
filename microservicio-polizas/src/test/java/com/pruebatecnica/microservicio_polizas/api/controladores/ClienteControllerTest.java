package com.pruebatecnica.microservicio_polizas.api.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pruebatecnica.microservicio_polizas.api.middleware.GlobalExceptionHandler;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Cliente;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposDocumento;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.ClienteDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.dto.TipoDocumentoDTO;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosActualizarCliente;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosCrearCliente;
import com.pruebatecnica.microservicio_polizas.dominio.interfaces.servicio.IClienteService;
import com.pruebatecnica.microservicio_polizas.dominio.mapeos.IClienteMapper;
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
 * Pruebas unitarias para {@link ClienteController}.
 * Usa MockMvc standalone (sin contexto de Spring completo).
 */
@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock private IClienteService service;
    @Mock private IClienteMapper mapper;

    @InjectMocks
    private ClienteController controller;

    private MockMvc mockMvc;
    private ObjectMapper json;

    @BeforeEach
    void setUp() {
        // Se incluye GlobalExceptionHandler para que las ValidationException sean manejadas
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        json = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // ─── fixtures ────────────────────────────────────────────────────────────

    private Cliente clienteBase() {
        TiposDocumento td = new TiposDocumento();
        td.setId(1); td.setCodigo("CC");
        Cliente c = new Cliente();
        c.setTipoDocumento(td);
        c.setNumeroDocumento("12345678");
        c.setNombres("Juan");
        c.setApellidos("Pérez");
        c.setEmail("juan@mail.com");
        return c;
    }

    private ClienteDTO clienteDTOBase() {
        TipoDocumentoDTO td = new TipoDocumentoDTO();
        td.setId(1); td.setCodigo("CC");
        ClienteDTO dto = new ClienteDTO();
        dto.setId(1L);
        dto.setTipoDocumento(td);
        dto.setNumeroDocumento("12345678");
        dto.setNombres("Juan");
        dto.setApellidos("Pérez");
        dto.setEmail("juan@mail.com");
        return dto;
    }

    // ─── GET /api/clientes/consultar-clientes ────────────────────────────────

    @Nested
    @DisplayName("GET /api/clientes/consultar-clientes")
    class ConsultarClientes {

        @Test
        @DisplayName("retorna 200 con lista de clientes")
        void retorna200ConLista() throws Exception {
            when(service.consultarClientes(any())).thenReturn(List.of(clienteBase()));
            when(mapper.toDto(any(Cliente.class))).thenReturn(clienteDTOBase());

            mockMvc.perform(get("/api/clientes/consultar-clientes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombres").value("Juan"))
                    .andExpect(jsonPath("$[0].numeroDocumento").value("12345678"));
        }

        @Test
        @DisplayName("retorna 200 con lista vacía cuando no hay clientes")
        void retorna200ListaVacia() throws Exception {
            when(service.consultarClientes(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/clientes/consultar-clientes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    // ─── POST /api/clientes/crear-cliente ────────────────────────────────────

    @Nested
    @DisplayName("POST /api/clientes/crear-cliente")
    class CrearCliente {

        @Test
        @DisplayName("retorna 200 con cliente creado")
        void retorna200ClienteCreado() throws Exception {
            ParametrosCrearCliente params = new ParametrosCrearCliente();
            params.setTipoDocumentoId(1L);
            params.setNumeroDocumento("12345678");
            params.setNombres("Juan");
            params.setApellidos("Pérez");
            params.setEmail("juan@mail.com");
            params.setFechaNacimiento(LocalDate.of(1990, 1, 1));

            when(service.crearCliente(any())).thenReturn(clienteBase());
            when(mapper.toDto(any(Cliente.class))).thenReturn(clienteDTOBase());

            mockMvc.perform(post("/api/clientes/crear-cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombres").value("Juan"));
        }

        @Test
        @DisplayName("retorna 400 cuando el servicio lanza ValidationException")
        void propagaValidationException() throws Exception {
            ParametrosCrearCliente params = new ParametrosCrearCliente();
            params.setTipoDocumentoId(1L);
            params.setNumeroDocumento("12345678");
            params.setNombres("Juan");
            params.setApellidos("Pérez");
            params.setEmail("juan@mail.com");

            when(service.crearCliente(any())).thenThrow(new ValidationException("Ya existe un cliente"));

            mockMvc.perform(post("/api/clientes/crear-cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── PUT /api/clientes/actualizar-cliente/{id} ───────────────────────────

    @Nested
    @DisplayName("PUT /api/clientes/actualizar-cliente/{id}")
    class ActualizarCliente {

        @Test
        @DisplayName("retorna 200 con cliente actualizado")
        void retorna200ClienteActualizado() throws Exception {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setNombres("Pedro");

            when(service.actualizarCliente(eq(1L), any())).thenReturn(clienteBase());
            when(mapper.toDto(any(Cliente.class))).thenReturn(clienteDTOBase());

            mockMvc.perform(put("/api/clientes/actualizar-cliente/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(params)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombres").value("Juan"));

            verify(service).actualizarCliente(eq(1L), any());
        }
    }

    // ─── DELETE /api/clientes/eliminar-cliente/{id} ──────────────────────────

    @Nested
    @DisplayName("DELETE /api/clientes/eliminar-cliente/{id}")
    class EliminarCliente {

        @Test
        @DisplayName("retorna 200 al eliminar cliente")
        void retorna200AlEliminar() throws Exception {
            doNothing().when(service).eliminarCliente(1L);

            mockMvc.perform(delete("/api/clientes/eliminar-cliente/1"))
                    .andExpect(status().isOk());

            verify(service).eliminarCliente(1L);
        }
    }
}



