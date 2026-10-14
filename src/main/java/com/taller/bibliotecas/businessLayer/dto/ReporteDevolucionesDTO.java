package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reporte de devoluciones en un rango de fechas")
public class ReporteDevolucionesDTO {

    @Schema(description = "Fecha inicial del reporte")
    private LocalDate desde;

    @Schema(description = "Fecha final del reporte")
    private LocalDate hasta;

    @Schema(description = "Sede filtrada (null = todas las sedes)")
    private Long sedeId;

    @Schema(description = "Total de devoluciones realizadas en el rango")
    private Integer totalDevoluciones;

    @Schema(description = "Devoluciones realizadas a tiempo")
    private Integer aTiempo;

    @Schema(description = "Devoluciones realizadas con retraso")
    private Integer conRetraso;

    @Schema(description = "Devoluciones realizadas en una sede distinta a la del prestamo")
    private Integer enSedeDiferente;

    @Schema(description = "Detalle de las devoluciones")
    private List<PrestamoDTO> devoluciones;
}
