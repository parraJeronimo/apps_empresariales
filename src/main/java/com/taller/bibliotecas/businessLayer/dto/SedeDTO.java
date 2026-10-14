package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informacion de una sede de la biblioteca")
public class SedeDTO {

    @Schema(description = "ID unico de la sede", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre de la sede", example = "Sede Central")
    private String nombre;

    @Schema(description = "Direccion de la sede", example = "Carrera 14 # 20-30")
    private String direccion;

    @Schema(description = "Ciudad de la sede", example = "Armenia")
    private String ciudad;

    @Schema(description = "Telefono de contacto", example = "6067400000")
    private String telefono;

    @Schema(description = "Indica si la sede esta activa", example = "true")
    private Boolean activa;

    @Schema(description = "Fecha de creacion del registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de ultima actualizacion", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
