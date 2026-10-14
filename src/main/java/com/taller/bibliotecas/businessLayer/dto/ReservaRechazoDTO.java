package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para rechazar una solicitud de reserva")
public class ReservaRechazoDTO {

    @NotBlank(message = "El motivo del rechazo es obligatorio")
    @Size(max = 500)
    @Schema(description = "Motivo por el que se rechaza la solicitud", example = "No hay ejemplares disponibles para esta sede")
    private String motivo;
}
