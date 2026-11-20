package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciales para iniciar sesion")
public class LoginRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    @Schema(description = "Correo del usuario administrativo", example = "admin@biblioteca.com")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @ToString.Exclude
    @Schema(description = "Contrasena del usuario", example = "Admin12345")
    private String password;
}
