package com.taller.bibliotecas.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taller.bibliotecas.presentationLayer.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/** 401: la ruta es protegida y no hay autenticacion valida (sin token, token falso o expirado). Mismo formato JSON que el resto de la API. */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        boolean huboTokenInvalido = request.getAttribute(JwtAuthenticationFilter.JWT_ERROR_ATTRIBUTE) != null;
        // Mensaje generico a proposito: no se revela si el token fue manipulado o si el usuario no existe
        String mensaje = huboTokenInvalido ? "Token invalido o expirado" : "Se requiere autenticacion";

        ErrorResponse body = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), mensaje, null);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
