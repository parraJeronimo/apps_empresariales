package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.EjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarDTO;
import com.taller.bibliotecas.businessLayer.service.EjemplarService;
import com.taller.bibliotecas.businessLayer.service.MovimientoEjemplarService;
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
@RequestMapping("/api/v1/ejemplares")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ejemplares", description = "RF-04, RF-08 y RF-20: ejemplares fisicos, ubicacion, estado e historial")
public class EjemplarController {

    private final EjemplarService ejemplarService;
    private final MovimientoEjemplarService movimientoService;

    @PostMapping
    @Operation(summary = "RF-04: Registrar ejemplar", description = "Si no se envia codigo, el sistema genera uno unico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ejemplar registrado"),
            @ApiResponse(responseCode = "400", description = "Codigo repetido o sede desactivada"),
            @ApiResponse(responseCode = "404", description = "Libro o sede inexistente")
    })
    public ResponseEntity<EjemplarDTO> crear(@Valid @RequestBody EjemplarCreateDTO createDTO) {
        log.info("POST /api/v1/ejemplares - libroId={}", createDTO.getLibroId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ejemplarService.createEjemplar(createDTO));
    }

    @GetMapping
    @Operation(summary = "RF-08: Listar ejemplares con su sede actual y estado")
    public ResponseEntity<List<EjemplarDTO>> listar() {
        return ResponseEntity.ok(ejemplarService.getAllEjemplares());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ejemplar por ID")
    public ResponseEntity<EjemplarDTO> obtenerPorId(
            @Parameter(description = "ID del ejemplar", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(ejemplarService.getEjemplarById(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener ejemplar por codigo")
    public ResponseEntity<EjemplarDTO> obtenerPorCodigo(
            @Parameter(description = "Codigo del ejemplar") @PathVariable String codigo) {
        return ResponseEntity.ok(ejemplarService.getEjemplarByCodigo(codigo));
    }

    @GetMapping("/libro/{libroId}")
    @Operation(summary = "RF-08: Ejemplares de un libro en todas las sedes")
    public ResponseEntity<List<EjemplarDTO>> listarPorLibro(
            @Parameter(description = "ID del libro", example = "1") @PathVariable Long libroId) {
        return ResponseEntity.ok(ejemplarService.getEjemplaresByLibro(libroId));
    }

    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "RF-08: Ejemplares que estan actualmente en una sede")
    public ResponseEntity<List<EjemplarDTO>> listarPorSede(
            @Parameter(description = "ID de la sede", example = "1") @PathVariable Long sedeId) {
        return ResponseEntity.ok(ejemplarService.getEjemplaresBySede(sedeId));
    }

    @GetMapping("/{id}/historial")
    @Operation(summary = "RF-20: Historial de movimientos y ubicaciones del ejemplar")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial del ejemplar"),
            @ApiResponse(responseCode = "404", description = "Ejemplar no encontrado")
    })
    public ResponseEntity<List<MovimientoEjemplarDTO>> historial(
            @Parameter(description = "ID del ejemplar", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.getHistorialByEjemplar(id));
    }
}
