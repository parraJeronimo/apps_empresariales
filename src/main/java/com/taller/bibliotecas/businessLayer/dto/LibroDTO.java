package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informacion de un libro del catalogo")
public class LibroDTO {

    @Schema(description = "ID unico del libro", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Titulo del libro")
    private String titulo;

    @Schema(description = "Autor del libro")
    private String autor;

    @Schema(description = "ISBN del libro")
    private String isbn;

    @Schema(description = "Editorial")
    private String editorial;

    @Schema(description = "Anio de publicacion")
    private Integer anioPublicacion;

    @Schema(description = "Descripcion o sinopsis del libro")
    private String descripcion;

    @Schema(description = "ID de la categoria del libro")
    private Long categoriaId;

    @Schema(description = "Nombre de la categoria del libro", accessMode = Schema.AccessMode.READ_ONLY)
    private String categoriaNombre;

    @Schema(description = "Fecha de creacion del registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de ultima actualizacion", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
