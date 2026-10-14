package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Libro con la cantidad de veces que fue solicitado (prestamos + reservas)")
public class LibroSolicitadoDTO {

    @Schema(description = "ID del libro")
    private Long libroId;

    @Schema(description = "Titulo del libro")
    private String titulo;

    @Schema(description = "Autor del libro")
    private String autor;

    @Schema(description = "Cantidad de prestamos del libro")
    private Long totalPrestamos;

    @Schema(description = "Cantidad de reservas/solicitudes del libro")
    private Long totalReservas;

    @Schema(description = "Prestamos + reservas")
    private Long totalSolicitudes;
}
