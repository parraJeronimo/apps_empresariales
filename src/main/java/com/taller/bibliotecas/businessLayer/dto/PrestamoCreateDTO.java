package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un prestamo")
public class PrestamoCreateDTO {

    @NotNull(message = "El ejemplar es obligatorio")
    @Schema(description = "ID del ejemplar que se presta", example = "1")
    private Long ejemplarId;

    @NotNull(message = "El cliente es obligatorio")
    @Schema(description = "ID del cliente que recibe el prestamo", example = "1")
    private Long clienteId;

    @NotNull(message = "La sede del prestamo es obligatoria")
    @Schema(description = "ID de la sede donde se realiza el prestamo", example = "1")
    private Long sedeId;

    @Min(value = 1, message = "Los dias de prestamo deben ser al menos 1")
    @Max(value = 30, message = "Los dias de prestamo no pueden superar 30")
    @Schema(description = "Dias de prestamo (por defecto 14, maximo 30)", example = "14")
    private Integer diasPrestamo;
}
