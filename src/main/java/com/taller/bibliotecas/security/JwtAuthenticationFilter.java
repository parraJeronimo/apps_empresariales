package com.taller.bibliotecas.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Se ejecuta una vez por peticion: si llega un token valido, le dice a Spring Security quien es el usuario.
 * No decide permisos ni responde errores (eso lo hacen SecurityConfig, el EntryPoint y el AccessDeniedHandler).
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    static final String JWT_ERROR_ATTRIBUTE = "jwt_error";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);   // sin token: no se rechaza aqui, decide SecurityConfig
            return;
        }

        final String token = authHeader.substring(BEARER_PREFIX.length());
        try {
            final String email = jwtService.extractUsername(token);   // valida la firma y la expiracion

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Se consulta la BD en cada peticion: si un usuario se desactiva, su token deja de servir de inmediato
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(token, userDetails) && userDetails.isEnabled()) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    request.setAttribute(JWT_ERROR_ATTRIBUTE, "Token invalido o usuario inactivo");
                }
            }
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            // Token falso, alterado o expirado, o usuario borrado: no se autentica y el EntryPoint responde 401
            request.setAttribute(JWT_ERROR_ATTRIBUTE, e.getMessage());
        }

        filterChain.doFilter(request, response);   // siempre se continua la cadena
    }

    /** El login es publico: un token viejo enviado por error no debe interferir. */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return request.getServletPath().equals("/api/v1/auth/login");
    }
}
