package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reporte general de libros y ejemplares")
public class ReporteLibrosDTO {

    @Schema(description = "Total de libros registrados")
    private Long totalLibros;

    @Schema(description = "Total de ejemplares registrados")
    private Long totalEjemplares;

    @Schema(description = "Ejemplares agrupados por estado")
    private Map<EstadoEjemplar, Long> ejemplaresPorEstado;

    @Schema(description = "Ejemplares agrupados por la sede donde se encuentran")
    private Map<String, Long> ejemplaresPorSede;
}
