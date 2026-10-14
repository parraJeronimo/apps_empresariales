package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar la devolucion de un ejemplar")
public class DevolucionDTO {

    @NotNull(message = "La sede de devolucion es obligatoria")
    @Schema(description = "ID de la sede donde se recibe el ejemplar (puede ser distinta a la del prestamo)", example = "2")
    private Long sedeDevolucionId;
}
