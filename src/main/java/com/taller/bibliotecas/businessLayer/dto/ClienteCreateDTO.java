package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un nuevo cliente de la biblioteca")
public class ClienteCreateDTO {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 150)
    @Schema(description = "Nombre completo del cliente", example = "Juan Perez")
    private String nombre;

    @NotBlank(message = "El documento del cliente es obligatorio")
    @Size(max = 30)
    @Schema(description = "Documento de identidad", example = "1094123456")
    private String documento;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    @Size(max = 150)
    @Schema(description = "Correo electronico del cliente", example = "juan.perez@email.com")
    private String email;

    @Size(max = 20)
    @Schema(description = "Telefono de contacto", example = "3001234567")
    private String telefono;

    @Size(max = 200)
    @Schema(description = "Direccion de residencia", example = "Calle 10 # 20-30, Armenia")
    private String direccion;
}
