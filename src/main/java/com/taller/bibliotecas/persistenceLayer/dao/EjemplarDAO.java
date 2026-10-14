package com.taller.bibliotecas.persistenceLayer.dao;

import com.taller.bibliotecas.businessLayer.dto.EjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EjemplarEntity;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.mapper.EjemplarMapper;
import com.taller.bibliotecas.persistenceLayer.repository.EjemplarRepository;
import com.taller.bibliotecas.persistenceLayer.repository.LibroRepository;
import com.taller.bibliotecas.persistenceLayer.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EjemplarDAO {

    private final EjemplarRepository ejemplarRepository;
    private final LibroRepository libroRepository;
    private final SedeRepository sedeRepository;
    private final EjemplarMapper ejemplarMapper;

    /** El ejemplar nace DISPONIBLE; la sede donde se registra es a la vez propietaria y actual. */
    public EjemplarDTO save(EjemplarCreateDTO createDTO) {
        EjemplarEntity entity = ejemplarMapper.toEntity(createDTO);
        entity.setLibroEntity(libroRepository.getReferenceById(createDTO.getLibroId()));
        entity.setSedePropietariaEntity(sedeRepository.getReferenceById(createDTO.getSedeId()));
        entity.setSedeActualEntity(sedeRepository.getReferenceById(createDTO.getSedeId()));
        return ejemplarMapper.toDTO(ejemplarRepository.save(entity));
    }

    public Optional<EjemplarDTO> findById(Long id) {
        return ejemplarRepository.findById(id).map(ejemplarMapper::toDTO);
    }

    public List<EjemplarDTO> findAll() {
        return ejemplarMapper.toDTOList(ejemplarRepository.findAll());
    }

    public Optional<EjemplarDTO> findByCodigo(String codigo) {
        return ejemplarRepository.findByCodigo(codigo).map(ejemplarMapper::toDTO);
    }

    public List<EjemplarDTO> findByLibroId(Long libroId) {
        return ejemplarMapper.toDTOList(ejemplarRepository.findByLibroEntityId(libroId));
    }

    public List<EjemplarDTO> findBySedeActualId(Long sedeId) {
        return ejemplarMapper.toDTOList(ejemplarRepository.findBySedeActualEntityId(sedeId));
    }

    /** Cambia el estado y, si sedeActualId no es null, tambien la ubicacion actual del ejemplar. */
    public Optional<EjemplarDTO> actualizarEstadoYSede(Long id, EstadoEjemplar estado, Long sedeActualId) {
        return ejemplarRepository.findById(id).map(existing -> {
            existing.setEstado(estado);
            if (sedeActualId != null) {
                existing.setSedeActualEntity(sedeRepository.getReferenceById(sedeActualId));
            }
            return ejemplarMapper.toDTO(ejemplarRepository.save(existing));
        });
    }

    public boolean existsById(Long id) {
        return ejemplarRepository.existsById(id);
    }

    public boolean existsByCodigo(String codigo) {
        return ejemplarRepository.existsByCodigo(codigo);
    }

    public boolean existsByLibroId(Long libroId) {
        return ejemplarRepository.existsByLibroEntityId(libroId);
    }

    public long countByLibroId(Long libroId) {
        return ejemplarRepository.countByLibroEntityId(libroId);
    }

    public long countByEstado(EstadoEjemplar estado) {
        return ejemplarRepository.countByEstado(estado);
    }

    public long countBySedeActualId(Long sedeId) {
        return ejemplarRepository.countBySedeActualEntityId(sedeId);
    }

    public long countByLibroIdAndSedeActualIdAndEstado(Long libroId, Long sedeId, EstadoEjemplar estado) {
        return ejemplarRepository.countByLibroEntityIdAndSedeActualEntityIdAndEstado(libroId, sedeId, estado);
    }

    public long countBySedeActualIdAndEstadoNot(Long sedeId, EstadoEjemplar estado) {
        return ejemplarRepository.countBySedeActualEntityIdAndEstadoNot(sedeId, estado);
    }

    public long count() {
        return ejemplarRepository.count();
    }
}
