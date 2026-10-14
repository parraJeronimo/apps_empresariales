package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.ClienteDTO;
import com.taller.bibliotecas.businessLayer.dto.DevolucionDTO;
import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.businessLayer.dto.FiltroPrestamo;
import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.service.ClienteService;
import com.taller.bibliotecas.businessLayer.service.EjemplarService;
import com.taller.bibliotecas.businessLayer.service.MovimientoEjemplarService;
import com.taller.bibliotecas.businessLayer.service.PrestamoService;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.persistenceLayer.dao.PrestamoDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoPrestamo;
import com.taller.bibliotecas.persistenceLayer.entity.TipoMovimiento;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PrestamoServiceImpl implements PrestamoService {

    /** Plazo por defecto de un prestamo. Pendiente de definir con el negocio (documento, seccion 3). */
    static final int DIAS_PRESTAMO_POR_DEFECTO = 14;
    static final int DIAS_PRESTAMO_MAXIMO = 30;

    private final PrestamoDAO prestamoDAO;
    private final ClienteService clienteService;
    private final SedeService sedeService;
    private final EjemplarService ejemplarService;
    private final MovimientoEjemplarService movimientoService;

    @Override
    public PrestamoDTO createPrestamo(PrestamoCreateDTO createDTO) {
        log.info("Registrando prestamo del ejemplar ID: {} al cliente ID: {}",
                createDTO.getEjemplarId(), createDTO.getClienteId());

        int dias = createDTO.getDiasPrestamo() == null ? DIAS_PRESTAMO_POR_DEFECTO : createDTO.getDiasPrestamo();
        if (dias < 1 || dias > DIAS_PRESTAMO_MAXIMO) {
            throw new SolicitudInvalidaException(
                    "Los dias de prestamo deben estar entre 1 y " + DIAS_PRESTAMO_MAXIMO);
        }

        ClienteDTO cliente = clienteService.getClienteById(createDTO.getClienteId());
        SedeDTO sede = sedeService.getSedeById(createDTO.getSedeId());
        if (!Boolean.TRUE.equals(sede.getActiva())) {
            throw new SolicitudInvalidaException("No se pueden registrar prestamos en una sede desactivada");
        }

        EjemplarDTO ejemplar = ejemplarService.getEjemplarById(createDTO.getEjemplarId());
        if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
            throw new SolicitudInvalidaException(
                    "El ejemplar no esta disponible para prestamo. Estado actual: " + ejemplar.getEstado());
        }
        if (!Objects.equals(ejemplar.getSedeActualId(), createDTO.getSedeId())) {
            throw new SolicitudInvalidaException(
                    "El ejemplar se encuentra en la sede " + ejemplar.getSedeActualNombre()
                            + " y no puede prestarse desde la sede " + sede.getNombre());
        }

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaLimite = fechaInicio.plusDays(dias);

        PrestamoDTO creado = prestamoDAO.save(createDTO, fechaInicio, fechaLimite);

        ejemplarService.actualizarEstadoYUbicacion(createDTO.getEjemplarId(), EstadoEjemplar.PRESTADO, null);
        movimientoService.registrarMovimiento(new MovimientoEjemplarCreateDTO(
                createDTO.getEjemplarId(), TipoMovimiento.PRESTAMO, createDTO.getSedeId(), null,
                "Prestado al cliente " + cliente.getNombre() + ". Fecha limite: " + fechaLimite));

        log.info("Prestamo registrado con ID: {}", creado.getId());
        return enriquecer(creado);
    }

    @Override
    @Transactional(readOnly = true)
    public PrestamoDTO getPrestamoById(Long id) {
        return prestamoDAO.findById(id)
                .map(this::enriquecer)
                .orElseThrow(() -> new RecursoNoEncontradoException("Prestamo no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> getAllPrestamos() {
        return prestamoDAO.findAll().stream().map(this::enriquecer).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> getPrestamosByCliente(Long clienteId, FiltroPrestamo filtro) {
        clienteService.getClienteById(clienteId);
        List<PrestamoDTO> prestamos = prestamoDAO.findByClienteId(clienteId).stream().map(this::enriquecer).toList();
        if (filtro == null) {
            return prestamos;
        }
        return prestamos.stream()
                .filter(p -> switch (filtro) {
                    case ACTIVOS -> p.getEstado() == EstadoPrestamo.ACTIVO && !Boolean.TRUE.equals(p.getVencido());
                    case VENCIDOS -> Boolean.TRUE.equals(p.getVencido());
                    case FINALIZADOS -> p.getEstado() == EstadoPrestamo.FINALIZADO;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> getHistorialByCliente(Long clienteId) {
        clienteService.getClienteById(clienteId);
        return prestamoDAO.findByClienteId(clienteId).stream()
                .map(this::enriquecer)
                .sorted(Comparator.comparing(PrestamoDTO::getFechaInicio, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> getPrestamosActivos() {
        return prestamoDAO.findByEstado(EstadoPrestamo.ACTIVO).stream().map(this::enriquecer).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> getPrestamosVencidos() {
        return getPrestamosActivos().stream().filter(p -> Boolean.TRUE.equals(p.getVencido())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> getPrestamosPorRangoDeInicio(LocalDate desde, LocalDate hasta) {
        return prestamoDAO.findByFechaInicioBetween(desde, hasta).stream().map(this::enriquecer).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> getDevolucionesPorRango(LocalDate desde, LocalDate hasta) {
        return prestamoDAO.findByFechaDevolucionBetween(desde, hasta).stream().map(this::enriquecer).toList();
    }

    @Override
    public PrestamoDTO registrarDevolucion(Long prestamoId, DevolucionDTO devolucionDTO) {
        log.info("Registrando devolucion del prestamo ID: {} en la sede ID: {}",
                prestamoId, devolucionDTO.getSedeDevolucionId());

        PrestamoDTO prestamo = prestamoDAO.findById(prestamoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Prestamo no encontrado con ID: " + prestamoId));
        if (prestamo.getEstado() == EstadoPrestamo.FINALIZADO) {
            throw new SolicitudInvalidaException("El prestamo ya fue devuelto anteriormente");
        }

        SedeDTO sedeDevolucion = sedeService.getSedeById(devolucionDTO.getSedeDevolucionId());
        if (!Boolean.TRUE.equals(sedeDevolucion.getActiva())) {
            throw new SolicitudInvalidaException("No se pueden recibir devoluciones en una sede desactivada");
        }

        // RF-15: determinar si se devolvio despues de la fecha limite
        LocalDate hoy = LocalDate.now();
        boolean conRetraso = hoy.isAfter(prestamo.getFechaLimite());
        int diasRetraso = conRetraso ? (int) ChronoUnit.DAYS.between(prestamo.getFechaLimite(), hoy) : 0;

        // RF-16 / RF-17: se guarda la sede donde se devolvio (puede ser distinta a la del prestamo)
        PrestamoDTO finalizado = prestamoDAO
                .registrarDevolucion(prestamoId, devolucionDTO.getSedeDevolucionId(), hoy, conRetraso, diasRetraso)
                .orElseThrow(() -> new RecursoNoEncontradoException("Prestamo no encontrado con ID: " + prestamoId));

        // El ejemplar queda disponible en la sede donde fue devuelto
        ejemplarService.actualizarEstadoYUbicacion(
                prestamo.getEjemplarId(), EstadoEjemplar.DISPONIBLE, devolucionDTO.getSedeDevolucionId());
        movimientoService.registrarMovimiento(new MovimientoEjemplarCreateDTO(
                prestamo.getEjemplarId(), TipoMovimiento.DEVOLUCION,
                prestamo.getSedePrestamoId(), devolucionDTO.getSedeDevolucionId(),
                "Devuelto en la sede " + sedeDevolucion.getNombre()
                        + (conRetraso ? " con " + diasRetraso + " dia(s) de retraso" : " a tiempo")));

        log.info("Devolucion registrada para el prestamo ID: {} (retraso: {})", prestamoId, conRetraso);
        return enriquecer(finalizado);
    }

    /**
     * Completa los campos calculados: un prestamo ACTIVO esta vencido si hoy es posterior a su fecha limite,
     * y en ese caso se informan los dias de retraso acumulados hasta hoy.
     */
    private PrestamoDTO enriquecer(PrestamoDTO prestamo) {
        boolean activo = prestamo.getEstado() == EstadoPrestamo.ACTIVO;
        LocalDate hoy = LocalDate.now();
        boolean vencido = activo && prestamo.getFechaLimite() != null && hoy.isAfter(prestamo.getFechaLimite());
        prestamo.setVencido(vencido);
        if (activo) {
            prestamo.setDiasRetraso(vencido ? (int) ChronoUnit.DAYS.between(prestamo.getFechaLimite(), hoy) : 0);
        }
        return prestamo;
    }
}
