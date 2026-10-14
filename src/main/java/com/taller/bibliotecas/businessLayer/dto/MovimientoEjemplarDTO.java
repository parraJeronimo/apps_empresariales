package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.TipoMovimiento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Movimiento registrado en el historial de un ejemplar")
public class MovimientoEjemplarDTO {

    @Schema(description = "ID del movimiento", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID del ejemplar")
    private Long ejemplarId;

    @Schema(description = "Codigo del ejemplar", accessMode = Schema.AccessMode.READ_ONLY)
    private String ejemplarCodigo;

    @Schema(description = "Tipo de movimiento")
    private TipoMovimiento tipo;

    @Schema(description = "Sede de origen")
    private Long sedeOrigenId;

    @Schema(description = "Nombre de la sede de origen", accessMode = Schema.AccessMode.READ_ONLY)
    private String sedeOrigenNombre;

    @Schema(description = "Sede de destino")
    private Long sedeDestinoId;

    @Schema(description = "Nombre de la sede de destino", accessMode = Schema.AccessMode.READ_ONLY)
    private String sedeDestinoNombre;

    @Schema(description = "Fecha y hora del movimiento")
    private LocalDateTime fechaMovimiento;

    @Schema(description = "Detalle del movimiento")
    private String detalle;
}
