package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.UsuarioAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios-admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios administrativos", description = "RF-23: usuarios administrativos y asignacion de roles")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;

    @PostMapping
    @Operation(summary = "RF-23: Registrar usuario administrativo",
            description = "Todos los roles, salvo ADMINISTRADOR_GENERAL, deben indicar la sede a la que pertenecen")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos, email repetido o falta la sede"),
            @ApiResponse(responseCode = "404", description = "La sede indicada no existe")
    })
    public ResponseEntity<UsuarioAdminDTO> crear(@Valid @RequestBody UsuarioAdminCreateDTO createDTO) {
        log.info("POST /api/v1/usuarios-admin - {}", createDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioAdminService.createUsuario(createDTO));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios administrativos")
    public ResponseEntity<List<UsuarioAdminDTO>> listar() {
        return ResponseEntity.ok(usuarioAdminService.getAllUsuarios());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario administrativo por ID")
    public ResponseEntity<UsuarioAdminDTO> obtenerPorId(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(usuarioAdminService.getUsuarioById(id));
    }

    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "Listar usuarios administrativos de una sede")
    public ResponseEntity<List<UsuarioAdminDTO>> listarPorSede(
            @Parameter(description = "ID de la sede", example = "1") @PathVariable Long sedeId) {
        return ResponseEntity.ok(usuarioAdminService.getUsuariosBySede(sedeId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "RF-23: Actualizar usuario, cambiar rol/sede o activarlo/desactivarlo")
    public ResponseEntity<UsuarioAdminDTO> actualizar(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long id,
            @Valid @RequestBody UsuarioAdminUpdateDTO updateDTO) {
        return ResponseEntity.ok(usuarioAdminService.updateUsuario(id, updateDTO));
    }
}
