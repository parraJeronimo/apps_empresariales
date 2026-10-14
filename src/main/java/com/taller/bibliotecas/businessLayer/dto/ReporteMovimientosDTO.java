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
@Schema(description = "Reporte de traslados de ejemplares entre sedes en un rango de fechas")
public class ReporteMovimientosDTO {

    @Schema(description = "Fecha inicial del reporte")
    private LocalDate desde;

    @Schema(description = "Fecha final del reporte")
    private LocalDate hasta;

    @Schema(description = "Sede filtrada (null = todas las sedes)")
    private Long sedeId;

    @Schema(description = "Total de traslados solicitados en el rango")
    private Integer totalTraslados;

    @Schema(description = "Traslados pendientes de recepcion")
    private Integer enTransito;

    @Schema(description = "Traslados recibidos")
    private Integer recibidos;

    @Schema(description = "Traslados cancelados")
    private Integer cancelados;

    @Schema(description = "Detalle de los traslados")
    private List<TrasladoDTO> traslados;
}
