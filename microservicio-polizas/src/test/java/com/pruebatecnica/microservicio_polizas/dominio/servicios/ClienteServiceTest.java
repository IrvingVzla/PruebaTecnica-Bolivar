package com.pruebatecnica.microservicio_polizas.dominio.servicios;

import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.ClienteRepository;
import com.pruebatecnica.microservicio_polizas.acceso_datos.datos.repositorios.TiposDocumentoRepository;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.Cliente;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.base_datos.TiposDocumento;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosActualizarCliente;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosConsultarClientes;
import com.pruebatecnica.microservicio_polizas.dominio.entidades.personalizadas.cliente.ParametrosCrearCliente;
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
 * Pruebas unitarias para {@link ClienteService}.
 * Se utiliza Mockito puro; no se levanta contexto de Spring.
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TiposDocumentoRepository tiposDocumentoRepository;

    @InjectMocks
    private ClienteService clienteService;

    // ─── helpers de fixtures ────────────────────────────────────────────────

    private TiposDocumento tipoDoc() {
        TiposDocumento td = new TiposDocumento();
        td.setId(1);
        td.setCodigo("CC");
        td.setDescripcion("Cédula de Ciudadanía");
        td.setActivo(true);
        return td;
    }

    private Cliente clienteBase() {
        Cliente c = new Cliente();
        c.setTipoDocumento(tipoDoc());
        c.setNumeroDocumento("12345678");
        c.setNombres("Juan");
        c.setApellidos("Pérez");
        c.setEmail("juan@mail.com");
        c.setTelefono("3001234567");
        c.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        return c;
    }

    private ParametrosCrearCliente parametrosCrear() {
        ParametrosCrearCliente p = new ParametrosCrearCliente();
        p.setTipoDocumentoId(1L);
        p.setNumeroDocumento("12345678");
        p.setNombres("Juan");
        p.setApellidos("Pérez");
        p.setEmail("juan@mail.com");
        p.setTelefono("3001234567");
        p.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // consultarClientes
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("consultarClientes")
    class ConsultarClientes {

        @Test
        @DisplayName("retorna lista vacía cuando no hay coincidencias")
        void retornaListaVacia() {
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of());
            List<Cliente> resultado = clienteService.consultarClientes(new ParametrosConsultarClientes());
            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("retorna todos los clientes cuando parámetros es null")
        void retornaListaConParametrosNull() {
            Cliente c = clienteBase();
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(c));
            List<Cliente> resultado = clienteService.consultarClientes(null);
            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("filtra correctamente por id")
        void filtraPorId() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setId(1L);
            Cliente c = clienteBase();
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(c));
            List<Cliente> resultado = clienteService.consultarClientes(params);
            assertThat(resultado).hasSize(1);
            verify(clienteRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("filtra correctamente por nombre")
        void filtraPorNombre() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setNombres("Juan");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por tipoDocumentoId")
        void filtraPorTipoDocumentoId() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setTipoDocumentoId(1L);
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por numeroDocumento")
        void filtraPorNumeroDocumento() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setNumeroDocumento("12345678");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("no filtra por numeroDocumento cuando está en blanco")
        void noFiltraPorNumeroDocumentoBlanco() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setNumeroDocumento("  ");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por apellidos")
        void filtraPorApellidos() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setApellidos("Pérez");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("no filtra por apellidos cuando está en blanco")
        void noFiltraPorApellidosBlanco() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setApellidos("  ");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por email")
        void filtraPorEmail() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setEmail("juan@mail.com");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("no filtra por email cuando está en blanco")
        void noFiltraPorEmailBlanco() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setEmail("  ");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por telefono")
        void filtraPorTelefono() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setTelefono("3001234567");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("no filtra por telefono cuando está en blanco")
        void noFiltraPorTelefonoBlanco() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setTelefono("  ");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("no filtra por nombres cuando está en blanco")
        void noFiltraPorNombresBlanco() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setNombres("  ");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }

        @Test
        @DisplayName("filtra por todos los parámetros combinados")
        void filtraPorTodosLosParametros() {
            ParametrosConsultarClientes params = new ParametrosConsultarClientes();
            params.setId(1L);
            params.setTipoDocumentoId(1L);
            params.setNumeroDocumento("12345678");
            params.setNombres("Juan");
            params.setApellidos("Pérez");
            params.setEmail("juan@mail.com");
            params.setTelefono("3001234567");
            when(clienteRepository.findAll(any(Specification.class))).thenReturn(List.of(clienteBase()));
            assertThat(clienteService.consultarClientes(params)).hasSize(1);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // crearCliente
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("crearCliente")
    class CrearCliente {

        @Test
        @DisplayName("crea cliente exitosamente con datos válidos")
        void creaClienteExitosamente() {
            ParametrosCrearCliente params = parametrosCrear();
            Cliente guardado = clienteBase();

            when(tiposDocumentoRepository.findById(1)).thenReturn(Optional.of(tipoDoc()));
            when(clienteRepository.existsByTipoDocumento_IdAndNumeroDocumento(1, "12345678")).thenReturn(false);
            when(clienteRepository.findByTipoDocumentoIdAndNumeroDocumento(1, "12345678")).thenReturn(Optional.empty());
            when(clienteRepository.save(any(Cliente.class))).thenReturn(guardado);

            Cliente resultado = clienteService.crearCliente(params);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombres()).isEqualTo("Juan");
            verify(clienteRepository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("lanza ValidationException cuando parámetros son null")
        void lanzaExcepcionConParametrosNull() {
            assertThatThrownBy(() -> clienteService.crearCliente(null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("lanza ValidationException cuando tipoDocumentoId es null")
        void lanzaExcepcionTipoDocumentoNull() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setTipoDocumentoId(null);
            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tipoDocumentoId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando email es inválido")
        void lanzaExcepcionEmailInvalido() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setEmail("no-es-email");
            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("lanza ValidationException cuando fecha de nacimiento es futura")
        void lanzaExcepcionFechaFutura() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setFechaNacimiento(LocalDate.now().plusYears(1));
            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("fechaNacimiento");
        }

        @Test
        @DisplayName("lanza ValidationException cuando el documento ya existe")
        void lanzaExcepcionDocumentoDuplicado() {
            ParametrosCrearCliente params = parametrosCrear();
            when(tiposDocumentoRepository.findById(1)).thenReturn(Optional.of(tipoDoc()));
            when(clienteRepository.existsByTipoDocumento_IdAndNumeroDocumento(1, "12345678")).thenReturn(true);

            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Ya existe un cliente");
        }

        @Test
        @DisplayName("lanza RuntimeException cuando tipo de documento no existe en BD")
        void lanzaExcepcionTipoDocumentoNoEncontrado() {
            ParametrosCrearCliente params = parametrosCrear();
            when(tiposDocumentoRepository.findById(1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Tipo de documento no encontrado");
        }

        @Test
        @DisplayName("lanza ValidationException cuando nombres está en blanco")
        void lanzaExcepcionNombresBlanco() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setNombres("  ");
            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("nombres");
        }

        @Test
        @DisplayName("lanza ValidationException cuando tipoDocumentoId es negativo")
        void lanzaExcepcionTipoDocumentoNegativo() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setTipoDocumentoId(-1L);
            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tipoDocumentoId");
        }

        @Test
        @DisplayName("lanza ValidationException cuando numeroDocumento es null")
        void lanzaExcepcionNumeroDocumentoNull() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setNumeroDocumento(null);
            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("numeroDocumento");
        }

        @Test
        @DisplayName("lanza ValidationException cuando numeroDocumento está en blanco")
        void lanzaExcepcionNumeroDocumentoBlanco() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setNumeroDocumento("  ");
            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("numeroDocumento");
        }

        @Test
        @DisplayName("lanza ValidationException cuando apellidos es null")
        void lanzaExcepcionApellidosNull() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setApellidos(null);
            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("apellidos");
        }

        @Test
        @DisplayName("lanza ValidationException cuando email es null")
        void lanzaExcepcionEmailNull() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setEmail(null);
            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("lanza ValidationException cuando telefono está en blanco")
        void lanzaExcepcionTelefonoBlanco() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setTelefono("  ");
            assertThatThrownBy(() -> clienteService.crearCliente(params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("telefono");
        }

        @Test
        @DisplayName("crea cliente sin telefono exitosamente")
        void creaClienteSinTelefono() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setTelefono(null);
            Cliente guardado = clienteBase();
            when(tiposDocumentoRepository.findById(1)).thenReturn(Optional.of(tipoDoc()));
            when(clienteRepository.existsByTipoDocumento_IdAndNumeroDocumento(1, "12345678")).thenReturn(false);
            when(clienteRepository.findByTipoDocumentoIdAndNumeroDocumento(1, "12345678")).thenReturn(Optional.empty());
            when(clienteRepository.save(any(Cliente.class))).thenReturn(guardado);
            Cliente resultado = clienteService.crearCliente(params);
            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("crea cliente sin fechaNacimiento exitosamente")
        void creaClienteSinFechaNacimiento() {
            ParametrosCrearCliente params = parametrosCrear();
            params.setFechaNacimiento(null);
            Cliente guardado = clienteBase();
            when(tiposDocumentoRepository.findById(1)).thenReturn(Optional.of(tipoDoc()));
            when(clienteRepository.existsByTipoDocumento_IdAndNumeroDocumento(1, "12345678")).thenReturn(false);
            when(clienteRepository.findByTipoDocumentoIdAndNumeroDocumento(1, "12345678")).thenReturn(Optional.empty());
            when(clienteRepository.save(any(Cliente.class))).thenReturn(guardado);
            Cliente resultado = clienteService.crearCliente(params);
            assertThat(resultado).isNotNull();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // actualizarCliente
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("actualizarCliente")
    class ActualizarCliente {

        @Test
        @DisplayName("actualiza cliente exitosamente")
        void actualizaExitosamente() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setNombres("Pedro");

            Cliente existente = clienteBase();
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(existente);

            Cliente resultado = clienteService.actualizarCliente(1L, params);
            assertThat(resultado).isNotNull();
            verify(clienteRepository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("lanza ValidationException cuando id es null")
        void lanzaExcepcionIdNull() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setNombres("Pedro");
            assertThatThrownBy(() -> clienteService.actualizarCliente(null, params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("id");
        }

        @Test
        @DisplayName("lanza ValidationException cuando id es cero")
        void lanzaExcepcionIdCero() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setNombres("Pedro");
            assertThatThrownBy(() -> clienteService.actualizarCliente(0L, params))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("lanza ValidationException cuando parámetros son null")
        void lanzaExcepcionParametrosNull() {
            assertThatThrownBy(() -> clienteService.actualizarCliente(1L, null))
                    .isInstanceOf(ValidationException.class);
        }

        @Test
        @DisplayName("lanza ValidationException cuando no se provee ningún campo")
        void lanzaExcepcionSinCampos() {
            assertThatThrownBy(() -> clienteService.actualizarCliente(1L, new ParametrosActualizarCliente()))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Al menos un campo");
        }

        @Test
        @DisplayName("lanza ValidationException cuando email de actualización es inválido")
        void lanzaExcepcionEmailInvalidoActualizar() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setEmail("correo-invalido");
            assertThatThrownBy(() -> clienteService.actualizarCliente(1L, params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("lanza RuntimeException cuando el cliente no existe en BD")
        void lanzaExcepcionClienteNoEncontrado() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setNombres("Pedro");
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> clienteService.actualizarCliente(99L, params))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Cliente no encontrado");
        }

        @Test
        @DisplayName("lanza ValidationException cuando apellidos está en blanco")
        void lanzaExcepcionApellidosBlanco() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setApellidos("  ");
            assertThatThrownBy(() -> clienteService.actualizarCliente(1L, params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("apellidos");
        }

        @Test
        @DisplayName("lanza ValidationException cuando email está en blanco")
        void lanzaExcepcionEmailBlanco() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setEmail("  ");
            assertThatThrownBy(() -> clienteService.actualizarCliente(1L, params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("lanza ValidationException cuando nombres está en blanco")
        void lanzaExcepcionNombresBlanco() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setNombres("  ");
            assertThatThrownBy(() -> clienteService.actualizarCliente(1L, params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("nombres");
        }

        @Test
        @DisplayName("lanza ValidationException cuando telefono está en blanco")
        void lanzaExcepcionTelefonoBlanco() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setTelefono("  ");
            assertThatThrownBy(() -> clienteService.actualizarCliente(1L, params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("telefono");
        }

        @Test
        @DisplayName("lanza ValidationException cuando fechaNacimiento futura")
        void lanzaExcepcionFechaFutura() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setFechaNacimiento(LocalDate.now().plusYears(1));
            assertThatThrownBy(() -> clienteService.actualizarCliente(1L, params))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("fechaNacimiento");
        }

        @Test
        @DisplayName("actualiza con apellidos exitosamente")
        void actualizaApellidosExitosamente() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setApellidos("López");
            Cliente existente = clienteBase();
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(existente);
            Cliente resultado = clienteService.actualizarCliente(1L, params);
            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("actualiza con email válido exitosamente")
        void actualizaEmailExitosamente() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setEmail("nuevo@mail.com");
            Cliente existente = clienteBase();
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(existente);
            Cliente resultado = clienteService.actualizarCliente(1L, params);
            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("actualiza con telefono válido exitosamente")
        void actualizaTelefonoExitosamente() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setTelefono("3009876543");
            Cliente existente = clienteBase();
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(existente);
            Cliente resultado = clienteService.actualizarCliente(1L, params);
            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("actualiza con fechaNacimiento válida exitosamente")
        void actualizaFechaNacimientoExitosamente() {
            ParametrosActualizarCliente params = new ParametrosActualizarCliente();
            params.setFechaNacimiento(LocalDate.of(1985, 5, 10));
            Cliente existente = clienteBase();
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(existente);
            Cliente resultado = clienteService.actualizarCliente(1L, params);
            assertThat(resultado).isNotNull();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // eliminarCliente
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("eliminarCliente")
    class EliminarCliente {

        @Test
        @DisplayName("elimina cliente exitosamente")
        void eliminaExitosamente() {
            doNothing().when(clienteRepository).deleteById(1L);
            clienteService.eliminarCliente(1L);
            verify(clienteRepository).deleteById(1L);
        }

        @Test
        @DisplayName("lanza ValidationException cuando id es null")
        void lanzaExcepcionIdNull() {
            assertThatThrownBy(() -> clienteService.eliminarCliente(null))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("id");
        }

        @Test
        @DisplayName("lanza ValidationException cuando id es negativo")
        void lanzaExcepcionIdNegativo() {
            assertThatThrownBy(() -> clienteService.eliminarCliente(-1L))
                    .isInstanceOf(ValidationException.class);
        }
    }
}

