package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un usuario administrativo")
public class UsuarioAdminCreateDTO {

    @NotBlank(message = "El nombre del usuario es obligatorio")
    @Size(max = 150)
    @Schema(description = "Nombre completo del usuario", example = "Maria Lopez")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    @Size(max = 150)
    @Schema(description = "Correo electronico del usuario", example = "maria.lopez@biblioteca.com")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
    @ToString.Exclude
    @Schema(description = "Contrasena inicial (minimo 8 caracteres). Se almacena cifrada con BCrypt", example = "Clave12345", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @NotNull(message = "El rol del usuario es obligatorio")
    @Schema(description = "Rol asignado al usuario", example = "ADMINISTRADOR_SEDE")
    private RolUsuario rol;

    @Schema(description = "Sede a la que pertenece (obligatoria salvo para ADMINISTRADOR_GENERAL)", example = "1")
    private Long sedeId;
}
