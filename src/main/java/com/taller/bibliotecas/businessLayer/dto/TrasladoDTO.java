package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.EstadoTraslado;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informacion de un traslado de ejemplar entre sedes")
public class TrasladoDTO {

    @Schema(description = "ID unico del traslado", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID del ejemplar trasladado")
    private Long ejemplarId;

    @Schema(description = "Codigo del ejemplar", accessMode = Schema.AccessMode.READ_ONLY)
    private String ejemplarCodigo;

    @Schema(description = "Titulo del libro", accessMode = Schema.AccessMode.READ_ONLY)
    private String libroTitulo;

    @Schema(description = "ID de la sede origen")
    private Long sedeOrigenId;

    @Schema(description = "Nombre de la sede origen", accessMode = Schema.AccessMode.READ_ONLY)
    private String sedeOrigenNombre;

    @Schema(description = "ID de la sede destino")
    private Long sedeDestinoId;

    @Schema(description = "Nombre de la sede destino", accessMode = Schema.AccessMode.READ_ONLY)
    private String sedeDestinoNombre;

    @Schema(description = "Estado del traslado", example = "EN_TRANSITO")
    private EstadoTraslado estado;

    @Schema(description = "Fecha y hora en que se registro el traslado")
    private LocalDateTime fechaSolicitud;

    @Schema(description = "Fecha y hora en que la sede destino confirmo la recepcion")
    private LocalDateTime fechaRecepcion;

    @Schema(description = "Observaciones del traslado")
    private String observaciones;

    @Schema(description = "Fecha de creacion del registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de ultima actualizacion", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
