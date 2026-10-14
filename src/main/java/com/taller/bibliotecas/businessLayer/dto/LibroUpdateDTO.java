package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar un libro existente (actualizacion parcial)")
public class LibroUpdateDTO {

    @Size(max = 200)
    @Schema(description = "Titulo del libro")
    private String titulo;

    @Size(max = 150)
    @Schema(description = "Autor del libro")
    private String autor;

    @Size(max = 20)
    @Schema(description = "ISBN del libro")
    private String isbn;

    @Size(max = 150)
    @Schema(description = "Editorial")
    private String editorial;

    @Positive(message = "El anio de publicacion debe ser mayor a cero")
    @Schema(description = "Anio de publicacion")
    private Integer anioPublicacion;

    @Size(max = 2000)
    @Schema(description = "Descripcion o sinopsis del libro")
    private String descripcion;

    @Schema(description = "Nueva categoria del libro (reclasificacion)")
    private Long categoriaId;
}
