package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un ejemplar fisico de un libro")
public class EjemplarCreateDTO {

    @Size(max = 50)
    @Schema(description = "Codigo unico del ejemplar. Si se omite, el sistema lo genera", example = "978030747472-001")
    private String codigo;

    @NotNull(message = "El libro del ejemplar es obligatorio")
    @Schema(description = "ID del libro al que pertenece el ejemplar", example = "1")
    private Long libroId;

    @NotNull(message = "La sede del ejemplar es obligatoria")
    @Schema(description = "ID de la sede donde se registra (sede propietaria)", example = "1")
    private Long sedeId;
}
