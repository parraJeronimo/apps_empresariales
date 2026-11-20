package com.taller.bibliotecas.businessLayer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.AuthResponseDTO;
import com.taller.bibliotecas.businessLayer.dto.LoginRequestDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAuthDTO;
import com.taller.bibliotecas.businessLayer.service.impl.AuthServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.UsuarioAdminDAO;
import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Unit Tests (RNF-01)")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioAdminDAO usuarioAdminDAO;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private final UserDetails userDetails = User.withUsername("admin@biblioteca.com")
            .password("hash").roles("ADMINISTRADOR_GENERAL").build();

    private LoginRequestDTO login(String email, String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    @Test
    void testLogin_Success() {
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(usuarioAdminDAO.findAuthByEmail("admin@biblioteca.com")).thenReturn(Optional.of(
                new UsuarioAuthDTO(1L, "Administrador General", "admin@biblioteca.com", "hash",
                        RolUsuario.ADMINISTRADOR_GENERAL, null, true)));
        when(jwtService.generateToken(userDetails)).thenReturn("token.jwt.firma");
        when(jwtService.getExpirationMs()).thenReturn(3_600_000L);

        AuthResponseDTO result = authService.login(login("  Admin@Biblioteca.com ", "Admin12345"));

        assertEquals("token.jwt.firma", result.getToken());
        assertEquals("Bearer", result.getTipo());
        assertEquals(3_600_000L, result.getExpiraEnMs());
        assertEquals(1L, result.getUsuarioId());
        assertEquals("admin@biblioteca.com", result.getEmail());
        assertEquals(RolUsuario.ADMINISTRADOR_GENERAL, result.getRol());
        assertNull(result.getSedeId());

        // el email se normaliza antes de autenticar
        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("admin@biblioteca.com", captor.getValue().getPrincipal());
        assertEquals("Admin12345", captor.getValue().getCredentials());
    }

    @Test
    void testLogin_CredencialesIncorrectas() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(login("admin@biblioteca.com", "incorrecta")));

        verifyNoInteractions(jwtService, usuarioAdminDAO);
    }

    @Test
    void testLogin_CuentaDesactivada() {
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("User is disabled"));

        assertThrows(DisabledException.class,
                () -> authService.login(login("admin@biblioteca.com", "Admin12345")));

        verifyNoInteractions(jwtService);
    }

    @Test
    void testLogin_UsuarioDesaparecioDespuesDeAutenticar() {
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(usuarioAdminDAO.findAuthByEmail("admin@biblioteca.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(login("admin@biblioteca.com", "Admin12345")));

        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void testGetPerfil_Found() {
        UsuarioAdminDTO usuario = TestData.usuario(1L, RolUsuario.BIBLIOTECARIO, 2L);
        when(usuarioAdminDAO.findByEmail("usuario1@biblioteca.com")).thenReturn(Optional.of(usuario));

        assertEquals(1L, authService.getPerfil("usuario1@biblioteca.com").getId());
    }

    @Test
    void testGetPerfil_NotFound() {
        when(usuarioAdminDAO.findByEmail("nadie@biblioteca.com")).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> authService.getPerfil("nadie@biblioteca.com"));

        assertEquals("Usuario no encontrado con email: nadie@biblioteca.com", ex.getMessage());
    }
}
