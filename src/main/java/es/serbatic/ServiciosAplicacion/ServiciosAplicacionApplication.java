package es.serbatic.ServiciosAplicacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// La anotacion @SpringBootApplication indica que esta es una aplicacion Spring Boot y permite la configuracion automatica de Spring
@SpringBootApplication
public class ServiciosAplicacionApplication {

	// El metodo main es el punto de entrada de la aplicacion
	// SpringApplication.run inicia la aplicacion Spring Boot y carga el contexto de la aplicacion
	public static void main(String[] args) {
		SpringApplication.run(ServiciosAplicacionApplication.class, args);
	}

}
