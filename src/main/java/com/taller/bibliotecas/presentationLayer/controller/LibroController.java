package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.LibroCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.LibroService;
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
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Libros", description = "RF-03, RF-06 y RF-07: registro, catalogo y busqueda de libros")
public class LibroController {

    private final LibroService libroService;

    @PostMapping
    @Operation(summary = "RF-03: Registrar libro", description = "El ISBN debe ser unico")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Libro registrado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o ISBN repetido"),
            @ApiResponse(responseCode = "404", description = "La categoria indicada no existe")
    })
    public ResponseEntity<LibroDTO> crear(@Valid @RequestBody LibroCreateDTO createDTO) {
        log.info("POST /api/v1/libros - {}", createDTO.getTitulo());
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.createLibro(createDTO));
    }

    @GetMapping
    @Operation(summary = "Listar todos los libros registrados")
    public ResponseEntity<List<LibroDTO>> listar() {
        return ResponseEntity.ok(libroService.getAllLibros());
    }

    @GetMapping("/catalogo")
    @Operation(summary = "RF-06: Catalogo general", description = "Libros con al menos un ejemplar disponible en cualquier sede")
    public ResponseEntity<List<LibroDTO>> catalogo() {
        return ResponseEntity.ok(libroService.getCatalogo());
    }

    @GetMapping("/buscar")
    @Operation(summary = "RF-07: Buscar libros",
            description = "Filtros opcionales combinables: titulo, autor, ISBN, categoria (incluye subcategorias) y codigo de ejemplar")
    public ResponseEntity<List<LibroDTO>> buscar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String codigoEjemplar) {
        return ResponseEntity.ok(libroService.buscarLibros(titulo, autor, isbn, categoriaId, codigoEjemplar));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener libro por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro encontrado"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    public ResponseEntity<LibroDTO> obtenerPorId(
            @Parameter(description = "ID del libro", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(libroService.getLibroById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar libro", description = "Permite ademas reclasificarlo enviando categoriaId")
    public ResponseEntity<LibroDTO> actualizar(
            @Parameter(description = "ID del libro", example = "1") @PathVariable Long id,
            @Valid @RequestBody LibroUpdateDTO updateDTO) {
        return ResponseEntity.ok(libroService.updateLibro(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar libro", description = "Solo si no tiene ejemplares registrados")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Libro eliminado"),
            @ApiResponse(responseCode = "400", description = "El libro tiene ejemplares"),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del libro", example = "1") @PathVariable Long id) {
        libroService.deleteLibro(id);
        return ResponseEntity.noContent().build();
    }
}
