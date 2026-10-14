package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para reservar un libro que no tiene ejemplares disponibles")
public class ReservaCreateDTO {

    @NotNull(message = "El cliente es obligatorio")
    @Schema(description = "ID del cliente que reserva", example = "1")
    private Long clienteId;

    @NotNull(message = "El libro es obligatorio")
    @Schema(description = "ID del libro que se quiere reservar", example = "1")
    private Long libroId;

    @NotNull(message = "La sede es obligatoria")
    @Schema(description = "ID de la sede donde el cliente quiere recoger el libro", example = "1")
    private Long sedeId;
}
