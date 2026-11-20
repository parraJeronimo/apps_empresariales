package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.AuthResponseDTO;
import com.taller.bibliotecas.businessLayer.dto.LoginRequestDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.businessLayer.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticacion", description = "RNF-01 y RNF-02: inicio de sesion con JWT y datos del usuario autenticado")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @SecurityRequirements   // en Swagger este endpoint no muestra el candado
    @Operation(summary = "RNF-01: Iniciar sesion",
            description = "Valida email y contrasena y devuelve un token JWT para enviar en el header Authorization: Bearer <token>")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesion iniciada, devuelve el token"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "401", description = "Email o contrasena incorrectos"),
            @ApiResponse(responseCode = "403", description = "La cuenta esta desactivada")
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("POST /api/v1/auth/login");
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Datos del usuario autenticado",
            description = "Devuelve el usuario dueno del token (incluye su rol y su sede)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario autenticado"),
            @ApiResponse(responseCode = "401", description = "Sin token, token invalido o expirado")
    })
    public ResponseEntity<UsuarioAdminDTO> me(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(authService.getPerfil(user.getUsername()));
    }
}
