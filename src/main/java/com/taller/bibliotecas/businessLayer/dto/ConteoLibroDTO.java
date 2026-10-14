package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Conteo de operaciones agrupadas por libro (uso interno de reportes)")
public class ConteoLibroDTO {

    @Schema(description = "ID del libro")
    private Long libroId;

    @Schema(description = "Cantidad de operaciones del libro")
    private Long total;
}
