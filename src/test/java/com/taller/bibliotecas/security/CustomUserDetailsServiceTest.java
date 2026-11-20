package com.taller.bibliotecas.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.taller.bibliotecas.businessLayer.dto.UsuarioAuthDTO;
import com.taller.bibliotecas.persistenceLayer.dao.UsuarioAdminDAO;
import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService - Unit Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioAdminDAO usuarioAdminDAO;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void cargaUsuarioConRolConPrefijoROLE() {
        when(usuarioAdminDAO.findAuthByEmail("maria@biblioteca.com")).thenReturn(Optional.of(
                new UsuarioAuthDTO(2L, "Maria", "maria@biblioteca.com", "$2a$10$hash",
                        RolUsuario.ADMINISTRADOR_SEDE, 1L, true)));

        UserDetails details = service.loadUserByUsername("maria@biblioteca.com");

        assertEquals("maria@biblioteca.com", details.getUsername());
        assertEquals("$2a$10$hash", details.getPassword());
        assertTrue(details.isEnabled());
        // sin el prefijo ROLE_ hasRole("ADMINISTRADOR_SEDE") daria 403 aunque el usuario tenga el rol
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR_SEDE")));
    }

    @Test
    void usuarioInactivoQuedaDeshabilitado() {
        when(usuarioAdminDAO.findAuthByEmail("baja@biblioteca.com")).thenReturn(Optional.of(
                new UsuarioAuthDTO(3L, "Baja", "baja@biblioteca.com", "$2a$10$hash",
                        RolUsuario.BIBLIOTECARIO, 1L, false)));

        assertFalse(service.loadUserByUsername("baja@biblioteca.com").isEnabled());
    }

    @Test
    void usuarioSinContrasenaNoPuedeAutenticarse() {
        when(usuarioAdminDAO.findAuthByEmail("viejo@biblioteca.com")).thenReturn(Optional.of(
                new UsuarioAuthDTO(4L, "Viejo", "viejo@biblioteca.com", null,
                        RolUsuario.BIBLIOTECARIO, 1L, true)));

        assertEquals("", service.loadUserByUsername("viejo@biblioteca.com").getPassword());
    }

    @Test
    void usuarioInexistente() {
        when(usuarioAdminDAO.findAuthByEmail("nadie@biblioteca.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nadie@biblioteca.com"));
    }
}
