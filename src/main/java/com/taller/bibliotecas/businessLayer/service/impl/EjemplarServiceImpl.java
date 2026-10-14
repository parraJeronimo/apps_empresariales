package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.EjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroDTO;
import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.service.EjemplarService;
import com.taller.bibliotecas.businessLayer.service.LibroService;
import com.taller.bibliotecas.businessLayer.service.MovimientoEjemplarService;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.persistenceLayer.dao.EjemplarDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.TipoMovimiento;
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
public class EjemplarServiceImpl implements EjemplarService {

    private final EjemplarDAO ejemplarDAO;
    private final LibroService libroService;
    private final SedeService sedeService;
    private final MovimientoEjemplarService movimientoService;

    @Override
    public EjemplarDTO createEjemplar(EjemplarCreateDTO createDTO) {
        log.info("Registrando ejemplar del libro ID: {}", createDTO.getLibroId());

        LibroDTO libro = libroService.getLibroById(createDTO.getLibroId());
        SedeDTO sede = sedeService.getSedeById(createDTO.getSedeId());
        if (!Boolean.TRUE.equals(sede.getActiva())) {
            throw new SolicitudInvalidaException("No se pueden registrar ejemplares en una sede desactivada");
        }

        String codigo = createDTO.getCodigo();
        if (codigo == null || codigo.isBlank()) {
            codigo = generarCodigo(libro);
        } else {
            codigo = codigo.trim();
            if (ejemplarDAO.existsByCodigo(codigo)) {
                throw new SolicitudInvalidaException("Ya existe un ejemplar con el codigo: " + codigo);
            }
        }
        createDTO.setCodigo(codigo);

        EjemplarDTO creado = ejemplarDAO.save(createDTO);

        movimientoService.registrarMovimiento(new MovimientoEjemplarCreateDTO(
                creado.getId(), TipoMovimiento.REGISTRO, null, createDTO.getSedeId(),
                "Registro del ejemplar en la sede " + sede.getNombre()));

        log.info("Ejemplar registrado con ID: {} y codigo: {}", creado.getId(), codigo);
        return creado;
    }

    @Override
    @Transactional(readOnly = true)
    public EjemplarDTO getEjemplarById(Long id) {
        return ejemplarDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ejemplar no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EjemplarDTO> getAllEjemplares() {
        return ejemplarDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public EjemplarDTO getEjemplarByCodigo(String codigo) {
        return ejemplarDAO.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ejemplar no encontrado con codigo: " + codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EjemplarDTO> getEjemplaresByLibro(Long libroId) {
        libroService.getLibroById(libroId);
        return ejemplarDAO.findByLibroId(libroId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EjemplarDTO> getEjemplaresBySede(Long sedeId) {
        sedeService.getSedeById(sedeId);
        return ejemplarDAO.findBySedeActualId(sedeId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarEjemplaresDisponibles(Long libroId, Long sedeId) {
        return ejemplarDAO.countByLibroIdAndSedeActualIdAndEstado(libroId, sedeId, EstadoEjemplar.DISPONIBLE);
    }

    @Override
    public EjemplarDTO actualizarEstadoYUbicacion(Long ejemplarId, EstadoEjemplar estado, Long sedeActualId) {
        return ejemplarDAO.actualizarEstadoYSede(ejemplarId, estado, sedeActualId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ejemplar no encontrado con ID: " + ejemplarId));
    }

    /** Genera un codigo consecutivo por libro: ISBN (solo alfanumerico) + "-" + numero de 3 digitos. */
    private String generarCodigo(LibroDTO libro) {
        String base = libro.getIsbn().replaceAll("[^A-Za-z0-9]", "");
        long consecutivo = ejemplarDAO.countByLibroId(libro.getId()) + 1;
        String candidato = String.format("%s-%03d", base, consecutivo);
        while (ejemplarDAO.existsByCodigo(candidato)) {
            consecutivo++;
            candidato = String.format("%s-%03d", base, consecutivo);
        }
        return candidato;
    }
}
