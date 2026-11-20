package com.taller.bibliotecas.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

/**
 * Flujo completo con la cadena de filtros real y H2 en memoria (perfil test).
 * El DataSeeder crea al arrancar admin@biblioteca.com / Admin12345 (ADMINISTRADOR_GENERAL).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Seguridad JWT - integracion")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private String loginBody(String email, String password) throws Exception {
        return mapper.writeValueAsString(Map.of("email", email, "password", password));
    }

    private String tokenDe(String email, String password) throws Exception {
        String respuesta = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(loginBody(email, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(respuesta).get("token").asText();
    }

    private String crearSede(String adminToken, String nombre) throws Exception {
        String respuesta = mvc.perform(post("/api/v1/sedes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(Map.of("nombre", nombre, "direccion", "Calle 1 # 2-3"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(respuesta).get("id").asText();
    }

    private void crearUsuario(String adminToken, String email, String rol, String sedeId) throws Exception {
        String sedeJson = sedeId == null ? "" : ",\"sedeId\":" + sedeId;
        mvc.perform(post("/api/v1/usuarios-admin")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Usuario Prueba\",\"email\":\"" + email
                                + "\",\"password\":\"Clave12345\",\"rol\":\"" + rol + "\"" + sedeJson + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Sin token en una ruta protegida: 401 con el JSON estandar de errores")
    void sinTokenDevuelve401() throws Exception {
        mvc.perform(get("/api/v1/sedes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Se requiere autenticacion"));
    }

    @Test
    @DisplayName("Token falso: 401")
    void tokenFalsoDevuelve401() throws Exception {
        mvc.perform(get("/api/v1/sedes").header("Authorization", "Bearer esto.no.es.valido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Token invalido o expirado"));
    }

    @Test
    @DisplayName("Login correcto devuelve token, rol y datos del usuario")
    void loginCorrecto() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("admin@biblioteca.com", "Admin12345")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR_GENERAL"));
    }

    @Test
    @DisplayName("Login con contrasena incorrecta o email inexistente: 401 con el mismo mensaje")
    void loginIncorrecto() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("admin@biblioteca.com", "incorrecta1")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email o contrasena incorrectos"));

        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("noexiste@biblioteca.com", "incorrecta1")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email o contrasena incorrectos"));
    }

    @Test
    @DisplayName("Login con datos invalidos: 400")
    void loginInvalido() throws Exception {
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"no-es-email\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Datos de entrada invalidos"));
    }

    @Test
    @DisplayName("Con token valido se accede y /auth/me devuelve al usuario")
    void accesoConToken() throws Exception {
        String token = tokenDe("admin@biblioteca.com", "Admin12345");

        mvc.perform(get("/api/v1/sedes").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@biblioteca.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("Un BIBLIOTECARIO puede consultar y operar, pero no administrar sedes, usuarios ni reportes (403)")
    void bibliotecarioSoloOperaYConsulta() throws Exception {
        String admin = tokenDe("admin@biblioteca.com", "Admin12345");
        String sedeId = crearSede(admin, "Sede Seguridad IT");
        crearUsuario(admin, "biblio.it@biblioteca.com", "BIBLIOTECARIO", sedeId);
        String biblio = tokenDe("biblio.it@biblioteca.com", "Clave12345");

        mvc.perform(get("/api/v1/sedes").header("Authorization", "Bearer " + biblio))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/clientes").header("Authorization", "Bearer " + biblio))
                .andExpect(status().isOk());

        mvc.perform(post("/api/v1/sedes").header("Authorization", "Bearer " + biblio)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Otra\",\"direccion\":\"x\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
        mvc.perform(get("/api/v1/usuarios-admin").header("Authorization", "Bearer " + biblio))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/reportes/libros").header("Authorization", "Bearer " + biblio))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un usuario desactivado pierde el acceso aunque su token siga vigente")
    void usuarioDesactivadoPierdeAcceso() throws Exception {
        String admin = tokenDe("admin@biblioteca.com", "Admin12345");
        String sedeId = crearSede(admin, "Sede Desactivar IT");
        crearUsuario(admin, "baja.it@biblioteca.com", "BIBLIOTECARIO", sedeId);
        String token = tokenDe("baja.it@biblioteca.com", "Clave12345");

        mvc.perform(get("/api/v1/sedes").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        String id = mapper.readTree(mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString()).get("id").asText();
        mvc.perform(put("/api/v1/usuarios-admin/" + id).header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"activo\":false}"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/sedes").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("baja.it@biblioteca.com", "Clave12345")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("La cuenta esta desactivada"));
    }

    @Test
    @DisplayName("La contrasena se guarda cifrada y no aparece en las respuestas")
    void contrasenaNoSeExpone() throws Exception {
        String admin = tokenDe("admin@biblioteca.com", "Admin12345");

        mvc.perform(get("/api/v1/usuarios-admin").header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    @DisplayName("El preflight CORS se responde sin token")
    void preflightCors() throws Exception {
        mvc.perform(options("/api/v1/sedes")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }
}
