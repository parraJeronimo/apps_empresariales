package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.SedeCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.SedeService;
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
@RequestMapping("/api/v1/sedes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sedes", description = "RF-01 y RF-02: registro, consulta, modificacion y desactivacion de sedes")
public class SedeController {

    private final SedeService sedeService;

    @PostMapping
    @Operation(summary = "RF-01: Registrar sede", description = "Registra una nueva sede. El nombre debe ser unico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sede registrada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o nombre repetido")
    })
    public ResponseEntity<SedeDTO> crear(@Valid @RequestBody SedeCreateDTO createDTO) {
        log.info("POST /api/v1/sedes - {}", createDTO.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(sedeService.createSede(createDTO));
    }

    @GetMapping
    @Operation(summary = "RF-02: Listar sedes", description = "Retorna todas las sedes, activas e inactivas")
    public ResponseEntity<List<SedeDTO>> listar() {
        return ResponseEntity.ok(sedeService.getAllSedes());
    }

    @GetMapping("/activas")
    @Operation(summary = "RF-02: Listar sedes activas")
    public ResponseEntity<List<SedeDTO>> listarActivas() {
        return ResponseEntity.ok(sedeService.getSedesActivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "RF-02: Consultar sede por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sede encontrada"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada")
    })
    public ResponseEntity<SedeDTO> obtenerPorId(
            @Parameter(description = "ID de la sede", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(sedeService.getSedeById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "RF-02: Modificar sede")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sede actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o nombre repetido"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada")
    })
    public ResponseEntity<SedeDTO> actualizar(
            @Parameter(description = "ID de la sede", example = "1") @PathVariable Long id,
            @Valid @RequestBody SedeUpdateDTO updateDTO) {
        return ResponseEntity.ok(sedeService.updateSede(id, updateDTO));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "RF-02: Desactivar sede", description = "No se permite si la sede tiene ejemplares prestados o en traslado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sede desactivada"),
            @ApiResponse(responseCode = "400", description = "La sede ya esta desactivada o tiene ejemplares fuera"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada")
    })
    public ResponseEntity<SedeDTO> desactivar(
            @Parameter(description = "ID de la sede", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(sedeService.desactivarSede(id));
    }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "RF-02: Reactivar sede")
    public ResponseEntity<SedeDTO> activar(
            @Parameter(description = "ID de la sede", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(sedeService.activarSede(id));
    }
}
