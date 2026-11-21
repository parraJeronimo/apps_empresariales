package com.taller.bibliotecas.presentationLayer;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

/**
 * Listados base con paginacion opcional: el cuerpo siempre es un arreglo JSON; solo se pagina si llegan page y/o size,
 * y en ese caso la informacion de paginas viaja en headers. Usa H2 (perfil test) y el admin que crea el DataSeeder.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Listados base - paginacion opcional por headers")
class PaginacionIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private String auth;

    @BeforeEach
    void login() throws Exception {
        String respuesta = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                Map.of("email", "admin@biblioteca.com", "password", "Admin12345"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        auth = "Bearer " + mapper.readTree(respuesta).get("token").asText();
    }

    @ParameterizedTest
    @ValueSource(strings = {"sedes", "categorias", "libros", "ejemplares", "clientes", "prestamos",
            "reservas", "traslados", "usuarios-admin"})
    @DisplayName("Sin page ni size: arreglo completo y sin headers de paginacion (comportamiento de siempre)")
    void sinParametrosDevuelveArregloSinHeaders(String recurso) throws Exception {
        mvc.perform(get("/api/v1/" + recurso).header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(header().doesNotExist("X-Total-Count"))
                .andExpect(header().doesNotExist("X-Total-Pages"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"sedes", "categorias", "libros", "ejemplares", "clientes", "prestamos",
            "reservas", "traslados", "usuarios-admin"})
    @DisplayName("Con page y size: el cuerpo sigue siendo un arreglo y los datos viajan en headers")
    void conParametrosPaginaPorHeaders(String recurso) throws Exception {
        mvc.perform(get("/api/v1/" + recurso).param("page", "0").param("size", "1").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.lessThanOrEqualTo(1)))
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(header().exists("X-Total-Pages"))
                .andExpect(header().string("X-Page", "0"))
                .andExpect(header().string("X-Page-Size", "1"));
    }

    @Test
    @DisplayName("Dos paginas consecutivas no repiten elementos y el total es coherente")
    void paginasConsecutivasNoSeRepiten() throws Exception {
        long sufijo = System.nanoTime();
        for (int i = 0; i < 3; i++) {
            mvc.perform(post("/api/v1/sedes").header("Authorization", auth)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(
                                    Map.of("nombre", "Sede paginacion " + sufijo + "-" + i, "direccion", "Calle " + i))))
                    .andExpect(status().isCreated());
        }

        String p0 = mvc.perform(get("/api/v1/sedes").param("page", "0").param("size", "2").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(header().string("X-Total-Count", greaterThanOrEqualTo("3")))
                .andReturn().getResponse().getContentAsString();
        String p1 = mvc.perform(get("/api/v1/sedes").param("page", "1").param("size", "2").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Page", "1"))
                .andReturn().getResponse().getContentAsString();

        JsonNode a = mapper.readTree(p0);
        JsonNode b = mapper.readTree(p1);
        assertTrue(b.size() >= 1);
        assertNotEquals(a.get(0).get("id").asLong(), b.get(0).get("id").asLong());
        assertTrue(a.get(1).get("id").asLong() < b.get(0).get("id").asLong(), "orden ascendente por id");
    }

    @Test
    @DisplayName("Solo size: pagina 0; solo page: tamano por defecto (20)")
    void soloUnParametroUsaValoresPorDefecto() throws Exception {
        mvc.perform(get("/api/v1/sedes").param("size", "5").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Page", "0"))
                .andExpect(header().string("X-Page-Size", "5"));
        mvc.perform(get("/api/v1/sedes").param("page", "0").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Page-Size", "20"));
    }

    @Test
    @DisplayName("Pagina fuera de rango: 200 con arreglo vacio y headers")
    void paginaFueraDeRangoDevuelveVacio() throws Exception {
        mvc.perform(get("/api/v1/sedes").param("page", "9999").param("size", "10").header("Authorization", auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andExpect(header().exists("X-Total-Count"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"page=-1", "size=0", "size=101", "page=abc", "size=xyz"})
    @DisplayName("Parametros invalidos: 400 con el JSON estandar de errores")
    void parametrosInvalidosDevuelven400(String query) throws Exception {
        mvc.perform(get("/api/v1/sedes?" + query).header("Authorization", auth))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("La paginacion no salta la seguridad: sin token sigue siendo 401")
    void sinTokenSigue401() throws Exception {
        mvc.perform(get("/api/v1/sedes").param("page", "0").param("size", "5"))
                .andExpect(status().isUnauthorized());
    }
}
