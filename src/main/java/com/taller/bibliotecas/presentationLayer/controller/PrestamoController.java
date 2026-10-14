package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.DevolucionDTO;
import com.taller.bibliotecas.businessLayer.dto.FiltroPrestamo;
import com.taller.bibliotecas.businessLayer.dto.PrestamoCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoDTO;
import com.taller.bibliotecas.businessLayer.service.PrestamoService;
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
@RequestMapping("/api/v1/prestamos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Prestamos y devoluciones", description = "RF-11 a RF-17 y RF-25: prestamos, devoluciones (incluso en otra sede) e historial")
public class PrestamoController {

    private final PrestamoService prestamoService;

    @PostMapping
    @Operation(summary = "RF-11/RF-12: Registrar prestamo",
            description = "Fija fecha de inicio (hoy) y fecha limite (14 dias por defecto, maximo 30). El ejemplar debe estar disponible en la sede indicada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Prestamo registrado"),
            @ApiResponse(responseCode = "400", description = "Ejemplar no disponible, en otra sede o sede desactivada"),
            @ApiResponse(responseCode = "404", description = "Ejemplar, cliente o sede inexistente")
    })
    public ResponseEntity<PrestamoDTO> crear(@Valid @RequestBody PrestamoCreateDTO createDTO) {
        log.info("POST /api/v1/prestamos - ejemplarId={}, clienteId={}", createDTO.getEjemplarId(), createDTO.getClienteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.createPrestamo(createDTO));
    }

    @GetMapping
    @Operation(summary = "Listar todos los prestamos")
    public ResponseEntity<List<PrestamoDTO>> listar() {
        return ResponseEntity.ok(prestamoService.getAllPrestamos());
    }

    @GetMapping("/vencidos")
    @Operation(summary = "Listar prestamos vencidos de todas las sedes")
    public ResponseEntity<List<PrestamoDTO>> listarVencidos() {
        return ResponseEntity.ok(prestamoService.getPrestamosVencidos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener prestamo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prestamo encontrado"),
            @ApiResponse(responseCode = "404", description = "Prestamo no encontrado")
    })
    public ResponseEntity<PrestamoDTO> obtenerPorId(
            @Parameter(description = "ID del prestamo", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.getPrestamoById(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "RF-13: Prestamos de un cliente",
            description = "Filtro opcional: ACTIVOS (vigentes), VENCIDOS o FINALIZADOS. Sin filtro devuelve todos")
    public ResponseEntity<List<PrestamoDTO>> listarPorCliente(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long clienteId,
            @RequestParam(required = false) FiltroPrestamo filtro) {
        return ResponseEntity.ok(prestamoService.getPrestamosByCliente(clienteId, filtro));
    }

    @GetMapping("/cliente/{clienteId}/historial")
    @Operation(summary = "RF-25: Historial de prestamos y devoluciones de un cliente")
    public ResponseEntity<List<PrestamoDTO>> historial(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long clienteId) {
        return ResponseEntity.ok(prestamoService.getHistorialByCliente(clienteId));
    }

    @PostMapping("/{id}/devolucion")
    @Operation(summary = "RF-14 a RF-17: Registrar devolucion",
            description = "Se puede devolver en una sede distinta a la del prestamo. Calcula si hubo retraso y registra la sede de devolucion")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Devolucion registrada"),
            @ApiResponse(responseCode = "400", description = "Prestamo ya devuelto o sede desactivada"),
            @ApiResponse(responseCode = "404", description = "Prestamo o sede inexistente")
    })
    public ResponseEntity<PrestamoDTO> devolver(
            @Parameter(description = "ID del prestamo", example = "1") @PathVariable Long id,
            @Valid @RequestBody DevolucionDTO devolucionDTO) {
        return ResponseEntity.ok(prestamoService.registrarDevolucion(id, devolucionDTO));
    }
}
