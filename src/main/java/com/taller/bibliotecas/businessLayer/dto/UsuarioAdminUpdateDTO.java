package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar un usuario administrativo (actualizacion parcial)")
public class UsuarioAdminUpdateDTO {

    @Size(max = 150)
    @Schema(description = "Nombre completo del usuario")
    private String nombre;

    @Email(message = "El email no tiene un formato valido")
    @Size(max = 150)
    @Schema(description = "Correo electronico del usuario")
    private String email;

    @Schema(description = "Nuevo rol del usuario")
    private RolUsuario rol;

    @Schema(description = "Nueva sede del usuario")
    private Long sedeId;

    @Schema(description = "Activar o desactivar al usuario")
    private Boolean activo;
}
