package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informacion de una categoria o subcategoria")
public class CategoriaDTO {

    @Schema(description = "ID unico de la categoria", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre de la categoria", example = "Ciencia ficcion")
    private String nombre;

    @Schema(description = "Descripcion de la categoria")
    private String descripcion;

    @Schema(description = "ID de la categoria padre (null si es categoria raiz)")
    private Long categoriaPadreId;

    @Schema(description = "Nombre de la categoria padre", accessMode = Schema.AccessMode.READ_ONLY)
    private String categoriaPadreNombre;

    @Schema(description = "Fecha de creacion del registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de ultima actualizacion", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
