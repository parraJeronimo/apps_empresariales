package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear una categoria o subcategoria")
public class CategoriaCreateDTO {

    @NotBlank(message = "El nombre de la categoria es obligatorio")
    @Size(max = 100)
    @Schema(description = "Nombre de la categoria", example = "Ciencia ficcion")
    private String nombre;

    @Size(max = 500)
    @Schema(description = "Descripcion de la categoria", example = "Novelas y cuentos de ciencia ficcion")
    private String descripcion;

    @Schema(description = "ID de la categoria padre (si es una subcategoria)", example = "1")
    private Long categoriaPadreId;
}
