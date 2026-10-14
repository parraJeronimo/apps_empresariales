package com.taller.bibliotecas.businessLayer;

import static com.taller.bibliotecas.businessLayer.TestData.cliente;
import static com.taller.bibliotecas.businessLayer.TestData.ejemplar;
import static com.taller.bibliotecas.businessLayer.TestData.prestamo;
import static com.taller.bibliotecas.businessLayer.TestData.sede;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.DevolucionDTO;
import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.businessLayer.dto.FiltroPrestamo;
import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoDTO;
import com.taller.bibliotecas.businessLayer.service.ClienteService;
import com.taller.bibliotecas.businessLayer.service.EjemplarService;
import com.taller.bibliotecas.businessLayer.service.MovimientoEjemplarService;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.businessLayer.service.impl.PrestamoServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.PrestamoDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoPrestamo;
import com.taller.bibliotecas.persistenceLayer.entity.TipoMovimiento;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("PrestamoService - Unit Tests (RF-11 a RF-17, RF-25)")
class PrestamoServiceTest {

    @Mock
    private PrestamoDAO prestamoDao;

    @Mock
    private ClienteService clienteService;

    @Mock
    private SedeService sedeService;

    @Mock
    private EjemplarService ejemplarService;

    @Mock
    private MovimientoEjemplarService movimientoService;

    @InjectMocks
    private PrestamoServiceImpl prestamoService;

    private final LocalDate hoy = LocalDate.now();

    /** Prestamo del ejemplar 1, cliente 2, sede 3 */
    private PrestamoCreateDTO crearDto(Integer dias) {
        PrestamoCreateDTO dto = new PrestamoCreateDTO();
        dto.setEjemplarId(1L);
        dto.setClienteId(2L);
        dto.setSedeId(3L);
        dto.setDiasPrestamo(dias);
        return dto;
    }

    /** Deja listas las validaciones previas de un prestamo valido (cliente 2, sede 3 activa, ejemplar 1 disponible en la sede 3). */
    private void stubPrestamoValido() {
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(ejemplarService.getEjemplarById(1L))
                .thenReturn(ejemplar(1L, "EJ-001", EstadoEjemplar.DISPONIBLE, 1L, 3L));
    }

    // ---------- RF-11 / RF-12 registrar prestamo ----------

    @Test
    void testCreatePrestamo_PlazoPorDefecto() {
        PrestamoCreateDTO dto = crearDto(null);
        stubPrestamoValido();
        when(prestamoDao.save(eq(dto), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(prestamo(10L, EstadoPrestamo.ACTIVO, hoy, hoy.plusDays(14)));

        PrestamoDTO result = prestamoService.createPrestamo(dto);

        assertEquals(10L, result.getId());
        assertFalse(result.getVencido());

        ArgumentCaptor<LocalDate> inicio = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> limite = ArgumentCaptor.forClass(LocalDate.class);
        verify(prestamoDao).save(eq(dto), inicio.capture(), limite.capture());
        assertEquals(inicio.getValue().plusDays(14), limite.getValue());
    }

    @Test
    void testCreatePrestamo_PlazoPersonalizado() {
        PrestamoCreateDTO dto = crearDto(7);
        stubPrestamoValido();
        when(prestamoDao.save(eq(dto), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(prestamo(10L, EstadoPrestamo.ACTIVO, hoy, hoy.plusDays(7)));

        prestamoService.createPrestamo(dto);

        ArgumentCaptor<LocalDate> inicio = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> limite = ArgumentCaptor.forClass(LocalDate.class);
        verify(prestamoDao).save(eq(dto), inicio.capture(), limite.capture());
        assertEquals(inicio.getValue().plusDays(7), limite.getValue());
    }

    @Test
    void testCreatePrestamo_MarcaElEjemplarComoPrestadoYRegistraMovimiento() {
        PrestamoCreateDTO dto = crearDto(null);
        stubPrestamoValido();
        when(prestamoDao.save(eq(dto), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(prestamo(10L, EstadoPrestamo.ACTIVO, hoy, hoy.plusDays(14)));

        prestamoService.createPrestamo(dto);

        verify(ejemplarService, times(1)).actualizarEstadoYUbicacion(1L, EstadoEjemplar.PRESTADO, null);
        ArgumentCaptor<MovimientoEjemplarCreateDTO> captor = ArgumentCaptor.forClass(MovimientoEjemplarCreateDTO.class);
        verify(movimientoService, times(1)).registrarMovimiento(captor.capture());
        assertEquals(TipoMovimiento.PRESTAMO, captor.getValue().getTipo());
        assertEquals(1L, captor.getValue().getEjemplarId());
        assertEquals(3L, captor.getValue().getSedeOrigenId());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 31, 100})
    void testCreatePrestamo_DiasFueraDeRango(int dias) {
        PrestamoCreateDTO dto = crearDto(dias);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> prestamoService.createPrestamo(dto));

        assertEquals("Los dias de prestamo deben estar entre 1 y 30", ex.getMessage());
        verifyNoInteractions(clienteService, sedeService, ejemplarService, prestamoDao, movimientoService);
    }

    @Test
    void testCreatePrestamo_ClienteNoExiste() {
        PrestamoCreateDTO dto = crearDto(null);
        when(clienteService.getClienteById(2L))
                .thenThrow(new RecursoNoEncontradoException("Cliente no encontrado con ID: 2"));

        assertThrows(RecursoNoEncontradoException.class, () -> prestamoService.createPrestamo(dto));

        verifyNoInteractions(prestamoDao);
    }

    @Test
    void testCreatePrestamo_SedeDesactivada() {
        PrestamoCreateDTO dto = crearDto(null);
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", false));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> prestamoService.createPrestamo(dto));

        assertEquals("No se pueden registrar prestamos en una sede desactivada", ex.getMessage());
        verifyNoInteractions(prestamoDao);
    }

    @Test
    void testCreatePrestamo_EjemplarNoExiste() {
        PrestamoCreateDTO dto = crearDto(null);
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(ejemplarService.getEjemplarById(1L))
                .thenThrow(new RecursoNoEncontradoException("Ejemplar no encontrado con ID: 1"));

        assertThrows(RecursoNoEncontradoException.class, () -> prestamoService.createPrestamo(dto));

        verifyNoInteractions(prestamoDao);
    }

    @Test
    void testCreatePrestamo_EjemplarNoDisponible() {
        PrestamoCreateDTO dto = crearDto(null);
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(ejemplarService.getEjemplarById(1L))
                .thenReturn(ejemplar(1L, "EJ-001", EstadoEjemplar.PRESTADO, 1L, 3L));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> prestamoService.createPrestamo(dto));

        assertTrue(ex.getMessage().contains("PRESTADO"));
        verifyNoInteractions(prestamoDao, movimientoService);
        verify(ejemplarService, never()).actualizarEstadoYUbicacion(any(), any(), any());
    }

    @Test
    void testCreatePrestamo_EjemplarEnTrasladoNoSePuedePrestar() {
        PrestamoCreateDTO dto = crearDto(null);
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(ejemplarService.getEjemplarById(1L))
                .thenReturn(ejemplar(1L, "EJ-001", EstadoEjemplar.EN_TRASLADO, 1L, 3L));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> prestamoService.createPrestamo(dto));

        assertTrue(ex.getMessage().contains("EN_TRASLADO"));
        verifyNoInteractions(prestamoDao);
    }

    @Test
    void testCreatePrestamo_EjemplarEnOtraSede() {
        PrestamoCreateDTO dto = crearDto(null);
        EjemplarDTO enOtraSede = ejemplar(1L, "EJ-001", EstadoEjemplar.DISPONIBLE, 1L, 9L);
        enOtraSede.setSedeActualNombre("Norte");
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(ejemplarService.getEjemplarById(1L)).thenReturn(enOtraSede);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> prestamoService.createPrestamo(dto));

        assertTrue(ex.getMessage().contains("Norte"));
        assertTrue(ex.getMessage().contains("Central"));
        verifyNoInteractions(prestamoDao);
    }

    // ---------- consultas ----------

    @Test
    void testGetPrestamoById_Vigente() {
        when(prestamoDao.findById(1L))
                .thenReturn(Optional.of(prestamo(1L, EstadoPrestamo.ACTIVO, hoy.minusDays(2), hoy.plusDays(12))));

        PrestamoDTO result = prestamoService.getPrestamoById(1L);

        assertFalse(result.getVencido());
        assertEquals(0, result.getDiasRetraso());
    }

    @Test
    void testGetPrestamoById_VencidoCalculaDiasDeRetraso() {
        when(prestamoDao.findById(1L))
                .thenReturn(Optional.of(prestamo(1L, EstadoPrestamo.ACTIVO, hoy.minusDays(20), hoy.minusDays(5))));

        PrestamoDTO result = prestamoService.getPrestamoById(1L);

        assertTrue(result.getVencido());
        assertEquals(5, result.getDiasRetraso());
    }

    @Test
    void testGetPrestamoById_FinalizadoNuncaEstaVencido() {
        PrestamoDTO finalizado = prestamo(1L, EstadoPrestamo.FINALIZADO, hoy.minusDays(30), hoy.minusDays(16));
        finalizado.setDiasRetraso(3);
        when(prestamoDao.findById(1L)).thenReturn(Optional.of(finalizado));

        PrestamoDTO result = prestamoService.getPrestamoById(1L);

        assertFalse(result.getVencido());
        assertEquals(3, result.getDiasRetraso());
    }

    @Test
    void testGetPrestamoById_NotFound() {
        when(prestamoDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> prestamoService.getPrestamoById(99L));

        assertEquals("Prestamo no encontrado con ID: 99", ex.getMessage());
    }

    @Test
    void testGetAllPrestamos_ReturnsList() {
        when(prestamoDao.findAll()).thenReturn(List.of(
                prestamo(1L, EstadoPrestamo.ACTIVO, hoy, hoy.plusDays(14)),
                prestamo(2L, EstadoPrestamo.ACTIVO, hoy.minusDays(30), hoy.minusDays(16))));

        List<PrestamoDTO> result = prestamoService.getAllPrestamos();

        assertEquals(2, result.size());
        assertFalse(result.get(0).getVencido());
        assertTrue(result.get(1).getVencido());
    }

    // ---------- RF-13 prestamos del cliente ----------

    /** id 1: vigente | id 2: vencido | id 3: finalizado */
    private List<PrestamoDTO> prestamosDelCliente() {
        return List.of(
                prestamo(1L, EstadoPrestamo.ACTIVO, hoy.minusDays(2), hoy.plusDays(12)),
                prestamo(2L, EstadoPrestamo.ACTIVO, hoy.minusDays(30), hoy.minusDays(16)),
                prestamo(3L, EstadoPrestamo.FINALIZADO, hoy.minusDays(40), hoy.minusDays(26)));
    }

    @Test
    void testGetPrestamosByCliente_Activos() {
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(prestamoDao.findByClienteId(2L)).thenReturn(prestamosDelCliente());

        List<PrestamoDTO> result = prestamoService.getPrestamosByCliente(2L, FiltroPrestamo.ACTIVOS);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void testGetPrestamosByCliente_Vencidos() {
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(prestamoDao.findByClienteId(2L)).thenReturn(prestamosDelCliente());

        List<PrestamoDTO> result = prestamoService.getPrestamosByCliente(2L, FiltroPrestamo.VENCIDOS);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
        assertTrue(result.get(0).getVencido());
    }

    @Test
    void testGetPrestamosByCliente_Finalizados() {
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(prestamoDao.findByClienteId(2L)).thenReturn(prestamosDelCliente());

        List<PrestamoDTO> result = prestamoService.getPrestamosByCliente(2L, FiltroPrestamo.FINALIZADOS);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
    }

    @Test
    void testGetPrestamosByCliente_SinFiltroDevuelveTodos() {
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(prestamoDao.findByClienteId(2L)).thenReturn(prestamosDelCliente());

        assertEquals(3, prestamoService.getPrestamosByCliente(2L, null).size());
    }

    @Test
    void testGetPrestamosByCliente_ClienteNoExiste() {
        when(clienteService.getClienteById(99L))
                .thenThrow(new RecursoNoEncontradoException("Cliente no encontrado con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class,
                () -> prestamoService.getPrestamosByCliente(99L, FiltroPrestamo.ACTIVOS));

        verifyNoInteractions(prestamoDao);
    }

    // ---------- RF-25 historial ----------

    @Test
    void testGetHistorialByCliente_OrdenadoDelMasRecienteAlMasAntiguo() {
        when(clienteService.getClienteById(2L)).thenReturn(cliente(2L, "Juan"));
        when(prestamoDao.findByClienteId(2L)).thenReturn(List.of(
                prestamo(1L, EstadoPrestamo.FINALIZADO, hoy.minusDays(10), hoy.plusDays(4)),
                prestamo(2L, EstadoPrestamo.FINALIZADO, hoy.minusDays(1), hoy.plusDays(13)),
                prestamo(3L, EstadoPrestamo.FINALIZADO, hoy.minusDays(5), hoy.plusDays(9))));

        List<PrestamoDTO> result = prestamoService.getHistorialByCliente(2L);

        assertEquals(List.of(2L, 3L, 1L), result.stream().map(PrestamoDTO::getId).toList());
    }

    @Test
    void testGetHistorialByCliente_ClienteNoExiste() {
        when(clienteService.getClienteById(99L))
                .thenThrow(new RecursoNoEncontradoException("Cliente no encontrado con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> prestamoService.getHistorialByCliente(99L));

        verifyNoInteractions(prestamoDao);
    }

    // ---------- activos / vencidos / rangos (reportes) ----------

    @Test
    void testGetPrestamosActivos_IncluyeVigentesYVencidos() {
        when(prestamoDao.findByEstado(EstadoPrestamo.ACTIVO)).thenReturn(List.of(
                prestamo(1L, EstadoPrestamo.ACTIVO, hoy, hoy.plusDays(14)),
                prestamo(2L, EstadoPrestamo.ACTIVO, hoy.minusDays(30), hoy.minusDays(16))));

        assertEquals(2, prestamoService.getPrestamosActivos().size());
    }

    @Test
    void testGetPrestamosVencidos_SoloLosQueSuperaronLaFechaLimite() {
        when(prestamoDao.findByEstado(EstadoPrestamo.ACTIVO)).thenReturn(List.of(
                prestamo(1L, EstadoPrestamo.ACTIVO, hoy, hoy.plusDays(14)),
                prestamo(2L, EstadoPrestamo.ACTIVO, hoy.minusDays(30), hoy.minusDays(16))));

        List<PrestamoDTO> result = prestamoService.getPrestamosVencidos();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void testGetPrestamosPorRangoDeInicio() {
        LocalDate desde = hoy.minusDays(7);
        when(prestamoDao.findByFechaInicioBetween(desde, hoy))
                .thenReturn(List.of(prestamo(1L, EstadoPrestamo.ACTIVO, hoy, hoy.plusDays(14))));

        assertEquals(1, prestamoService.getPrestamosPorRangoDeInicio(desde, hoy).size());
        verify(prestamoDao, times(1)).findByFechaInicioBetween(desde, hoy);
    }

    @Test
    void testGetDevolucionesPorRango() {
        LocalDate desde = hoy.minusDays(7);
        when(prestamoDao.findByFechaDevolucionBetween(desde, hoy))
                .thenReturn(List.of(prestamo(1L, EstadoPrestamo.FINALIZADO, hoy.minusDays(10), hoy.minusDays(1))));

        assertEquals(1, prestamoService.getDevolucionesPorRango(desde, hoy).size());
        verify(prestamoDao, times(1)).findByFechaDevolucionBetween(desde, hoy);
    }

    // ---------- RF-14 a RF-17 devolucion ----------

    private DevolucionDTO devolucionEn(Long sedeId) {
        DevolucionDTO dto = new DevolucionDTO();
        dto.setSedeDevolucionId(sedeId);
        return dto;
    }

    @Test
    void testRegistrarDevolucion_ATiempoEnLaMismaSede() {
        DevolucionDTO dto = devolucionEn(3L);
        when(prestamoDao.findById(5L))
                .thenReturn(Optional.of(prestamo(5L, EstadoPrestamo.ACTIVO, hoy.minusDays(2), hoy.plusDays(12))));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        PrestamoDTO finalizado = prestamo(5L, EstadoPrestamo.FINALIZADO, hoy.minusDays(2), hoy.plusDays(12));
        finalizado.setDevueltoConRetraso(false);
        when(prestamoDao.registrarDevolucion(eq(5L), eq(3L), any(LocalDate.class), eq(false), eq(0)))
                .thenReturn(Optional.of(finalizado));

        PrestamoDTO result = prestamoService.registrarDevolucion(5L, dto);

        assertEquals(EstadoPrestamo.FINALIZADO, result.getEstado());
        assertFalse(result.getDevueltoConRetraso());
        verify(ejemplarService, times(1)).actualizarEstadoYUbicacion(1L, EstadoEjemplar.DISPONIBLE, 3L);
    }

    @Test
    void testRegistrarDevolucion_ConRetrasoCalculaLosDias() {
        DevolucionDTO dto = devolucionEn(3L);
        when(prestamoDao.findById(5L))
                .thenReturn(Optional.of(prestamo(5L, EstadoPrestamo.ACTIVO, hoy.minusDays(20), hoy.minusDays(4))));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        PrestamoDTO finalizado = prestamo(5L, EstadoPrestamo.FINALIZADO, hoy.minusDays(20), hoy.minusDays(4));
        finalizado.setDevueltoConRetraso(true);
        finalizado.setDiasRetraso(4);
        when(prestamoDao.registrarDevolucion(eq(5L), eq(3L), any(LocalDate.class), eq(true), eq(4)))
                .thenReturn(Optional.of(finalizado));

        PrestamoDTO result = prestamoService.registrarDevolucion(5L, dto);

        assertTrue(result.getDevueltoConRetraso());
        assertEquals(4, result.getDiasRetraso());
    }

    @Test
    void testRegistrarDevolucion_EnLimiteDeLaFechaNoEsRetraso() {
        DevolucionDTO dto = devolucionEn(3L);
        when(prestamoDao.findById(5L))
                .thenReturn(Optional.of(prestamo(5L, EstadoPrestamo.ACTIVO, hoy.minusDays(14), hoy)));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(prestamoDao.registrarDevolucion(eq(5L), eq(3L), any(LocalDate.class), eq(false), eq(0)))
                .thenReturn(Optional.of(prestamo(5L, EstadoPrestamo.FINALIZADO, hoy.minusDays(14), hoy)));

        assertEquals(EstadoPrestamo.FINALIZADO, prestamoService.registrarDevolucion(5L, dto).getEstado());
    }

    @Test
    void testRegistrarDevolucion_EnSedeDiferenteDejaElEjemplarEnEsaSede() {
        // El prestamo se hizo en la sede 3 y se devuelve en la sede 4 (RF-16 / RF-17)
        DevolucionDTO dto = devolucionEn(4L);
        when(prestamoDao.findById(5L))
                .thenReturn(Optional.of(prestamo(5L, EstadoPrestamo.ACTIVO, hoy.minusDays(2), hoy.plusDays(12))));
        when(sedeService.getSedeById(4L)).thenReturn(sede(4L, "Norte", true));
        PrestamoDTO finalizado = prestamo(5L, EstadoPrestamo.FINALIZADO, hoy.minusDays(2), hoy.plusDays(12));
        finalizado.setSedeDevolucionId(4L);
        when(prestamoDao.registrarDevolucion(eq(5L), eq(4L), any(LocalDate.class), eq(false), eq(0)))
                .thenReturn(Optional.of(finalizado));

        PrestamoDTO result = prestamoService.registrarDevolucion(5L, dto);

        assertEquals(4L, result.getSedeDevolucionId());
        assertNotEquals(result.getSedePrestamoId(), result.getSedeDevolucionId());
        verify(ejemplarService, times(1)).actualizarEstadoYUbicacion(1L, EstadoEjemplar.DISPONIBLE, 4L);

        ArgumentCaptor<MovimientoEjemplarCreateDTO> captor = ArgumentCaptor.forClass(MovimientoEjemplarCreateDTO.class);
        verify(movimientoService, times(1)).registrarMovimiento(captor.capture());
        assertEquals(TipoMovimiento.DEVOLUCION, captor.getValue().getTipo());
        assertEquals(3L, captor.getValue().getSedeOrigenId());
        assertEquals(4L, captor.getValue().getSedeDestinoId());
    }

    @Test
    void testRegistrarDevolucion_PrestamoNoExiste() {
        when(prestamoDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> prestamoService.registrarDevolucion(99L, devolucionEn(3L)));

        assertEquals("Prestamo no encontrado con ID: 99", ex.getMessage());
        verifyNoInteractions(sedeService, ejemplarService, movimientoService);
    }

    @Test
    void testRegistrarDevolucion_PrestamoYaFinalizado() {
        when(prestamoDao.findById(5L))
                .thenReturn(Optional.of(prestamo(5L, EstadoPrestamo.FINALIZADO, hoy.minusDays(10), hoy.plusDays(4))));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> prestamoService.registrarDevolucion(5L, devolucionEn(3L)));

        assertEquals("El prestamo ya fue devuelto anteriormente", ex.getMessage());
        verify(prestamoDao, never()).registrarDevolucion(any(), any(), any(), anyBoolean(), anyInt());
        verifyNoInteractions(ejemplarService, movimientoService);
    }

    @Test
    void testRegistrarDevolucion_SedeNoExiste() {
        when(prestamoDao.findById(5L))
                .thenReturn(Optional.of(prestamo(5L, EstadoPrestamo.ACTIVO, hoy.minusDays(2), hoy.plusDays(12))));
        when(sedeService.getSedeById(77L)).thenThrow(new RecursoNoEncontradoException("Sede no encontrada con ID: 77"));

        assertThrows(RecursoNoEncontradoException.class,
                () -> prestamoService.registrarDevolucion(5L, devolucionEn(77L)));

        verify(prestamoDao, never()).registrarDevolucion(any(), any(), any(), anyBoolean(), anyInt());
    }

    @Test
    void testRegistrarDevolucion_SedeDesactivada() {
        when(prestamoDao.findById(5L))
                .thenReturn(Optional.of(prestamo(5L, EstadoPrestamo.ACTIVO, hoy.minusDays(2), hoy.plusDays(12))));
        when(sedeService.getSedeById(4L)).thenReturn(sede(4L, "Norte", false));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> prestamoService.registrarDevolucion(5L, devolucionEn(4L)));

        assertEquals("No se pueden recibir devoluciones en una sede desactivada", ex.getMessage());
        verifyNoInteractions(ejemplarService, movimientoService);
    }

    @Test
    void testRegistrarDevolucion_PrestamoDesaparecioAlActualizar() {
        when(prestamoDao.findById(5L))
                .thenReturn(Optional.of(prestamo(5L, EstadoPrestamo.ACTIVO, hoy.minusDays(2), hoy.plusDays(12))));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(prestamoDao.registrarDevolucion(eq(5L), eq(3L), any(LocalDate.class), eq(false), eq(0)))
                .thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> prestamoService.registrarDevolucion(5L, devolucionEn(3L)));

        verifyNoInteractions(ejemplarService, movimientoService);
    }
}
