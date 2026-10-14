package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.EstadoReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informacion de una reserva / solicitud de libro")
public class ReservaDTO {

    @Schema(description = "ID unico de la reserva", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID del cliente")
    private Long clienteId;

    @Schema(description = "Nombre del cliente", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNombre;

    @Schema(description = "ID del libro solicitado")
    private Long libroId;

    @Schema(description = "Titulo del libro solicitado", accessMode = Schema.AccessMode.READ_ONLY)
    private String libroTitulo;

    @Schema(description = "ID de la sede solicitada")
    private Long sedeId;

    @Schema(description = "Nombre de la sede solicitada", accessMode = Schema.AccessMode.READ_ONLY)
    private String sedeNombre;

    @Schema(description = "Estado de la reserva", example = "PENDIENTE")
    private EstadoReserva estado;

    @Schema(description = "Motivo del rechazo (solo si fue rechazada)")
    private String motivoRechazo;

    @Schema(description = "Fecha y hora de la solicitud")
    private LocalDateTime fechaSolicitud;

    @Schema(description = "Fecha y hora de la respuesta del administrador")
    private LocalDateTime fechaRespuesta;

    @Schema(description = "Fecha de creacion del registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de ultima actualizacion", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
