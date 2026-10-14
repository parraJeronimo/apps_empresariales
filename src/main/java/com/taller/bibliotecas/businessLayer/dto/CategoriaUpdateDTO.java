package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar una categoria existente")
public class CategoriaUpdateDTO {

    @Size(max = 100)
    @Schema(description = "Nombre de la categoria", example = "Ciencia ficcion")
    private String nombre;

    @Size(max = 500)
    @Schema(description = "Descripcion de la categoria")
    private String descripcion;
}
