package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informacion de un usuario administrativo")
public class UsuarioAdminDTO {

    @Schema(description = "ID unico del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre completo del usuario")
    private String nombre;

    @Schema(description = "Correo electronico del usuario")
    private String email;

    @Schema(description = "Rol asignado")
    private RolUsuario rol;

    @Schema(description = "ID de la sede del usuario")
    private Long sedeId;

    @Schema(description = "Nombre de la sede del usuario", accessMode = Schema.AccessMode.READ_ONLY)
    private String sedeNombre;

    @Schema(description = "Indica si el usuario esta activo")
    private Boolean activo;

    @Schema(description = "Fecha de creacion del registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de ultima actualizacion", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
