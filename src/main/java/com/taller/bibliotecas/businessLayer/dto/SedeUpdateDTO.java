package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar una sede existente (actualizacion parcial)")
public class SedeUpdateDTO {

    @Size(max = 150)
    @Schema(description = "Nombre de la sede", example = "Sede Central")
    private String nombre;

    @Size(max = 200)
    @Schema(description = "Direccion de la sede", example = "Carrera 14 # 20-30")
    private String direccion;

    @Size(max = 100)
    @Schema(description = "Ciudad de la sede", example = "Armenia")
    private String ciudad;

    @Size(max = 20)
    @Schema(description = "Telefono de contacto", example = "6067400000")
    private String telefono;
}
