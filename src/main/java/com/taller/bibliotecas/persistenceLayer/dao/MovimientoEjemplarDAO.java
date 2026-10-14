package com.taller.bibliotecas.persistenceLayer.dao;

import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarDTO;
import com.taller.bibliotecas.persistenceLayer.entity.MovimientoEjemplarEntity;
import com.taller.bibliotecas.persistenceLayer.mapper.MovimientoEjemplarMapper;
import com.taller.bibliotecas.persistenceLayer.repository.EjemplarRepository;
import com.taller.bibliotecas.persistenceLayer.repository.MovimientoEjemplarRepository;
import com.taller.bibliotecas.persistenceLayer.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MovimientoEjemplarDAO {

    private final MovimientoEjemplarRepository movimientoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final SedeRepository sedeRepository;
    private final MovimientoEjemplarMapper movimientoMapper;

    public MovimientoEjemplarDTO save(MovimientoEjemplarCreateDTO createDTO) {
        MovimientoEjemplarEntity entity = new MovimientoEjemplarEntity();
        entity.setEjemplarEntity(ejemplarRepository.getReferenceById(createDTO.getEjemplarId()));
        entity.setTipo(createDTO.getTipo());
        entity.setDetalle(createDTO.getDetalle());
        if (createDTO.getSedeOrigenId() != null) {
            entity.setSedeOrigenEntity(sedeRepository.getReferenceById(createDTO.getSedeOrigenId()));
        }
        if (createDTO.getSedeDestinoId() != null) {
            entity.setSedeDestinoEntity(sedeRepository.getReferenceById(createDTO.getSedeDestinoId()));
        }
        return movimientoMapper.toDTO(movimientoRepository.save(entity));
    }

    public List<MovimientoEjemplarDTO> findByEjemplarId(Long ejemplarId) {
        return movimientoMapper.toDTOList(
                movimientoRepository.findByEjemplarEntityIdOrderByFechaMovimientoAscIdAsc(ejemplarId));
    }
}
