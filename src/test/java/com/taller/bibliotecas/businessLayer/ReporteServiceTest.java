package com.taller.bibliotecas.businessLayer;

import static com.taller.bibliotecas.businessLayer.TestData.cliente;
import static com.taller.bibliotecas.businessLayer.TestData.libro;
import static com.taller.bibliotecas.businessLayer.TestData.prestamo;
import static com.taller.bibliotecas.businessLayer.TestData.sede;
import static com.taller.bibliotecas.businessLayer.TestData.traslado;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.ConteoLibroDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroSolicitadoDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteClientesDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteDevolucionesDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteLibrosDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteMovimientosDTO;
import com.taller.bibliotecas.businessLayer.dto.ReportePrestamosDTO;
import com.taller.bibliotecas.businessLayer.dto.TrasladoDTO;
import com.taller.bibliotecas.businessLayer.service.ClienteService;
import com.taller.bibliotecas.businessLayer.service.LibroService;
import com.taller.bibliotecas.businessLayer.service.PrestamoService;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.businessLayer.service.TrasladoService;
import com.taller.bibliotecas.businessLayer.service.impl.ReporteServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.ClienteDAO;
import com.taller.bibliotecas.persistenceLayer.dao.EjemplarDAO;
import com.taller.bibliotecas.persistenceLayer.dao.LibroDAO;
import com.taller.bibliotecas.persistenceLayer.dao.PrestamoDAO;
import com.taller.bibliotecas.persistenceLayer.dao.ReservaDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoPrestamo;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoTraslado;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteService - Unit Tests (RF-24 y libros mas solicitados)")
class ReporteServiceTest {

    @Mock
    private PrestamoService prestamoService;

    @Mock
    private TrasladoService trasladoService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private SedeService sedeService;

    @Mock
    private LibroService libroService;

    @Mock
    private PrestamoDAO prestamoDao;

    @Mock
    private ReservaDAO reservaDao;

    @Mock
    private LibroDAO libroDao;

    @Mock
    private EjemplarDAO ejemplarDao;

    @Mock
    private ClienteDAO clienteDao;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private final LocalDate desde = LocalDate.of(2026, 10, 1);
    private final LocalDate hasta = LocalDate.of(2026, 10, 31);

    // ---------- helpers ----------

    /** Prestamo hecho en la sede indicada (cliente 2). */
    private PrestamoDTO prestamoEnSede(Long id, Long sedeId, EstadoPrestamo estado, boolean vencido, boolean conRetraso) {
        PrestamoDTO p = prestamo(id, estado, desde, hasta);
        p.setSedePrestamoId(sedeId);
        p.setVencido(vencido);
        p.setDevueltoConRetraso(conRetraso);
        return p;
    }

    /** Devolucion de un prestamo hecho en sedePrestamo y devuelto en sedeDevolucion. */
    private PrestamoDTO devolucion(Long id, Long sedePrestamo, Long sedeDevolucion, boolean conRetraso) {
        PrestamoDTO p = prestamoEnSede(id, sedePrestamo, EstadoPrestamo.FINALIZADO, false, conRetraso);
        p.setSedeDevolucionId(sedeDevolucion);
        return p;
    }

    private TrasladoDTO trasladoEntre(Long id, Long origen, Long destino, EstadoTraslado estado) {
        TrasladoDTO t = traslado(id, estado);
        t.setSedeOrigenId(origen);
        t.setSedeDestinoId(destino);
        return t;
    }

    // ---------- reporte de prestamos ----------

    private List<PrestamoDTO> prestamosDelPeriodo() {
        return List.of(
                prestamoEnSede(1L, 1L, EstadoPrestamo.ACTIVO, false, false),      // vigente
                prestamoEnSede(2L, 1L, EstadoPrestamo.ACTIVO, true, false),       // vencido
                prestamoEnSede(3L, 1L, EstadoPrestamo.FINALIZADO, false, true),   // devuelto con retraso
                prestamoEnSede(4L, 1L, EstadoPrestamo.FINALIZADO, false, false),  // devuelto a tiempo
                prestamoEnSede(5L, 2L, EstadoPrestamo.ACTIVO, false, false));     // vigente en otra sede
    }

    @Test
    void testReportePrestamos_TodasLasSedes() {
        when(prestamoService.getPrestamosPorRangoDeInicio(desde, hasta)).thenReturn(prestamosDelPeriodo());

        ReportePrestamosDTO result = reporteService.generarReportePrestamos(desde, hasta, null);

        assertEquals(5, result.getTotalPrestamos());
        assertEquals(2, result.getActivos());
        assertEquals(1, result.getVencidos());
        assertEquals(2, result.getFinalizados());
        assertEquals(1, result.getDevueltosConRetraso());
        assertEquals(5, result.getPrestamos().size());
        assertEquals(desde, result.getDesde());
        assertEquals(hasta, result.getHasta());
        assertNull(result.getSedeId());
        verifyNoInteractions(sedeService);
    }

    @Test
    void testReportePrestamos_FiltradoPorSede() {
        when(sedeService.getSedeById(1L)).thenReturn(sede(1L, "Central", true));
        when(prestamoService.getPrestamosPorRangoDeInicio(desde, hasta)).thenReturn(prestamosDelPeriodo());

        ReportePrestamosDTO result = reporteService.generarReportePrestamos(desde, hasta, 1L);

        assertEquals(4, result.getTotalPrestamos());
        assertEquals(1, result.getActivos());
        assertEquals(1L, result.getSedeId());
    }

    @Test
    void testReportePrestamos_SinPrestamos() {
        when(prestamoService.getPrestamosPorRangoDeInicio(desde, hasta)).thenReturn(List.of());

        ReportePrestamosDTO result = reporteService.generarReportePrestamos(desde, hasta, null);

        assertEquals(0, result.getTotalPrestamos());
        assertTrue(result.getPrestamos().isEmpty());
    }

    @Test
    void testReportePrestamos_RangoInvalido() {
        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> reporteService.generarReportePrestamos(hasta, desde, null));

        assertEquals("La fecha desde no puede ser posterior a la fecha hasta", ex.getMessage());
        verifyNoInteractions(prestamoService);
    }

    @Test
    void testReportePrestamos_FechasObligatorias() {
        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> reporteService.generarReportePrestamos(null, hasta, null));

        assertEquals("Las fechas desde y hasta son obligatorias", ex.getMessage());
        assertThrows(SolicitudInvalidaException.class,
                () -> reporteService.generarReportePrestamos(desde, null, null));
        verifyNoInteractions(prestamoService);
    }

    @Test
    void testReportePrestamos_SedeNoExiste() {
        when(sedeService.getSedeById(99L)).thenThrow(new RecursoNoEncontradoException("Sede no encontrada con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> reporteService.generarReportePrestamos(desde, hasta, 99L));

        verifyNoInteractions(prestamoService);
    }

    // ---------- reporte de devoluciones ----------

    private List<PrestamoDTO> devolucionesDelPeriodo() {
        return List.of(
                devolucion(1L, 1L, 1L, false),   // misma sede, a tiempo
                devolucion(2L, 1L, 2L, true),    // otra sede, con retraso
                devolucion(3L, 2L, 2L, false));  // misma sede, a tiempo
    }

    @Test
    void testReporteDevoluciones_TodasLasSedes() {
        when(prestamoService.getDevolucionesPorRango(desde, hasta)).thenReturn(devolucionesDelPeriodo());

        ReporteDevolucionesDTO result = reporteService.generarReporteDevoluciones(desde, hasta, null);

        assertEquals(3, result.getTotalDevoluciones());
        assertEquals(1, result.getConRetraso());
        assertEquals(2, result.getATiempo());
        assertEquals(1, result.getEnSedeDiferente());
        assertEquals(3, result.getDevoluciones().size());
    }

    @Test
    void testReporteDevoluciones_FiltradoPorSedeDeDevolucion() {
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Norte", true));
        when(prestamoService.getDevolucionesPorRango(desde, hasta)).thenReturn(devolucionesDelPeriodo());

        ReporteDevolucionesDTO result = reporteService.generarReporteDevoluciones(desde, hasta, 2L);

        assertEquals(2, result.getTotalDevoluciones());
        assertEquals(1, result.getConRetraso());
        assertEquals(1, result.getATiempo());
        assertEquals(1, result.getEnSedeDiferente());
    }

    @Test
    void testReporteDevoluciones_RangoInvalido() {
        assertThrows(SolicitudInvalidaException.class,
                () -> reporteService.generarReporteDevoluciones(hasta, desde, null));

        verifyNoInteractions(prestamoService);
    }

    // ---------- reporte de libros ----------

    @Test
    void testReporteLibros_TotalesPorEstadoYPorSede() {
        when(libroDao.count()).thenReturn(10L);
        when(ejemplarDao.count()).thenReturn(30L);
        when(ejemplarDao.countByEstado(EstadoEjemplar.DISPONIBLE)).thenReturn(20L);
        when(ejemplarDao.countByEstado(EstadoEjemplar.PRESTADO)).thenReturn(8L);
        when(ejemplarDao.countByEstado(EstadoEjemplar.EN_TRASLADO)).thenReturn(2L);
        when(sedeService.getAllSedes()).thenReturn(List.of(sede(1L, "Central", true), sede(2L, "Norte", true)));
        when(ejemplarDao.countBySedeActualId(1L)).thenReturn(18L);
        when(ejemplarDao.countBySedeActualId(2L)).thenReturn(12L);

        ReporteLibrosDTO result = reporteService.generarReporteLibros();

        assertEquals(10L, result.getTotalLibros());
        assertEquals(30L, result.getTotalEjemplares());
        assertEquals(20L, result.getEjemplaresPorEstado().get(EstadoEjemplar.DISPONIBLE));
        assertEquals(8L, result.getEjemplaresPorEstado().get(EstadoEjemplar.PRESTADO));
        assertEquals(2L, result.getEjemplaresPorEstado().get(EstadoEjemplar.EN_TRASLADO));
        assertEquals(18L, result.getEjemplaresPorSede().get("Central"));
        assertEquals(12L, result.getEjemplaresPorSede().get("Norte"));
    }

    // ---------- reporte de clientes ----------

    @Test
    void testReporteClientes_ClientesConPrestamosActivosYVencidos() {
        PrestamoDTO c1Vigente = prestamoEnSede(1L, 1L, EstadoPrestamo.ACTIVO, false, false);
        c1Vigente.setClienteId(1L);
        PrestamoDTO c1Vencido = prestamoEnSede(2L, 1L, EstadoPrestamo.ACTIVO, true, false);
        c1Vencido.setClienteId(1L);
        PrestamoDTO c2Vigente = prestamoEnSede(3L, 1L, EstadoPrestamo.ACTIVO, false, false);
        c2Vigente.setClienteId(2L);
        PrestamoDTO c3Vencido = prestamoEnSede(4L, 1L, EstadoPrestamo.ACTIVO, true, false);
        c3Vencido.setClienteId(3L);

        when(clienteDao.count()).thenReturn(50L);
        when(prestamoService.getPrestamosActivos()).thenReturn(List.of(c1Vigente, c1Vencido, c2Vigente, c3Vencido));
        when(clienteService.getClienteById(1L)).thenReturn(cliente(1L, "Juan"));
        when(clienteService.getClienteById(3L)).thenReturn(cliente(3L, "Ana"));

        ReporteClientesDTO result = reporteService.generarReporteClientes();

        assertEquals(50L, result.getTotalClientes());
        assertEquals(3, result.getClientesConPrestamosActivos());
        assertEquals(2, result.getClientesConPrestamosVencidos());
        assertEquals(List.of("Juan", "Ana"), result.getClientesConRetraso().stream().map(c -> c.getNombre()).toList());
    }

    @Test
    void testReporteClientes_SinPrestamosActivos() {
        when(clienteDao.count()).thenReturn(5L);
        when(prestamoService.getPrestamosActivos()).thenReturn(List.of());

        ReporteClientesDTO result = reporteService.generarReporteClientes();

        assertEquals(0, result.getClientesConPrestamosActivos());
        assertEquals(0, result.getClientesConPrestamosVencidos());
        assertTrue(result.getClientesConRetraso().isEmpty());
        verifyNoInteractions(clienteService);
    }

    // ---------- reporte de movimientos entre sedes ----------

    private List<TrasladoDTO> trasladosDelPeriodo() {
        return List.of(
                trasladoEntre(1L, 1L, 2L, EstadoTraslado.EN_TRANSITO),
                trasladoEntre(2L, 1L, 2L, EstadoTraslado.RECIBIDO),
                trasladoEntre(3L, 2L, 3L, EstadoTraslado.CANCELADO),
                trasladoEntre(4L, 3L, 1L, EstadoTraslado.RECIBIDO));
    }

    @Test
    void testReporteMovimientos_TodasLasSedes() {
        when(trasladoService.getTrasladosPorRango(desde, hasta)).thenReturn(trasladosDelPeriodo());

        ReporteMovimientosDTO result = reporteService.generarReporteMovimientos(desde, hasta, null);

        assertEquals(4, result.getTotalTraslados());
        assertEquals(1, result.getEnTransito());
        assertEquals(2, result.getRecibidos());
        assertEquals(1, result.getCancelados());
    }

    @Test
    void testReporteMovimientos_FiltradoPorSedeOrigenODestino() {
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Sur", true));
        when(trasladoService.getTrasladosPorRango(desde, hasta)).thenReturn(trasladosDelPeriodo());

        ReporteMovimientosDTO result = reporteService.generarReporteMovimientos(desde, hasta, 3L);

        // la sede 3 participa en los traslados 3 (destino) y 4 (origen)
        assertEquals(2, result.getTotalTraslados());
        assertEquals(1, result.getRecibidos());
        assertEquals(1, result.getCancelados());
        assertEquals(0, result.getEnTransito());
    }

    @Test
    void testReporteMovimientos_RangoInvalido() {
        assertThrows(SolicitudInvalidaException.class,
                () -> reporteService.generarReporteMovimientos(hasta, desde, null));

        verifyNoInteractions(trasladoService);
    }

    // ---------- libros mas solicitados ----------

    @Test
    void testLibrosMasSolicitados_SumaPrestamosYReservasYOrdena() {
        when(prestamoDao.contarPrestamosPorLibro()).thenReturn(List.of(
                new ConteoLibroDTO(1L, 5L), new ConteoLibroDTO(2L, 2L), new ConteoLibroDTO(3L, 1L)));
        when(reservaDao.contarReservasPorLibro()).thenReturn(List.of(
                new ConteoLibroDTO(2L, 4L), new ConteoLibroDTO(4L, 1L)));
        // totales: libro 2 = 6, libro 1 = 5, libro 3 = 1, libro 4 = 1 (empate: gana el id menor)
        when(libroService.getLibroById(2L)).thenReturn(libro(2L, "Rayuela", "978-2"));
        when(libroService.getLibroById(1L)).thenReturn(libro(1L, "Cien anios de soledad", "978-1"));
        when(libroService.getLibroById(3L)).thenReturn(libro(3L, "Pedro Paramo", "978-3"));

        List<LibroSolicitadoDTO> result = reporteService.getLibrosMasSolicitados(3);

        assertEquals(List.of(2L, 1L, 3L), result.stream().map(LibroSolicitadoDTO::getLibroId).toList());
        LibroSolicitadoDTO primero = result.get(0);
        assertEquals("Rayuela", primero.getTitulo());
        assertEquals(2L, primero.getTotalPrestamos());
        assertEquals(4L, primero.getTotalReservas());
        assertEquals(6L, primero.getTotalSolicitudes());
        assertEquals(0L, result.get(1).getTotalReservas());
    }

    @Test
    void testLibrosMasSolicitados_SoloReservas() {
        when(prestamoDao.contarPrestamosPorLibro()).thenReturn(List.of());
        when(reservaDao.contarReservasPorLibro()).thenReturn(List.of(new ConteoLibroDTO(4L, 3L)));
        when(libroService.getLibroById(4L)).thenReturn(libro(4L, "Ficciones", "978-4"));

        List<LibroSolicitadoDTO> result = reporteService.getLibrosMasSolicitados(10);

        assertEquals(1, result.size());
        assertEquals(0L, result.get(0).getTotalPrestamos());
        assertEquals(3L, result.get(0).getTotalSolicitudes());
    }

    @Test
    void testLibrosMasSolicitados_SinDatos() {
        when(prestamoDao.contarPrestamosPorLibro()).thenReturn(List.of());
        when(reservaDao.contarReservasPorLibro()).thenReturn(List.of());

        assertTrue(reporteService.getLibrosMasSolicitados(10).isEmpty());
        verifyNoInteractions(libroService);
    }

    @Test
    void testLibrosMasSolicitados_LimiteInvalido() {
        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> reporteService.getLibrosMasSolicitados(0));

        assertEquals("El limite debe ser mayor a cero", ex.getMessage());
        verifyNoInteractions(prestamoDao, reservaDao, libroService);
    }
}
