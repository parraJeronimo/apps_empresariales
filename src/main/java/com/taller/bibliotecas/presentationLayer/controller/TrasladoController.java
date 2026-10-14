package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.TrasladoCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.TrasladoDTO;
import com.taller.bibliotecas.businessLayer.service.TrasladoService;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoTraslado;
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
@RequestMapping("/api/v1/traslados")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Traslados", description = "RF-18 y RF-19: traslado de ejemplares entre sedes")
public class TrasladoController {

    private final TrasladoService trasladoService;

    @PostMapping
    @Operation(summary = "RF-18: Registrar traslado de un ejemplar", description = "El ejemplar debe estar disponible; queda EN_TRASLADO")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Traslado registrado"),
            @ApiResponse(responseCode = "400", description = "Ejemplar no disponible, misma sede o sede destino desactivada"),
            @ApiResponse(responseCode = "404", description = "Ejemplar o sede inexistente")
    })
    public ResponseEntity<TrasladoDTO> crear(@Valid @RequestBody TrasladoCreateDTO createDTO) {
        log.info("POST /api/v1/traslados - ejemplarId={}", createDTO.getEjemplarId());
        return ResponseEntity.status(HttpStatus.CREATED).body(trasladoService.createTraslado(createDTO));
    }

    @GetMapping
    @Operation(summary = "Listar todos los traslados")
    public ResponseEntity<List<TrasladoDTO>> listar() {
        return ResponseEntity.ok(trasladoService.getAllTraslados());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener traslado por ID")
    public ResponseEntity<TrasladoDTO> obtenerPorId(
            @Parameter(description = "ID del traslado", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(trasladoService.getTrasladoById(id));
    }

    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "Traslados de una sede (como origen o destino)", description = "Filtro opcional por estado, ej. EN_TRANSITO")
    public ResponseEntity<List<TrasladoDTO>> listarPorSede(
            @Parameter(description = "ID de la sede", example = "1") @PathVariable Long sedeId,
            @RequestParam(required = false) EstadoTraslado estado) {
        return ResponseEntity.ok(trasladoService.getTrasladosBySede(sedeId, estado));
    }

    @PatchMapping("/{id}/recepcion")
    @Operation(summary = "RF-19: Confirmar recepcion en la sede destino",
            description = "Actualiza automaticamente la ubicacion del ejemplar y lo deja disponible")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recepcion confirmada"),
            @ApiResponse(responseCode = "400", description = "El traslado no esta en transito"),
            @ApiResponse(responseCode = "404", description = "Traslado no encontrado")
    })
    public ResponseEntity<TrasladoDTO> confirmarRecepcion(
            @Parameter(description = "ID del traslado", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(trasladoService.confirmarRecepcion(id));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar un traslado en transito", description = "El ejemplar vuelve a estar disponible en su sede de origen")
    public ResponseEntity<TrasladoDTO> cancelar(
            @Parameter(description = "ID del traslado", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(trasladoService.cancelarTraslado(id));
    }
}
