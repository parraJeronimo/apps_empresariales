package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.AuthResponseDTO;
import com.taller.bibliotecas.businessLayer.dto.LoginRequestDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAuthDTO;
import com.taller.bibliotecas.businessLayer.service.AuthService;
import com.taller.bibliotecas.persistenceLayer.dao.UsuarioAdminDAO;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String TIPO_TOKEN = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final UsuarioAdminDAO usuarioAdminDAO;
    private final JwtService jwtService;

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        log.info("Intento de inicio de sesion: {}", email);

        // El AuthenticationManager busca al usuario (CustomUserDetailsService) y compara el hash (PasswordEncoder).
        // Si falla lanza BadCredentialsException (401) o DisabledException (403): no hay ningun if de contrasenas aqui.
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        UsuarioAuthDTO usuario = usuarioAdminDAO.findAuthByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        return new AuthResponseDTO(
                jwtService.generateToken(userDetails),
                TIPO_TOKEN,
                jwtService.getExpirationMs(),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getSedeId());
    }

    @Override
    public UsuarioAdminDTO getPerfil(String email) {
        return usuarioAdminDAO.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));
    }
}
