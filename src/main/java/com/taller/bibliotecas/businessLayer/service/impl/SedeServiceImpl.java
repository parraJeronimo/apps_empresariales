package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.SedeCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.persistenceLayer.dao.EjemplarDAO;
import com.taller.bibliotecas.persistenceLayer.dao.SedeDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
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
public class SedeServiceImpl implements SedeService {

    private final SedeDAO sedeDAO;
    private final EjemplarDAO ejemplarDAO;

    @Override
    public SedeDTO createSede(SedeCreateDTO createDTO) {
        log.info("Registrando nueva sede: {}", createDTO.getNombre());
        if (sedeDAO.existsByNombre(createDTO.getNombre())) {
            throw new SolicitudInvalidaException("Ya existe una sede registrada con el nombre: " + createDTO.getNombre());
        }
        return sedeDAO.save(createDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SedeDTO getSedeById(Long id) {
        return sedeDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sede no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SedeDTO> getAllSedes() {
        return sedeDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SedeDTO> getSedesActivas() {
        return sedeDAO.findByActiva(true);
    }

    @Override
    public SedeDTO updateSede(Long id, SedeUpdateDTO updateDTO) {
        if (updateDTO.getNombre() != null && sedeDAO.existsByNombreAndIdNot(updateDTO.getNombre(), id)) {
            throw new SolicitudInvalidaException("Ya existe otra sede registrada con el nombre: " + updateDTO.getNombre());
        }
        return sedeDAO.update(id, updateDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sede no encontrada con ID: " + id));
    }

    @Override
    public SedeDTO desactivarSede(Long id) {
        log.info("Desactivando sede ID: {}", id);
        SedeDTO sede = getSedeById(id);
        if (!Boolean.TRUE.equals(sede.getActiva())) {
            throw new SolicitudInvalidaException("La sede ya se encuentra desactivada");
        }
        long ejemplaresFuera = ejemplarDAO.countBySedeActualIdAndEstadoNot(id, EstadoEjemplar.DISPONIBLE);
        if (ejemplaresFuera > 0) {
            throw new SolicitudInvalidaException(
                    "No se puede desactivar la sede: tiene " + ejemplaresFuera + " ejemplar(es) prestados o en traslado");
        }
        return sedeDAO.cambiarEstado(id, false)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sede no encontrada con ID: " + id));
    }

    @Override
    public SedeDTO activarSede(Long id) {
        log.info("Activando sede ID: {}", id);
        SedeDTO sede = getSedeById(id);
        if (Boolean.TRUE.equals(sede.getActiva())) {
            throw new SolicitudInvalidaException("La sede ya se encuentra activa");
        }
        return sedeDAO.cambiarEstado(id, true)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sede no encontrada con ID: " + id));
    }
}
