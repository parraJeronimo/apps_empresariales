package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.dto.TrasladoCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.TrasladoDTO;
import com.taller.bibliotecas.businessLayer.service.EjemplarService;
import com.taller.bibliotecas.businessLayer.service.MovimientoEjemplarService;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.businessLayer.service.TrasladoService;
import com.taller.bibliotecas.persistenceLayer.dao.TrasladoDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoTraslado;
import com.taller.bibliotecas.persistenceLayer.entity.TipoMovimiento;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TrasladoServiceImpl implements TrasladoService {

    private final TrasladoDAO trasladoDAO;
    private final EjemplarService ejemplarService;
    private final SedeService sedeService;
    private final MovimientoEjemplarService movimientoService;

    @Override
    public TrasladoDTO createTraslado(TrasladoCreateDTO createDTO) {
        log.info("Registrando traslado del ejemplar ID: {} hacia la sede ID: {}",
                createDTO.getEjemplarId(), createDTO.getSedeDestinoId());

        EjemplarDTO ejemplar = ejemplarService.getEjemplarById(createDTO.getEjemplarId());
        if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
            throw new SolicitudInvalidaException(
                    "Solo se pueden trasladar ejemplares disponibles. Estado actual: " + ejemplar.getEstado());
        }

        SedeDTO destino = sedeService.getSedeById(createDTO.getSedeDestinoId());
        if (!Boolean.TRUE.equals(destino.getActiva())) {
            throw new SolicitudInvalidaException("La sede destino se encuentra desactivada");
        }

        Long sedeOrigenId = ejemplar.getSedeActualId();
        if (sedeOrigenId.equals(createDTO.getSedeDestinoId())) {
            throw new SolicitudInvalidaException(
                    "La sede destino debe ser diferente a la sede donde se encuentra el ejemplar");
        }

        TrasladoDTO creado = trasladoDAO.save(createDTO, sedeOrigenId);

        ejemplarService.actualizarEstadoYUbicacion(createDTO.getEjemplarId(), EstadoEjemplar.EN_TRASLADO, null);
        movimientoService.registrarMovimiento(new MovimientoEjemplarCreateDTO(
                createDTO.getEjemplarId(), TipoMovimiento.TRASLADO_SALIDA, sedeOrigenId, createDTO.getSedeDestinoId(),
                "Sale en traslado hacia la sede " + destino.getNombre()));

        log.info("Traslado registrado con ID: {}", creado.getId());
        return creado;
    }

    @Override
    @Transactional(readOnly = true)
    public TrasladoDTO getTrasladoById(Long id) {
        return trasladoDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Traslado no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrasladoDTO> getAllTraslados() {
        return trasladoDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrasladoDTO> getTrasladosBySede(Long sedeId, EstadoTraslado estado) {
        sedeService.getSedeById(sedeId);
        List<TrasladoDTO> traslados = trasladoDAO.findBySedeId(sedeId);
        if (estado == null) {
            return traslados;
        }
        return traslados.stream().filter(t -> t.getEstado() == estado).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrasladoDTO> getTrasladosPorRango(LocalDate desde, LocalDate hasta) {
        return trasladoDAO.findByFechaSolicitudBetween(desde.atStartOfDay(), hasta.atTime(LocalTime.MAX));
    }

    @Override
    public TrasladoDTO confirmarRecepcion(Long id) {
        TrasladoDTO traslado = getTrasladoById(id);
        if (traslado.getEstado() != EstadoTraslado.EN_TRANSITO) {
            throw new SolicitudInvalidaException(
                    "Solo se puede confirmar la recepcion de traslados en transito. Estado actual: " + traslado.getEstado());
        }
        SedeDTO destino = sedeService.getSedeById(traslado.getSedeDestinoId());
        if (!Boolean.TRUE.equals(destino.getActiva())) {
            throw new SolicitudInvalidaException("La sede destino se encuentra desactivada");
        }

        TrasladoDTO recibido = trasladoDAO.actualizarEstado(id, EstadoTraslado.RECIBIDO, LocalDateTime.now())
                .orElseThrow(() -> new RecursoNoEncontradoException("Traslado no encontrado con ID: " + id));

        // RF-19: la ubicacion del ejemplar se actualiza automaticamente al confirmar la recepcion
        ejemplarService.actualizarEstadoYUbicacion(
                traslado.getEjemplarId(), EstadoEjemplar.DISPONIBLE, traslado.getSedeDestinoId());
        movimientoService.registrarMovimiento(new MovimientoEjemplarCreateDTO(
                traslado.getEjemplarId(), TipoMovimiento.TRASLADO_RECEPCION,
                traslado.getSedeOrigenId(), traslado.getSedeDestinoId(),
                "Recepcion confirmada en la sede " + destino.getNombre()));

        log.info("Recepcion confirmada para el traslado ID: {}", id);
        return recibido;
    }

    @Override
    public TrasladoDTO cancelarTraslado(Long id) {
        TrasladoDTO traslado = getTrasladoById(id);
        if (traslado.getEstado() != EstadoTraslado.EN_TRANSITO) {
            throw new SolicitudInvalidaException(
                    "Solo se pueden cancelar traslados en transito. Estado actual: " + traslado.getEstado());
        }

        TrasladoDTO cancelado = trasladoDAO.actualizarEstado(id, EstadoTraslado.CANCELADO, null)
                .orElseThrow(() -> new RecursoNoEncontradoException("Traslado no encontrado con ID: " + id));

        // El ejemplar nunca salio de la sede de origen: vuelve a estar disponible alli
        ejemplarService.actualizarEstadoYUbicacion(traslado.getEjemplarId(), EstadoEjemplar.DISPONIBLE, null);
        movimientoService.registrarMovimiento(new MovimientoEjemplarCreateDTO(
                traslado.getEjemplarId(), TipoMovimiento.TRASLADO_CANCELADO,
                traslado.getSedeOrigenId(), traslado.getSedeDestinoId(),
                "Traslado cancelado; el ejemplar permanece en la sede de origen"));

        log.info("Traslado ID: {} cancelado", id);
        return cancelado;
    }
}
