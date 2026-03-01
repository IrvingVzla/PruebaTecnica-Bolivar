package com.pruebatecnica.microservicio_polizas;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de integración del contexto de Spring.
 * Deshabilitado en entornos sin SQL Server; habilitar cuando la BD esté disponible.
 */
@SpringBootTest
@Disabled("Requiere SQL Server disponible. Habilitar en entornos con BD configurada.")
class MicroservicioPolizasApplicationTests {

    @Test
    void contextLoads() {
    }
}
