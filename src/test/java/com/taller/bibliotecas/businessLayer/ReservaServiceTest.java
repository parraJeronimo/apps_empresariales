package com.taller.bibliotecas.businessLayer;

import static com.taller.bibliotecas.businessLayer.TestData.cliente;
import static com.taller.bibliotecas.businessLayer.TestData.libro;
import static com.taller.bibliotecas.businessLayer.TestData.reserva;
import static com.taller.bibliotecas.businessLayer.TestData.sede;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.ReservaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaRechazoDTO;
import com.taller.bibliotecas.businessLayer.service.ClienteService;
import com.taller.bibliotecas.businessLayer.service.EjemplarService;
import com.taller.bibliotecas.businessLayer.service.LibroService;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.businessLayer.service.impl.ReservaServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.ReservaDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoReserva;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservaService - Unit Tests (RF-21, RF-22)")
class ReservaServiceTest {

    @Mock
    private ReservaDAO reservaDao;

    @Mock
    private ClienteService clienteService;

    @Mock
    private LibroService libroService;

    @Mock
    private SedeService sedeService;

    @Mock
    private EjemplarService ejemplarService;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    /** cliente 1, libro 2, sede 3 */
    private ReservaCreateDTO crearDto() {
        ReservaCreateDTO dto = new ReservaCreateDTO();
        dto.setClienteId(1L);
        dto.setLibroId(2L);
        dto.setSedeId(3L);
        return dto;
    }

    private ReservaRechazoDTO rechazo(String motivo) {
        ReservaRechazoDTO dto = new ReservaRechazoDTO();
        dto.setMotivo(motivo);
        return dto;
    }

    // ---------- RF-21 crear reserva ----------

    @Test
    void testCreateReserva_Success() {
        ReservaCreateDTO dto = crearDto();
        when(clienteService.getClienteById(1L)).thenReturn(cliente(1L, "Juan"));
        when(libroService.getLibroById(2L)).thenReturn(libro(2L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(ejemplarService.contarEjemplaresDisponibles(2L, 3L)).thenReturn(0L);
        when(reservaDao.existsByClienteIdAndLibroIdAndSedeIdAndEstado(1L, 2L, 3L, EstadoReserva.PENDIENTE))
                .thenReturn(false);
        when(reservaDao.save(dto)).thenReturn(reserva(10L, EstadoReserva.PENDIENTE));

        ReservaDTO result = reservaService.createReserva(dto);

        assertEquals(10L, result.getId());
        assertEquals(EstadoReserva.PENDIENTE, result.getEstado());
        verify(reservaDao, times(1)).save(dto);
    }

    @Test
    void testCreateReserva_HayEjemplaresDisponiblesEnLaSede() {
        ReservaCreateDTO dto = crearDto();
        when(clienteService.getClienteById(1L)).thenReturn(cliente(1L, "Juan"));
        when(libroService.getLibroById(2L)).thenReturn(libro(2L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(ejemplarService.contarEjemplaresDisponibles(2L, 3L)).thenReturn(2L);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> reservaService.createReserva(dto));

        assertTrue(ex.getMessage().contains("Central"));
        verify(reservaDao, never()).save(any());
    }

    @Test
    void testCreateReserva_SolicitudPendienteDuplicada() {
        ReservaCreateDTO dto = crearDto();
        when(clienteService.getClienteById(1L)).thenReturn(cliente(1L, "Juan"));
        when(libroService.getLibroById(2L)).thenReturn(libro(2L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(ejemplarService.contarEjemplaresDisponibles(2L, 3L)).thenReturn(0L);
        when(reservaDao.existsByClienteIdAndLibroIdAndSedeIdAndEstado(1L, 2L, 3L, EstadoReserva.PENDIENTE))
                .thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> reservaService.createReserva(dto));

        assertEquals("El cliente ya tiene una solicitud pendiente para este libro en la sede indicada", ex.getMessage());
        verify(reservaDao, never()).save(any());
    }

    @Test
    void testCreateReserva_SedeDesactivada() {
        ReservaCreateDTO dto = crearDto();
        when(clienteService.getClienteById(1L)).thenReturn(cliente(1L, "Juan"));
        when(libroService.getLibroById(2L)).thenReturn(libro(2L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", false));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> reservaService.createReserva(dto));

        assertEquals("No se pueden registrar reservas en una sede desactivada", ex.getMessage());
        verifyNoInteractions(ejemplarService, reservaDao);
    }

    @Test
    void testCreateReserva_ClienteNoExiste() {
        ReservaCreateDTO dto = crearDto();
        when(clienteService.getClienteById(1L))
                .thenThrow(new RecursoNoEncontradoException("Cliente no encontrado con ID: 1"));

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.createReserva(dto));

        verifyNoInteractions(reservaDao);
    }

    @Test
    void testCreateReserva_LibroNoExiste() {
        ReservaCreateDTO dto = crearDto();
        when(clienteService.getClienteById(1L)).thenReturn(cliente(1L, "Juan"));
        when(libroService.getLibroById(2L)).thenThrow(new RecursoNoEncontradoException("Libro no encontrado con ID: 2"));

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.createReserva(dto));

        verifyNoInteractions(reservaDao);
    }

    @Test
    void testCreateReserva_SedeNoExiste() {
        ReservaCreateDTO dto = crearDto();
        when(clienteService.getClienteById(1L)).thenReturn(cliente(1L, "Juan"));
        when(libroService.getLibroById(2L)).thenReturn(libro(2L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(3L)).thenThrow(new RecursoNoEncontradoException("Sede no encontrada con ID: 3"));

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.createReserva(dto));

        verifyNoInteractions(reservaDao);
    }

    // ---------- RF-22 consultar ----------

    @Test
    void testGetReservaById_Found() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva(1L, EstadoReserva.PENDIENTE)));

        assertEquals(1L, reservaService.getReservaById(1L).getId());
    }

    @Test
    void testGetReservaById_NotFound() {
        when(reservaDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> reservaService.getReservaById(99L));

        assertEquals("Reserva no encontrada con ID: 99", ex.getMessage());
    }

    @Test
    void testGetAllReservas_ReturnsList() {
        when(reservaDao.findAll()).thenReturn(List.of(
                reserva(1L, EstadoReserva.PENDIENTE), reserva(2L, EstadoReserva.RECHAZADA)));

        assertEquals(2, reservaService.getAllReservas().size());
    }

    @Test
    void testGetReservasByCliente_Success() {
        when(clienteService.getClienteById(1L)).thenReturn(cliente(1L, "Juan"));
        when(reservaDao.findByClienteId(1L)).thenReturn(List.of(reserva(1L, EstadoReserva.PENDIENTE)));

        assertEquals(1, reservaService.getReservasByCliente(1L).size());
    }

    @Test
    void testGetReservasByCliente_ClienteNoExiste() {
        when(clienteService.getClienteById(99L))
                .thenThrow(new RecursoNoEncontradoException("Cliente no encontrado con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.getReservasByCliente(99L));

        verifyNoInteractions(reservaDao);
    }

    @Test
    void testGetReservasBySede_SinFiltroDeEstado() {
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(reservaDao.findBySedeId(3L)).thenReturn(List.of(
                reserva(1L, EstadoReserva.PENDIENTE), reserva(2L, EstadoReserva.APROBADA)));

        assertEquals(2, reservaService.getReservasBySede(3L, null).size());
        verify(reservaDao, never()).findBySedeIdAndEstado(any(), any());
    }

    @Test
    void testGetReservasBySede_SoloPendientes() {
        when(sedeService.getSedeById(3L)).thenReturn(sede(3L, "Central", true));
        when(reservaDao.findBySedeIdAndEstado(3L, EstadoReserva.PENDIENTE))
                .thenReturn(List.of(reserva(1L, EstadoReserva.PENDIENTE)));

        List<ReservaDTO> result = reservaService.getReservasBySede(3L, EstadoReserva.PENDIENTE);

        assertEquals(1, result.size());
        verify(reservaDao, never()).findBySedeId(any());
    }

    @Test
    void testGetReservasBySede_SedeNoExiste() {
        when(sedeService.getSedeById(99L)).thenThrow(new RecursoNoEncontradoException("Sede no encontrada con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.getReservasBySede(99L, null));

        verifyNoInteractions(reservaDao);
    }

    // ---------- RF-22 aprobar ----------

    @Test
    void testAprobarReserva_Success() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva(1L, EstadoReserva.PENDIENTE)));
        when(reservaDao.actualizarEstado(eq(1L), eq(EstadoReserva.APROBADA), isNull(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(reserva(1L, EstadoReserva.APROBADA)));

        ReservaDTO result = reservaService.aprobarReserva(1L);

        assertEquals(EstadoReserva.APROBADA, result.getEstado());
    }

    @ParameterizedTest
    @EnumSource(value = EstadoReserva.class, names = {"APROBADA", "RECHAZADA", "CANCELADA"})
    void testAprobarReserva_SoloSePuedeAprobarUnaPendiente(EstadoReserva estadoActual) {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva(1L, estadoActual)));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> reservaService.aprobarReserva(1L));

        assertTrue(ex.getMessage().contains(estadoActual.name()));
        verify(reservaDao, never()).actualizarEstado(any(), any(), any(), any());
    }

    @Test
    void testAprobarReserva_NotFound() {
        when(reservaDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.aprobarReserva(99L));
    }

    @Test
    void testAprobarReserva_DesaparecioAlActualizar() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva(1L, EstadoReserva.PENDIENTE)));
        when(reservaDao.actualizarEstado(eq(1L), eq(EstadoReserva.APROBADA), isNull(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.aprobarReserva(1L));
    }

    // ---------- RF-22 rechazar con motivo ----------

    @Test
    void testRechazarReserva_GuardaElMotivoSinEspacios() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva(1L, EstadoReserva.PENDIENTE)));
        ReservaDTO rechazada = reserva(1L, EstadoReserva.RECHAZADA);
        rechazada.setMotivoRechazo("Sin ejemplares disponibles");
        when(reservaDao.actualizarEstado(eq(1L), eq(EstadoReserva.RECHAZADA), eq("Sin ejemplares disponibles"),
                any(LocalDateTime.class))).thenReturn(Optional.of(rechazada));

        ReservaDTO result = reservaService.rechazarReserva(1L, rechazo("  Sin ejemplares disponibles  "));

        assertEquals(EstadoReserva.RECHAZADA, result.getEstado());
        assertEquals("Sin ejemplares disponibles", result.getMotivoRechazo());
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.NullAndEmptySource
    @org.junit.jupiter.params.provider.ValueSource(strings = {"   "})
    void testRechazarReserva_MotivoObligatorio(String motivo) {
        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> reservaService.rechazarReserva(1L, rechazo(motivo)));

        assertEquals("El motivo del rechazo es obligatorio", ex.getMessage());
        verifyNoInteractions(reservaDao);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoReserva.class, names = {"APROBADA", "RECHAZADA", "CANCELADA"})
    void testRechazarReserva_SoloSePuedeRechazarUnaPendiente(EstadoReserva estadoActual) {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva(1L, estadoActual)));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> reservaService.rechazarReserva(1L, rechazo("Sin stock")));

        assertTrue(ex.getMessage().contains(estadoActual.name()));
        verify(reservaDao, never()).actualizarEstado(any(), any(), any(), any());
    }

    @Test
    void testRechazarReserva_NotFound() {
        when(reservaDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> reservaService.rechazarReserva(99L, rechazo("Sin stock")));
    }

    @Test
    void testRechazarReserva_DesaparecioAlActualizar() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva(1L, EstadoReserva.PENDIENTE)));
        when(reservaDao.actualizarEstado(eq(1L), eq(EstadoReserva.RECHAZADA), eq("Sin stock"), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> reservaService.rechazarReserva(1L, rechazo("Sin stock")));
    }

    // ---------- RF-22 cancelar ----------

    @ParameterizedTest
    @EnumSource(value = EstadoReserva.class, names = {"PENDIENTE", "APROBADA"})
    void testCancelarReserva_PendienteOAprobada(EstadoReserva estadoActual) {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva(1L, estadoActual)));
        when(reservaDao.actualizarEstado(1L, EstadoReserva.CANCELADA, null, null))
                .thenReturn(Optional.of(reserva(1L, EstadoReserva.CANCELADA)));

        ReservaDTO result = reservaService.cancelarReserva(1L);

        assertEquals(EstadoReserva.CANCELADA, result.getEstado());
    }

    @ParameterizedTest
    @EnumSource(value = EstadoReserva.class, names = {"RECHAZADA", "CANCELADA"})
    void testCancelarReserva_NoSePuedeSiYaFueCerrada(EstadoReserva estadoActual) {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva(1L, estadoActual)));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> reservaService.cancelarReserva(1L));

        assertTrue(ex.getMessage().contains(estadoActual.name()));
        verify(reservaDao, never()).actualizarEstado(any(), any(), any(), any());
    }

    @Test
    void testCancelarReserva_NotFound() {
        when(reservaDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.cancelarReserva(99L));
    }

    @Test
    void testCancelarReserva_DesaparecioAlActualizar() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva(1L, EstadoReserva.PENDIENTE)));
        when(reservaDao.actualizarEstado(1L, EstadoReserva.CANCELADA, null, null)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.cancelarReserva(1L));
    }
}
