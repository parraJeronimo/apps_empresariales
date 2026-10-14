package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarDTO;
import com.taller.bibliotecas.businessLayer.service.MovimientoEjemplarService;
import com.taller.bibliotecas.persistenceLayer.dao.EjemplarDAO;
import com.taller.bibliotecas.persistenceLayer.dao.MovimientoEjemplarDAO;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MovimientoEjemplarServiceImpl implements MovimientoEjemplarService {

    private final MovimientoEjemplarDAO movimientoDAO;
    private final EjemplarDAO ejemplarDAO;

    @Override
    public MovimientoEjemplarDTO registrarMovimiento(MovimientoEjemplarCreateDTO createDTO) {
        log.info("Registrando movimiento {} del ejemplar ID: {}", createDTO.getTipo(), createDTO.getEjemplarId());
        return movimientoDAO.save(createDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoEjemplarDTO> getHistorialByEjemplar(Long ejemplarId) {
        if (!ejemplarDAO.existsById(ejemplarId)) {
            throw new RecursoNoEncontradoException("Ejemplar no encontrado con ID: " + ejemplarId);
        }
        return movimientoDAO.findByEjemplarId(ejemplarId);
    }
}
