package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informacion de un cliente")
public class ClienteDTO {

    @Schema(description = "ID unico del cliente", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre completo del cliente")
    private String nombre;

    @Schema(description = "Documento de identidad")
    private String documento;

    @Schema(description = "Correo electronico del cliente")
    private String email;

    @Schema(description = "Telefono de contacto")
    private String telefono;

    @Schema(description = "Direccion de residencia")
    private String direccion;

    @Schema(description = "Fecha de creacion del registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de ultima actualizacion", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
