package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminUpdateDTO;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioAdminService {

    /** RF-23: Registrar un usuario administrativo y asignarle un rol (y una sede, salvo ADMINISTRADOR_GENERAL) */
    UsuarioAdminDTO createUsuario(UsuarioAdminCreateDTO createDTO);

    UsuarioAdminDTO getUsuarioById(Long id);

    List<UsuarioAdminDTO> getAllUsuarios();

    /** Igual que getAllUsuarios() pero devuelve solo la pagina pedida (el controlador la expone por headers). */
    Page<UsuarioAdminDTO> getAllUsuarios(Pageable pageable);

    List<UsuarioAdminDTO> getUsuariosBySede(Long sedeId);

    /** RF-23: Cambiar datos, rol, sede o activar/desactivar un usuario */
    UsuarioAdminDTO updateUsuario(Long id, UsuarioAdminUpdateDTO updateDTO);
}
