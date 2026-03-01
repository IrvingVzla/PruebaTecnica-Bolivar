package com.pruebatecnica.microservicio_polizas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del microservicio de pólizas.
 * <p>
 * Punto de entrada de la aplicación Spring Boot que gestiona la creación,
 * consulta y administración de pólizas de vida, salud y vehículo para clientes.
 * </p>
 */
@SpringBootApplication
public class MicroservicioPolizasApplication {

	/**
	 * Método principal que inicia el contexto de Spring Boot.
	 *
	 * @param args argumentos de línea de comandos pasados al arrancar la aplicación
	 */
	public static void main(String[] args) {
		SpringApplication.run(MicroservicioPolizasApplication.class, args);
	}

}
