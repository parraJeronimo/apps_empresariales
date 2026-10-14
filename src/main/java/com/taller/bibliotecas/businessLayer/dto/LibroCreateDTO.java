package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un nuevo libro")
public class LibroCreateDTO {

    @NotBlank(message = "El titulo del libro es obligatorio")
    @Size(max = 200)
    @Schema(description = "Titulo del libro", example = "Cien anios de soledad")
    private String titulo;

    @NotBlank(message = "El autor del libro es obligatorio")
    @Size(max = 150)
    @Schema(description = "Autor del libro", example = "Gabriel Garcia Marquez")
    private String autor;

    @NotBlank(message = "El ISBN del libro es obligatorio")
    @Size(max = 20)
    @Schema(description = "ISBN del libro", example = "978-0307474728")
    private String isbn;

    @Size(max = 150)
    @Schema(description = "Editorial", example = "Vintage Espanol")
    private String editorial;

    @Positive(message = "El anio de publicacion debe ser mayor a cero")
    @Schema(description = "Anio de publicacion", example = "1967")
    private Integer anioPublicacion;

    @Size(max = 2000)
    @Schema(description = "Descripcion o sinopsis del libro")
    private String descripcion;

    @Schema(description = "ID de la categoria o subcategoria del libro (opcional)", example = "1")
    private Long categoriaId;
}
