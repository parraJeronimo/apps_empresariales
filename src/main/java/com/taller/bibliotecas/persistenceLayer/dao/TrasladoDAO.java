package com.taller.bibliotecas.persistenceLayer.dao;

import com.taller.bibliotecas.businessLayer.dto.TrasladoCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.TrasladoDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoTraslado;
import com.taller.bibliotecas.persistenceLayer.entity.TrasladoEntity;
import com.taller.bibliotecas.persistenceLayer.mapper.TrasladoMapper;
import com.taller.bibliotecas.persistenceLayer.repository.EjemplarRepository;
import com.taller.bibliotecas.persistenceLayer.repository.SedeRepository;
import com.taller.bibliotecas.persistenceLayer.repository.TrasladoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrasladoDAO {

    private final TrasladoRepository trasladoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final SedeRepository sedeRepository;
    private final TrasladoMapper trasladoMapper;

    public TrasladoDTO save(TrasladoCreateDTO createDTO, Long sedeOrigenId) {
        TrasladoEntity entity = new TrasladoEntity();
        entity.setEjemplarEntity(ejemplarRepository.getReferenceById(createDTO.getEjemplarId()));
        entity.setSedeOrigenEntity(sedeRepository.getReferenceById(sedeOrigenId));
        entity.setSedeDestinoEntity(sedeRepository.getReferenceById(createDTO.getSedeDestinoId()));
        entity.setObservaciones(createDTO.getObservaciones());
        entity.setEstado(EstadoTraslado.EN_TRANSITO);
        entity.setFechaSolicitud(LocalDateTime.now());
        return trasladoMapper.toDTO(trasladoRepository.save(entity));
    }

    public Optional<TrasladoDTO> findById(Long id) {
        return trasladoRepository.findById(id).map(trasladoMapper::toDTO);
    }

    public List<TrasladoDTO> findAll() {
        return trasladoMapper.toDTOList(trasladoRepository.findAll());
    }

    /** Traslados donde la sede es origen o destino */
    public List<TrasladoDTO> findBySedeId(Long sedeId) {
        return trasladoMapper.toDTOList(
                trasladoRepository.findBySedeOrigenEntityIdOrSedeDestinoEntityId(sedeId, sedeId));
    }

    public List<TrasladoDTO> findByFechaSolicitudBetween(LocalDateTime desde, LocalDateTime hasta) {
        return trasladoMapper.toDTOList(trasladoRepository.findByFechaSolicitudBetween(desde, hasta));
    }

    public Optional<TrasladoDTO> actualizarEstado(Long id, EstadoTraslado estado, LocalDateTime fechaRecepcion) {
        return trasladoRepository.findById(id).map(existing -> {
            existing.setEstado(estado);
            existing.setFechaRecepcion(fechaRecepcion);
            return trasladoMapper.toDTO(trasladoRepository.save(existing));
        });
    }
}
