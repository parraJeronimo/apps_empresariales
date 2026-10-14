package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.ReservaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaRechazoDTO;
import com.taller.bibliotecas.businessLayer.service.ReservaService;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoReserva;
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
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservas", description = "RF-21 y RF-22: reservas de libros sin disponibilidad y gestion de solicitudes por sede")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(summary = "RF-21: Reservar un libro no disponible",
            description = "Solo se permite si no hay ejemplares disponibles del libro en la sede solicitada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada en estado PENDIENTE"),
            @ApiResponse(responseCode = "400", description = "Hay ejemplares disponibles, solicitud duplicada o sede desactivada"),
            @ApiResponse(responseCode = "404", description = "Cliente, libro o sede inexistente")
    })
    public ResponseEntity<ReservaDTO> crear(@Valid @RequestBody ReservaCreateDTO createDTO) {
        log.info("POST /api/v1/reservas - libroId={}, clienteId={}", createDTO.getLibroId(), createDTO.getClienteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.createReserva(createDTO));
    }

    @GetMapping
    @Operation(summary = "RF-22: Listar todas las reservas")
    public ResponseEntity<List<ReservaDTO>> listar() {
        return ResponseEntity.ok(reservaService.getAllReservas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "RF-22: Consultar reserva por ID")
    public ResponseEntity<ReservaDTO> obtenerPorId(
            @Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.getReservaById(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "RF-22: Reservas de un cliente")
    public ResponseEntity<List<ReservaDTO>> listarPorCliente(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.getReservasByCliente(clienteId));
    }

    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "Solicitudes recibidas por una sede",
            description = "Lista para los administradores de la sede. Filtro opcional por estado (ej. PENDIENTE)")
    public ResponseEntity<List<ReservaDTO>> listarPorSede(
            @Parameter(description = "ID de la sede", example = "1") @PathVariable Long sedeId,
            @RequestParam(required = false) EstadoReserva estado) {
        return ResponseEntity.ok(reservaService.getReservasBySede(sedeId, estado));
    }

    @PatchMapping("/{id}/aprobar")
    @Operation(summary = "RF-22: Aprobar una solicitud pendiente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva aprobada"),
            @ApiResponse(responseCode = "400", description = "La reserva no esta pendiente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaDTO> aprobar(
            @Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.aprobarReserva(id));
    }

    @PatchMapping("/{id}/rechazar")
    @Operation(summary = "RF-22: Rechazar una solicitud pendiente indicando el motivo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva rechazada"),
            @ApiResponse(responseCode = "400", description = "Falta el motivo o la reserva no esta pendiente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaDTO> rechazar(
            @Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id,
            @Valid @RequestBody ReservaRechazoDTO rechazoDTO) {
        return ResponseEntity.ok(reservaService.rechazarReserva(id, rechazoDTO));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "RF-22: Cancelar una reserva (la hace el cliente)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada"),
            @ApiResponse(responseCode = "400", description = "La reserva ya fue rechazada o cancelada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaDTO> cancelar(
            @Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }
}
