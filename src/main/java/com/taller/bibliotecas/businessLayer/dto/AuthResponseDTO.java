package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta del inicio de sesion: token JWT y datos basicos del usuario")
public class AuthResponseDTO {

    @Schema(description = "Token JWT que se envia en el header Authorization: Bearer <token>")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tipo;

    @Schema(description = "Milisegundos de vigencia del token", example = "3600000")
    private long expiraEnMs;

    @Schema(description = "ID del usuario", example = "1")
    private Long usuarioId;

    @Schema(description = "Nombre del usuario", example = "Administrador General")
    private String nombre;

    @Schema(description = "Correo del usuario", example = "admin@biblioteca.com")
    private String email;

    @Schema(description = "Rol del usuario; el frontend lo usa para decidir a que panel enviarlo", example = "ADMINISTRADOR_GENERAL")
    private RolUsuario rol;

    @Schema(description = "ID de la sede del usuario (null para el administrador general)")
    private Long sedeId;
}
