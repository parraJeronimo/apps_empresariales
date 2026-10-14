package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar un cliente existente (el documento no se puede modificar)")
public class ClienteUpdateDTO {

    @Size(max = 150)
    @Schema(description = "Nombre completo del cliente", example = "Juan Perez")
    private String nombre;

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
