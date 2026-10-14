package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.ClienteDTO;
import com.taller.bibliotecas.businessLayer.dto.ConteoLibroDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroSolicitadoDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteClientesDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteDevolucionesDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteLibrosDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteMovimientosDTO;
import com.taller.bibliotecas.businessLayer.dto.ReportePrestamosDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.dto.TrasladoDTO;
import com.taller.bibliotecas.businessLayer.service.ClienteService;
import com.taller.bibliotecas.businessLayer.service.LibroService;
import com.taller.bibliotecas.businessLayer.service.PrestamoService;
import com.taller.bibliotecas.businessLayer.service.ReporteService;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.businessLayer.service.TrasladoService;
import com.taller.bibliotecas.persistenceLayer.dao.ClienteDAO;
import com.taller.bibliotecas.persistenceLayer.dao.EjemplarDAO;
import com.taller.bibliotecas.persistenceLayer.dao.LibroDAO;
import com.taller.bibliotecas.persistenceLayer.dao.PrestamoDAO;
import com.taller.bibliotecas.persistenceLayer.dao.ReservaDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoPrestamo;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoTraslado;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReporteServiceImpl implements ReporteService {

    private final PrestamoService prestamoService;
    private final TrasladoService trasladoService;
    private final ClienteService clienteService;
    private final SedeService sedeService;
    private final LibroService libroService;
    private final PrestamoDAO prestamoDAO;
    private final ReservaDAO reservaDAO;
    private final LibroDAO libroDAO;
    private final EjemplarDAO ejemplarDAO;
    private final ClienteDAO clienteDAO;

    @Override
    public ReportePrestamosDTO generarReportePrestamos(LocalDate desde, LocalDate hasta, Long sedeId) {
        validarRango(desde, hasta);
        validarSede(sedeId);

        List<PrestamoDTO> prestamos = prestamoService.getPrestamosPorRangoDeInicio(desde, hasta).stream()
                .filter(p -> sedeId == null || sedeId.equals(p.getSedePrestamoId()))
                .toList();

        ReportePrestamosDTO reporte = new ReportePrestamosDTO();
        reporte.setDesde(desde);
        reporte.setHasta(hasta);
        reporte.setSedeId(sedeId);
        reporte.setTotalPrestamos(prestamos.size());
        reporte.setActivos((int) prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.ACTIVO && !Boolean.TRUE.equals(p.getVencido())).count());
        reporte.setVencidos((int) prestamos.stream().filter(p -> Boolean.TRUE.equals(p.getVencido())).count());
        reporte.setFinalizados((int) prestamos.stream().filter(p -> p.getEstado() == EstadoPrestamo.FINALIZADO).count());
        reporte.setDevueltosConRetraso((int) prestamos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.FINALIZADO && Boolean.TRUE.equals(p.getDevueltoConRetraso()))
                .count());
        reporte.setPrestamos(prestamos);
        return reporte;
    }

    @Override
    public ReporteDevolucionesDTO generarReporteDevoluciones(LocalDate desde, LocalDate hasta, Long sedeId) {
        validarRango(desde, hasta);
        validarSede(sedeId);

        List<PrestamoDTO> devoluciones = prestamoService.getDevolucionesPorRango(desde, hasta).stream()
                .filter(p -> sedeId == null || sedeId.equals(p.getSedeDevolucionId()))
                .toList();

        int conRetraso = (int) devoluciones.stream().filter(p -> Boolean.TRUE.equals(p.getDevueltoConRetraso())).count();

        ReporteDevolucionesDTO reporte = new ReporteDevolucionesDTO();
        reporte.setDesde(desde);
        reporte.setHasta(hasta);
        reporte.setSedeId(sedeId);
        reporte.setTotalDevoluciones(devoluciones.size());
        reporte.setConRetraso(conRetraso);
        reporte.setATiempo(devoluciones.size() - conRetraso);
        reporte.setEnSedeDiferente((int) devoluciones.stream()
                .filter(p -> !Objects.equals(p.getSedePrestamoId(), p.getSedeDevolucionId())).count());
        reporte.setDevoluciones(devoluciones);
        return reporte;
    }

    @Override
    public ReporteLibrosDTO generarReporteLibros() {
        Map<EstadoEjemplar, Long> porEstado = new EnumMap<>(EstadoEjemplar.class);
        for (EstadoEjemplar estado : EstadoEjemplar.values()) {
            porEstado.put(estado, ejemplarDAO.countByEstado(estado));
        }

        Map<String, Long> porSede = new LinkedHashMap<>();
        for (SedeDTO sede : sedeService.getAllSedes()) {
            porSede.put(sede.getNombre(), ejemplarDAO.countBySedeActualId(sede.getId()));
        }

        ReporteLibrosDTO reporte = new ReporteLibrosDTO();
        reporte.setTotalLibros(libroDAO.count());
        reporte.setTotalEjemplares(ejemplarDAO.count());
        reporte.setEjemplaresPorEstado(porEstado);
        reporte.setEjemplaresPorSede(porSede);
        return reporte;
    }

    @Override
    public ReporteClientesDTO generarReporteClientes() {
        List<PrestamoDTO> activos = prestamoService.getPrestamosActivos();

        Set<Long> clientesConActivos = new LinkedHashSet<>();
        Set<Long> clientesConVencidos = new LinkedHashSet<>();
        for (PrestamoDTO prestamo : activos) {
            clientesConActivos.add(prestamo.getClienteId());
            if (Boolean.TRUE.equals(prestamo.getVencido())) {
                clientesConVencidos.add(prestamo.getClienteId());
            }
        }

        List<ClienteDTO> conRetraso = clientesConVencidos.stream().map(clienteService::getClienteById).toList();

        ReporteClientesDTO reporte = new ReporteClientesDTO();
        reporte.setTotalClientes(clienteDAO.count());
        reporte.setClientesConPrestamosActivos(clientesConActivos.size());
        reporte.setClientesConPrestamosVencidos(clientesConVencidos.size());
        reporte.setClientesConRetraso(conRetraso);
        return reporte;
    }

    @Override
    public ReporteMovimientosDTO generarReporteMovimientos(LocalDate desde, LocalDate hasta, Long sedeId) {
        validarRango(desde, hasta);
        validarSede(sedeId);

        List<TrasladoDTO> traslados = trasladoService.getTrasladosPorRango(desde, hasta).stream()
                .filter(t -> sedeId == null || sedeId.equals(t.getSedeOrigenId()) || sedeId.equals(t.getSedeDestinoId()))
                .toList();

        ReporteMovimientosDTO reporte = new ReporteMovimientosDTO();
        reporte.setDesde(desde);
        reporte.setHasta(hasta);
        reporte.setSedeId(sedeId);
        reporte.setTotalTraslados(traslados.size());
        reporte.setEnTransito((int) traslados.stream().filter(t -> t.getEstado() == EstadoTraslado.EN_TRANSITO).count());
        reporte.setRecibidos((int) traslados.stream().filter(t -> t.getEstado() == EstadoTraslado.RECIBIDO).count());
        reporte.setCancelados((int) traslados.stream().filter(t -> t.getEstado() == EstadoTraslado.CANCELADO).count());
        reporte.setTraslados(traslados);
        return reporte;
    }

    @Override
    public List<LibroSolicitadoDTO> getLibrosMasSolicitados(int limite) {
        if (limite <= 0) {
            throw new SolicitudInvalidaException("El limite debe ser mayor a cero");
        }

        // libroId -> [prestamos, reservas]
        Map<Long, long[]> acumulado = new HashMap<>();
        for (ConteoLibroDTO conteo : prestamoDAO.contarPrestamosPorLibro()) {
            acumulado.computeIfAbsent(conteo.getLibroId(), k -> new long[2])[0] = conteo.getTotal();
        }
        for (ConteoLibroDTO conteo : reservaDAO.contarReservasPorLibro()) {
            acumulado.computeIfAbsent(conteo.getLibroId(), k -> new long[2])[1] = conteo.getTotal();
        }

        return acumulado.entrySet().stream()
                .sorted(Comparator
                        .comparingLong((Map.Entry<Long, long[]> e) -> e.getValue()[0] + e.getValue()[1])
                        .reversed()
                        .thenComparing(e -> e.getKey()))
                .limit(limite)
                .map(e -> {
                    LibroDTO libro = libroService.getLibroById(e.getKey());
                    LibroSolicitadoDTO dto = new LibroSolicitadoDTO();
                    dto.setLibroId(libro.getId());
                    dto.setTitulo(libro.getTitulo());
                    dto.setAutor(libro.getAutor());
                    dto.setTotalPrestamos(e.getValue()[0]);
                    dto.setTotalReservas(e.getValue()[1]);
                    dto.setTotalSolicitudes(e.getValue()[0] + e.getValue()[1]);
                    return dto;
                })
                .toList();
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new SolicitudInvalidaException("Las fechas desde y hasta son obligatorias");
        }
        if (desde.isAfter(hasta)) {
            throw new SolicitudInvalidaException("La fecha desde no puede ser posterior a la fecha hasta");
        }
    }

    private void validarSede(Long sedeId) {
        if (sedeId != null) {
            sedeService.getSedeById(sedeId);
        }
    }
}
