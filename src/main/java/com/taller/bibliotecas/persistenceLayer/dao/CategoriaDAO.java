package com.taller.bibliotecas.persistenceLayer.dao;

import com.taller.bibliotecas.businessLayer.dto.CategoriaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.CategoriaEntity;
import com.taller.bibliotecas.persistenceLayer.mapper.CategoriaMapper;
import com.taller.bibliotecas.persistenceLayer.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoriaDAO {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaDTO save(CategoriaCreateDTO createDTO) {
        CategoriaEntity entity = categoriaMapper.toEntity(createDTO);
        if (createDTO.getCategoriaPadreId() != null) {
            entity.setCategoriaPadreEntity(categoriaRepository.getReferenceById(createDTO.getCategoriaPadreId()));
        }
        return categoriaMapper.toDTO(categoriaRepository.save(entity));
    }

    public Optional<CategoriaDTO> findById(Long id) {
        return categoriaRepository.findById(id).map(categoriaMapper::toDTO);
    }

    public List<CategoriaDTO> findAll() {
        return categoriaMapper.toDTOList(categoriaRepository.findAll());
    }

    public List<CategoriaDTO> findRaices() {
        return categoriaMapper.toDTOList(categoriaRepository.findByCategoriaPadreEntityIsNull());
    }

    public List<CategoriaDTO> findByPadreId(Long categoriaPadreId) {
        return categoriaMapper.toDTOList(categoriaRepository.findByCategoriaPadreEntityId(categoriaPadreId));
    }

    public Optional<CategoriaDTO> update(Long id, CategoriaUpdateDTO updateDTO) {
        return categoriaRepository.findById(id).map(existing -> {
            categoriaMapper.updateEntityFromDTO(updateDTO, existing);
            return categoriaMapper.toDTO(categoriaRepository.save(existing));
        });
    }

    public boolean deleteById(Long id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsById(Long id) {
        return categoriaRepository.existsById(id);
    }

    public boolean existsByPadreId(Long categoriaPadreId) {
        return categoriaRepository.existsByCategoriaPadreEntityId(categoriaPadreId);
    }
}
