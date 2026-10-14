package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.CategoriaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.CategoriaService;
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
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categorias", description = "RF-05: categorias y subcategorias para clasificar libros")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @Operation(summary = "RF-05: Crear categoria o subcategoria", description = "Si se envia categoriaPadreId se crea una subcategoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria creada"),
            @ApiResponse(responseCode = "404", description = "La categoria padre no existe")
    })
    public ResponseEntity<CategoriaDTO> crear(@Valid @RequestBody CategoriaCreateDTO createDTO) {
        log.info("POST /api/v1/categorias - {}", createDTO.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.createCategoria(createDTO));
    }

    @GetMapping
    @Operation(summary = "Listar todas las categorias")
    public ResponseEntity<List<CategoriaDTO>> listar() {
        return ResponseEntity.ok(categoriaService.getAllCategorias());
    }

    @GetMapping("/raiz")
    @Operation(summary = "Listar categorias de primer nivel")
    public ResponseEntity<List<CategoriaDTO>> listarRaiz() {
        return ResponseEntity.ok(categoriaService.getCategoriasRaiz());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoria por ID")
    public ResponseEntity<CategoriaDTO> obtenerPorId(
            @Parameter(description = "ID de la categoria", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.getCategoriaById(id));
    }

    @GetMapping("/{id}/subcategorias")
    @Operation(summary = "Listar subcategorias directas de una categoria")
    public ResponseEntity<List<CategoriaDTO>> listarSubcategorias(
            @Parameter(description = "ID de la categoria padre", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.getSubcategorias(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoria")
    public ResponseEntity<CategoriaDTO> actualizar(
            @Parameter(description = "ID de la categoria", example = "1") @PathVariable Long id,
            @Valid @RequestBody CategoriaUpdateDTO updateDTO) {
        return ResponseEntity.ok(categoriaService.updateCategoria(id, updateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoria", description = "Solo si no tiene subcategorias ni libros asociados")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria eliminada"),
            @ApiResponse(responseCode = "400", description = "Tiene subcategorias o libros asociados"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la categoria", example = "1") @PathVariable Long id) {
        categoriaService.deleteCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
