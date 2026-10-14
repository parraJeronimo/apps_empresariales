package com.taller.bibliotecas.persistenceLayer.dao;

import com.taller.bibliotecas.businessLayer.dto.SedeCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.SedeEntity;
import com.taller.bibliotecas.persistenceLayer.mapper.SedeMapper;
import com.taller.bibliotecas.persistenceLayer.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SedeDAO {

    private final SedeRepository sedeRepository;
    private final SedeMapper sedeMapper;

    public SedeDTO save(SedeCreateDTO createDTO) {
        SedeEntity entity = sedeMapper.toEntity(createDTO);
        return sedeMapper.toDTO(sedeRepository.save(entity));
    }

    public Optional<SedeDTO> findById(Long id) {
        return sedeRepository.findById(id).map(sedeMapper::toDTO);
    }

    public List<SedeDTO> findAll() {
        return sedeMapper.toDTOList(sedeRepository.findAll());
    }

    public List<SedeDTO> findByActiva(boolean activa) {
        return sedeMapper.toDTOList(sedeRepository.findByActiva(activa));
    }

    public Optional<SedeDTO> update(Long id, SedeUpdateDTO updateDTO) {
        return sedeRepository.findById(id).map(existing -> {
            sedeMapper.updateEntityFromDTO(updateDTO, existing);
            return sedeMapper.toDTO(sedeRepository.save(existing));
        });
    }

    public Optional<SedeDTO> cambiarEstado(Long id, boolean activa) {
        return sedeRepository.findById(id).map(existing -> {
            existing.setActiva(activa);
            return sedeMapper.toDTO(sedeRepository.save(existing));
        });
    }

    public boolean existsById(Long id) {
        return sedeRepository.existsById(id);
    }

    public boolean existsByNombre(String nombre) {
        return sedeRepository.existsByNombre(nombre);
    }

    public boolean existsByNombreAndIdNot(String nombre, Long id) {
        return sedeRepository.existsByNombreAndIdNot(nombre, id);
    }
}
