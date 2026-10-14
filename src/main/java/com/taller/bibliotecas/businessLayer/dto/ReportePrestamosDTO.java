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
@Schema(description = "Reporte de prestamos en un rango de fechas")
public class ReportePrestamosDTO {

    @Schema(description = "Fecha inicial del reporte")
    private LocalDate desde;

    @Schema(description = "Fecha final del reporte")
    private LocalDate hasta;

    @Schema(description = "Sede filtrada (null = todas las sedes)")
    private Long sedeId;

    @Schema(description = "Total de prestamos iniciados en el rango")
    private Integer totalPrestamos;

    @Schema(description = "Prestamos vigentes (activos y no vencidos)")
    private Integer activos;

    @Schema(description = "Prestamos activos con fecha limite superada")
    private Integer vencidos;

    @Schema(description = "Prestamos ya devueltos")
    private Integer finalizados;

    @Schema(description = "Prestamos devueltos despues de la fecha limite")
    private Integer devueltosConRetraso;

    @Schema(description = "Detalle de los prestamos")
    private List<PrestamoDTO> prestamos;
}
