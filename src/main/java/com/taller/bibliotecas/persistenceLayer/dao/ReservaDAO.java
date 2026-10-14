package com.taller.bibliotecas.persistenceLayer.dao;

import com.taller.bibliotecas.businessLayer.dto.ConteoLibroDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoReserva;
import com.taller.bibliotecas.persistenceLayer.entity.ReservaEntity;
import com.taller.bibliotecas.persistenceLayer.mapper.ReservaMapper;
import com.taller.bibliotecas.persistenceLayer.repository.ClienteRepository;
import com.taller.bibliotecas.persistenceLayer.repository.LibroRepository;
import com.taller.bibliotecas.persistenceLayer.repository.ReservaRepository;
import com.taller.bibliotecas.persistenceLayer.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservaDAO {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final LibroRepository libroRepository;
    private final SedeRepository sedeRepository;
    private final ReservaMapper reservaMapper;

    public ReservaDTO save(ReservaCreateDTO createDTO) {
        ReservaEntity entity = new ReservaEntity();
        entity.setClienteEntity(clienteRepository.getReferenceById(createDTO.getClienteId()));
        entity.setLibroEntity(libroRepository.getReferenceById(createDTO.getLibroId()));
        entity.setSedeEntity(sedeRepository.getReferenceById(createDTO.getSedeId()));
        entity.setEstado(EstadoReserva.PENDIENTE);
        entity.setFechaSolicitud(LocalDateTime.now());
        return reservaMapper.toDTO(reservaRepository.save(entity));
    }

    public Optional<ReservaDTO> findById(Long id) {
        return reservaRepository.findById(id).map(reservaMapper::toDTO);
    }

    public List<ReservaDTO> findAll() {
        return reservaMapper.toDTOList(reservaRepository.findAll());
    }

    public List<ReservaDTO> findByClienteId(Long clienteId) {
        return reservaMapper.toDTOList(reservaRepository.findByClienteEntityId(clienteId));
    }

    public List<ReservaDTO> findBySedeId(Long sedeId) {
        return reservaMapper.toDTOList(reservaRepository.findBySedeEntityId(sedeId));
    }

    public List<ReservaDTO> findBySedeIdAndEstado(Long sedeId, EstadoReserva estado) {
        return reservaMapper.toDTOList(reservaRepository.findBySedeEntityIdAndEstado(sedeId, estado));
    }

    public boolean existsByClienteIdAndLibroIdAndSedeIdAndEstado(Long clienteId, Long libroId, Long sedeId,
                                                                 EstadoReserva estado) {
        return reservaRepository.existsByClienteEntityIdAndLibroEntityIdAndSedeEntityIdAndEstado(
                clienteId, libroId, sedeId, estado);
    }

    public Optional<ReservaDTO> actualizarEstado(Long id, EstadoReserva estado, String motivoRechazo,
                                                 LocalDateTime fechaRespuesta) {
        return reservaRepository.findById(id).map(existing -> {
            existing.setEstado(estado);
            existing.setMotivoRechazo(motivoRechazo);
            existing.setFechaRespuesta(fechaRespuesta);
            return reservaMapper.toDTO(reservaRepository.save(existing));
        });
    }

    public List<ConteoLibroDTO> contarReservasPorLibro() {
        return reservaRepository.contarReservasPorLibro();
    }
}
