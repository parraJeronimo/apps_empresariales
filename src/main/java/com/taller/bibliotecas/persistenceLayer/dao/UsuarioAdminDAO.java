package com.taller.bibliotecas.persistenceLayer.dao;

import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminUpdateDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAuthDTO;
import com.taller.bibliotecas.persistenceLayer.entity.UsuarioAdminEntity;
import com.taller.bibliotecas.persistenceLayer.mapper.UsuarioAdminMapper;
import com.taller.bibliotecas.persistenceLayer.repository.SedeRepository;
import com.taller.bibliotecas.persistenceLayer.repository.UsuarioAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
@RequiredArgsConstructor
public class UsuarioAdminDAO {

    private final UsuarioAdminRepository usuarioRepository;
    private final SedeRepository sedeRepository;
    private final UsuarioAdminMapper usuarioMapper;

    public UsuarioAdminDTO save(UsuarioAdminCreateDTO createDTO) {
        UsuarioAdminEntity entity = usuarioMapper.toEntity(createDTO);
        if (createDTO.getSedeId() != null) {
            entity.setSedeEntity(sedeRepository.getReferenceById(createDTO.getSedeId()));
        }
        return usuarioMapper.toDTO(usuarioRepository.save(entity));
    }

    public Optional<UsuarioAdminDTO> findById(Long id) {
        return usuarioRepository.findById(id).map(usuarioMapper::toDTO);
    }

    /** Datos para autenticar (incluye el hash de la contrasena). Solo lo usa la capa de seguridad. */
    @Transactional(readOnly = true)
    public Optional<UsuarioAuthDTO> findAuthByEmail(String email) {
        return usuarioRepository.findByEmail(email).map(u -> new UsuarioAuthDTO(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getPassword(),
                u.getRol(),
                u.getSedeEntity() != null ? u.getSedeEntity().getId() : null,
                u.getActivo()));
    }

    public Optional<UsuarioAdminDTO> findByEmail(String email) {
        return usuarioRepository.findByEmail(email).map(usuarioMapper::toDTO);
    }

    public List<UsuarioAdminDTO> findAll() {
        return usuarioMapper.toDTOList(usuarioRepository.findAll());
    }

    /** Pagina de resultados (la consulta se limita en la base de datos con LIMIT/OFFSET). */
    public Page<UsuarioAdminDTO> findAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(usuarioMapper::toDTO);
    }

    public List<UsuarioAdminDTO> findBySedeId(Long sedeId) {
        return usuarioMapper.toDTOList(usuarioRepository.findBySedeEntityId(sedeId));
    }

    public Optional<UsuarioAdminDTO> update(Long id, UsuarioAdminUpdateDTO updateDTO) {
        return usuarioRepository.findById(id).map(existing -> {
            usuarioMapper.updateEntityFromDTO(updateDTO, existing);
            if (updateDTO.getSedeId() != null) {
                existing.setSedeEntity(sedeRepository.getReferenceById(updateDTO.getSedeId()));
            }
            return usuarioMapper.toDTO(usuarioRepository.save(existing));
        });
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        return usuarioRepository.existsByEmailAndIdNot(email, id);
    }
}
