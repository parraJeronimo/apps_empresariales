package com.taller.bibliotecas.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de la documentacion OpenAPI / Swagger.
 * Visible en /bibliotecas/swagger-ui.html y /bibliotecas/api-docs
 * (segun server.servlet.context-path definido en application.properties)
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Sistema de Gestion de Multiples Bibliotecas API",
                version = "1.0.0",
                description = "API REST para administrar sedes, libros, ejemplares, clientes, prestamos, "
                        + "devoluciones, reservas y traslados entre sedes de una biblioteca",
                contact = @Contact(
                        name = "Sebastian Ospina - Jose Bravo",
                        email = "contacto@taller.edu.co"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080/bibliotecas", description = "Servidor de Desarrollo"),
                @Server(url = "http://localhost:8090/bibliotecas", description = "Servidor de Pruebas"),
                @Server(url = "http://localhost:8099/bibliotecas", description = "Servidor de Produccion")
        }
)
public class OpenApiConfig {
}
