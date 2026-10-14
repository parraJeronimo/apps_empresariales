package com.taller.bibliotecas.presentationLayer.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estructura estandar de error de la API")
public class ErrorResponse {
    @Schema(example = "2026-10-09T10:15:30")
    private LocalDateTime timestamp;

    @Schema(example = "400")
    private int status;

    @Schema(example = "Bad Request")
    private String error;

    @Schema(example = "El nombre de la sede es obligatorio")
    private String message;

    @Schema(description = "Detalle de errores de validacion por campo (si aplica)")
    private List<String> detalles;
}
