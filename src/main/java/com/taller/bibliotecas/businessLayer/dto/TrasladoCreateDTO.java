package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar el traslado de un ejemplar a otra sede")
public class TrasladoCreateDTO {

    @NotNull(message = "El ejemplar es obligatorio")
    @Schema(description = "ID del ejemplar a trasladar", example = "1")
    private Long ejemplarId;

    @NotNull(message = "La sede destino es obligatoria")
    @Schema(description = "ID de la sede destino", example = "2")
    private Long sedeDestinoId;

    @Size(max = 500)
    @Schema(description = "Observaciones del traslado")
    private String observaciones;
}
