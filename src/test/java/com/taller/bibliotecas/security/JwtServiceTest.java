package com.taller.bibliotecas.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JwtService - Unit Tests")
class JwtServiceTest {

    // 32 bytes en Base64, solo para pruebas
    private static final String SECRET = Base64.getEncoder().encodeToString(
            "clave-solo-para-pruebas-32-bytes".getBytes());

    private JwtService jwtService;
    private UserDetails admin;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000);   // sin Spring: constructor directo
        admin = User.withUsername("admin@biblioteca.com").password("x").roles("ADMINISTRADOR_GENERAL").build();
    }

    @Test
    @DisplayName("Genera un token de 3 partes cuyo subject es el email")
    void generaTokenConSubject() {
        String token = jwtService.generateToken(admin);

        assertThat(token.split("\\.")).hasSize(3);
        assertThat(jwtService.extractUsername(token)).isEqualTo("admin@biblioteca.com");
    }

    @Test
    @DisplayName("El token lleva los roles como claim")
    void incluyeRoles() {
        String token = jwtService.generateToken(admin);

        List<?> roles = jwtService.extractClaim(token, c -> c.get("roles", List.class));

        assertThat(roles).hasSize(1);
        assertThat(roles.get(0)).isEqualTo("ROLE_ADMINISTRADOR_GENERAL");
    }

    @Test
    @DisplayName("Un token recien creado es valido y expira en el futuro")
    void tokenValido() {
        String token = jwtService.generateToken(admin);

        assertThat(jwtService.isTokenValid(token, admin)).isTrue();
        assertThat(jwtService.extractExpiration(token)).isInTheFuture();
        assertThat(jwtService.getExpirationMs()).isEqualTo(3_600_000);
    }

    @Test
    @DisplayName("Rechaza un token con la firma alterada")
    void rechazaTokenAlterado() {
        String token = jwtService.generateToken(admin);
        String alterado = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtService.isTokenValid(alterado, admin)).isFalse();
    }

    @Test
    @DisplayName("Rechaza un token expirado")
    void rechazaTokenExpirado() {
        JwtService corto = new JwtService(SECRET, -1000);   // nace expirado
        String token = corto.generateToken(admin);

        assertThat(corto.isTokenValid(token, admin)).isFalse();
    }

    @Test
    @DisplayName("Rechaza el token de otro usuario")
    void rechazaOtroUsuario() {
        UserDetails otro = User.withUsername("otro@biblioteca.com").password("x").roles("BIBLIOTECARIO").build();
        String token = jwtService.generateToken(admin);

        assertThat(jwtService.isTokenValid(token, otro)).isFalse();
    }

    @Test
    @DisplayName("Rechaza un token firmado con otra clave")
    void rechazaOtraClave() {
        String otraClave = Base64.getEncoder().encodeToString("otra-clave-distinta-de-32-bytes!".getBytes());
        String token = new JwtService(otraClave, 3_600_000).generateToken(admin);

        assertThat(jwtService.isTokenValid(token, admin)).isFalse();
    }

    @Test
    @DisplayName("Rechaza texto que no es un JWT")
    void rechazaBasura() {
        assertThat(jwtService.isTokenValid("esto-no-es-un-jwt", admin)).isFalse();
        assertThat(jwtService.isTokenValid("", admin)).isFalse();
    }

    @Test
    @DisplayName("Una clave menor a 256 bits falla al arrancar")
    void claveDebil() {
        String corta = Base64.getEncoder().encodeToString("corta".getBytes());

        assertThrows(WeakKeyException.class, () -> new JwtService(corta, 1000));
    }
}
