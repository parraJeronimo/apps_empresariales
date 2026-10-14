package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.businessLayer.service.UsuarioAdminService;
import com.taller.bibliotecas.persistenceLayer.dao.UsuarioAdminDAO;
import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UsuarioAdminServiceImpl implements UsuarioAdminService {

    private final UsuarioAdminDAO usuarioAdminDAO;
    private final SedeService sedeService;

    @Override
    public UsuarioAdminDTO createUsuario(UsuarioAdminCreateDTO createDTO) {
        log.info("Registrando usuario administrativo: {}", createDTO.getEmail());
        if (usuarioAdminDAO.existsByEmail(createDTO.getEmail())) {
            throw new SolicitudInvalidaException("Ya existe un usuario registrado con el email: " + createDTO.getEmail());
        }
        validarSedeSegunRol(createDTO.getRol(), createDTO.getSedeId());
        return usuarioAdminDAO.save(createDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioAdminDTO getUsuarioById(Long id) {
        return usuarioAdminDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAdminDTO> getAllUsuarios() {
        return usuarioAdminDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAdminDTO> getUsuariosBySede(Long sedeId) {
        sedeService.getSedeById(sedeId);
        return usuarioAdminDAO.findBySedeId(sedeId);
    }

    @Override
    public UsuarioAdminDTO updateUsuario(Long id, UsuarioAdminUpdateDTO updateDTO) {
        UsuarioAdminDTO actual = getUsuarioById(id);

        if (updateDTO.getEmail() != null && usuarioAdminDAO.existsByEmailAndIdNot(updateDTO.getEmail(), id)) {
            throw new SolicitudInvalidaException("Ya existe otro usuario registrado con el email: " + updateDTO.getEmail());
        }

        // Se valida la combinacion rol/sede que quedaria despues de la actualizacion
        RolUsuario rolFinal = updateDTO.getRol() != null ? updateDTO.getRol() : actual.getRol();
        Long sedeFinal = updateDTO.getSedeId() != null ? updateDTO.getSedeId() : actual.getSedeId();
        validarSedeSegunRol(rolFinal, sedeFinal);

        return usuarioAdminDAO.update(id, updateDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
    }

    /** Todos los roles, salvo ADMINISTRADOR_GENERAL, deben pertenecer a una sede existente. */
    private void validarSedeSegunRol(RolUsuario rol, Long sedeId) {
        if (rol != RolUsuario.ADMINISTRADOR_GENERAL && sedeId == null) {
            throw new SolicitudInvalidaException("Los usuarios con rol " + rol + " deben estar asociados a una sede");
        }
        if (sedeId != null) {
            sedeService.getSedeById(sedeId);
        }
    }
}
