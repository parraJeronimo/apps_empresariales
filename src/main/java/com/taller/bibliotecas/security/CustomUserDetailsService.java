package com.taller.bibliotecas.security;

import com.taller.bibliotecas.businessLayer.dto.UsuarioAuthDTO;
import com.taller.bibliotecas.persistenceLayer.dao.UsuarioAdminDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/** Adaptador entre la capa de persistencia (UsuarioAdminDAO) y Spring Security (UserDetails). */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioAdminDAO usuarioAdminDAO;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioAuthDTO usuario = usuarioAdminDAO.findAuthByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Un usuario sin contrasena (creado antes de agregar la seguridad) no puede iniciar sesion
        String hash = usuario.getPassword() != null ? usuario.getPassword() : "";

        return User.builder()
                .username(usuario.getEmail())
                .password(hash)
                // Un rol es una authority con prefijo ROLE_: hasRole("ADMINISTRADOR_GENERAL") busca ROLE_ADMINISTRADOR_GENERAL
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())))
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}
