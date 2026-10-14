package com.taller.bibliotecas.persistenceLayer.dao;

import com.taller.bibliotecas.businessLayer.dto.ConteoLibroDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoPrestamo;
import com.taller.bibliotecas.persistenceLayer.entity.PrestamoEntity;
import com.taller.bibliotecas.persistenceLayer.mapper.PrestamoMapper;
import com.taller.bibliotecas.persistenceLayer.repository.ClienteRepository;
import com.taller.bibliotecas.persistenceLayer.repository.EjemplarRepository;
import com.taller.bibliotecas.persistenceLayer.repository.PrestamoRepository;
import com.taller.bibliotecas.persistenceLayer.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PrestamoDAO {

    private final PrestamoRepository prestamoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final ClienteRepository clienteRepository;
    private final SedeRepository sedeRepository;
    private final PrestamoMapper prestamoMapper;

    public PrestamoDTO save(PrestamoCreateDTO createDTO, LocalDate fechaInicio, LocalDate fechaLimite) {
        PrestamoEntity entity = new PrestamoEntity();
        entity.setEjemplarEntity(ejemplarRepository.getReferenceById(createDTO.getEjemplarId()));
        entity.setClienteEntity(clienteRepository.getReferenceById(createDTO.getClienteId()));
        entity.setSedePrestamoEntity(sedeRepository.getReferenceById(createDTO.getSedeId()));
        entity.setFechaInicio(fechaInicio);
        entity.setFechaLimite(fechaLimite);
        entity.setEstado(EstadoPrestamo.ACTIVO);
        return prestamoMapper.toDTO(prestamoRepository.save(entity));
    }

    public Optional<PrestamoDTO> findById(Long id) {
        return prestamoRepository.findById(id).map(prestamoMapper::toDTO);
    }

    public List<PrestamoDTO> findAll() {
        return prestamoMapper.toDTOList(prestamoRepository.findAll());
    }

    public List<PrestamoDTO> findByClienteId(Long clienteId) {
        return prestamoMapper.toDTOList(prestamoRepository.findByClienteEntityId(clienteId));
    }

    public List<PrestamoDTO> findByEstado(EstadoPrestamo estado) {
        return prestamoMapper.toDTOList(prestamoRepository.findByEstado(estado));
    }

    public List<PrestamoDTO> findByFechaInicioBetween(LocalDate desde, LocalDate hasta) {
        return prestamoMapper.toDTOList(prestamoRepository.findByFechaInicioBetween(desde, hasta));
    }

    public List<PrestamoDTO> findByFechaDevolucionBetween(LocalDate desde, LocalDate hasta) {
        return prestamoMapper.toDTOList(prestamoRepository.findByFechaDevolucionBetween(desde, hasta));
    }

    public Optional<PrestamoDTO> registrarDevolucion(Long id, Long sedeDevolucionId, LocalDate fechaDevolucion,
                                                     boolean conRetraso, int diasRetraso) {
        return prestamoRepository.findById(id).map(existing -> {
            existing.setSedeDevolucionEntity(sedeRepository.getReferenceById(sedeDevolucionId));
            existing.setFechaDevolucion(fechaDevolucion);
            existing.setDevueltoConRetraso(conRetraso);
            existing.setDiasRetraso(diasRetraso);
            existing.setEstado(EstadoPrestamo.FINALIZADO);
            return prestamoMapper.toDTO(prestamoRepository.save(existing));
        });
    }

    public List<ConteoLibroDTO> contarPrestamosPorLibro() {
        return prestamoRepository.contarPrestamosPorLibro();
    }
}
