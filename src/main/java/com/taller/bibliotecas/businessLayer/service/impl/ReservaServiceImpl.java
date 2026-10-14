package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.ReservaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaRechazoDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.service.ClienteService;
import com.taller.bibliotecas.businessLayer.service.EjemplarService;
import com.taller.bibliotecas.businessLayer.service.LibroService;
import com.taller.bibliotecas.businessLayer.service.ReservaService;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.persistenceLayer.dao.ReservaDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoReserva;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReservaServiceImpl implements ReservaService {

    private final ReservaDAO reservaDAO;
    private final ClienteService clienteService;
    private final LibroService libroService;
    private final SedeService sedeService;
    private final EjemplarService ejemplarService;

    @Override
    public ReservaDTO createReserva(ReservaCreateDTO createDTO) {
        log.info("Creando reserva del libro ID: {} para el cliente ID: {}", createDTO.getLibroId(), createDTO.getClienteId());

        // Validar que existan cliente, libro y sede (404 si no)
        clienteService.getClienteById(createDTO.getClienteId());
        libroService.getLibroById(createDTO.getLibroId());
        SedeDTO sede = sedeService.getSedeById(createDTO.getSedeId());
        if (!Boolean.TRUE.equals(sede.getActiva())) {
            throw new SolicitudInvalidaException("No se pueden registrar reservas en una sede desactivada");
        }

        // RF-21: solo se reservan libros que NO estan disponibles en la sede solicitada
        if (ejemplarService.contarEjemplaresDisponibles(createDTO.getLibroId(), createDTO.getSedeId()) > 0) {
            throw new SolicitudInvalidaException(
                    "El libro tiene ejemplares disponibles en la sede " + sede.getNombre()
                            + ". Registre un prestamo en lugar de una reserva");
        }

        if (reservaDAO.existsByClienteIdAndLibroIdAndSedeIdAndEstado(
                createDTO.getClienteId(), createDTO.getLibroId(), createDTO.getSedeId(), EstadoReserva.PENDIENTE)) {
            throw new SolicitudInvalidaException(
                    "El cliente ya tiene una solicitud pendiente para este libro en la sede indicada");
        }

        ReservaDTO creada = reservaDAO.save(createDTO);
        log.info("Reserva creada con ID: {}", creada.getId());
        return creada;
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaDTO getReservaById(Long id) {
        return reservaDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> getAllReservas() {
        return reservaDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> getReservasByCliente(Long clienteId) {
        clienteService.getClienteById(clienteId);
        return reservaDAO.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaDTO> getReservasBySede(Long sedeId, EstadoReserva estado) {
        sedeService.getSedeById(sedeId);
        return estado == null
                ? reservaDAO.findBySedeId(sedeId)
                : reservaDAO.findBySedeIdAndEstado(sedeId, estado);
    }

    @Override
    public ReservaDTO aprobarReserva(Long id) {
        ReservaDTO reserva = getReservaById(id);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new SolicitudInvalidaException(
                    "Solo se pueden aprobar reservas pendientes. Estado actual: " + reserva.getEstado());
        }
        log.info("Aprobando reserva ID: {}", id);
        return reservaDAO.actualizarEstado(id, EstadoReserva.APROBADA, null, LocalDateTime.now())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id));
    }

    @Override
    public ReservaDTO rechazarReserva(Long id, ReservaRechazoDTO rechazoDTO) {
        String motivo = rechazoDTO.getMotivo();
        if (motivo == null || motivo.isBlank()) {
            throw new SolicitudInvalidaException("El motivo del rechazo es obligatorio");
        }
        ReservaDTO reserva = getReservaById(id);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new SolicitudInvalidaException(
                    "Solo se pueden rechazar reservas pendientes. Estado actual: " + reserva.getEstado());
        }
        log.info("Rechazando reserva ID: {}", id);
        return reservaDAO.actualizarEstado(id, EstadoReserva.RECHAZADA, motivo.trim(), LocalDateTime.now())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id));
    }

    @Override
    public ReservaDTO cancelarReserva(Long id) {
        ReservaDTO reserva = getReservaById(id);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE && reserva.getEstado() != EstadoReserva.APROBADA) {
            throw new SolicitudInvalidaException(
                    "Solo se pueden cancelar reservas pendientes o aprobadas. Estado actual: " + reserva.getEstado());
        }
        log.info("Cancelando reserva ID: {}", id);
        return reservaDAO.actualizarEstado(id, EstadoReserva.CANCELADA, null, null)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id));
    }
}
