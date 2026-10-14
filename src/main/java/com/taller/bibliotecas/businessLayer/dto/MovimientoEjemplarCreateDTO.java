package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un movimiento en el historial de un ejemplar")
public class MovimientoEjemplarCreateDTO {

    @Schema(description = "ID del ejemplar")
    private Long ejemplarId;

    @Schema(description = "Tipo de movimiento")
    private TipoMovimiento tipo;

    @Schema(description = "Sede de origen del movimiento (opcional)")
    private Long sedeOrigenId;

    @Schema(description = "Sede de destino del movimiento (opcional)")
    private Long sedeDestinoId;

    @Schema(description = "Detalle del movimiento")
    private String detalle;
}
