package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Datos de un usuario administrativo para autenticarlo. USO INTERNO: incluye el hash de la contrasena,
 * por eso nunca se devuelve en una respuesta HTTP (para eso esta UsuarioAdminDTO).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAuthDTO {

    private Long id;
    private String nombre;
    private String email;

    @ToString.Exclude
    private String password;

    private RolUsuario rol;
    private Long sedeId;
    private Boolean activo;
}
