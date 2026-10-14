package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.ClienteCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.ClienteService;
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
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Clientes", description = "RF-09 y RF-10: registro, consulta y actualizacion de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @Operation(summary = "RF-09: Registrar cliente", description = "Documento y email deben ser unicos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente registrado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos, documento o email repetido")
    })
    public ResponseEntity<ClienteDTO> crear(@Valid @RequestBody ClienteCreateDTO createDTO) {
        log.info("POST /api/v1/clientes - {}", createDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.createCliente(createDTO));
    }

    @GetMapping
    @Operation(summary = "RF-10: Listar clientes")
    public ResponseEntity<List<ClienteDTO>> listar() {
        return ResponseEntity.ok(clienteService.getAllClientes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "RF-10: Consultar cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ClienteDTO> obtenerPorId(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(clienteService.getClienteById(id));
    }

    @GetMapping("/documento/{documento}")
    @Operation(summary = "RF-10: Consultar cliente por documento")
    public ResponseEntity<ClienteDTO> obtenerPorDocumento(
            @Parameter(description = "Documento de identidad") @PathVariable String documento) {
        return ResponseEntity.ok(clienteService.getClienteByDocumento(documento));
    }

    @PutMapping("/{id}")
    @Operation(summary = "RF-10: Actualizar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o email repetido"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ClienteDTO> actualizar(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long id,
            @Valid @RequestBody ClienteUpdateDTO updateDTO) {
        return ResponseEntity.ok(clienteService.updateCliente(id, updateDTO));
    }
}
