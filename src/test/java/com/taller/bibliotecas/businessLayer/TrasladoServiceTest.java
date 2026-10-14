package com.taller.bibliotecas.businessLayer;

import static com.taller.bibliotecas.businessLayer.TestData.ejemplar;
import static com.taller.bibliotecas.businessLayer.TestData.sede;
import static com.taller.bibliotecas.businessLayer.TestData.traslado;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.TrasladoCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.TrasladoDTO;
import com.taller.bibliotecas.businessLayer.service.EjemplarService;
import com.taller.bibliotecas.businessLayer.service.MovimientoEjemplarService;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.businessLayer.service.impl.TrasladoServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.TrasladoDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoTraslado;
import com.taller.bibliotecas.persistenceLayer.entity.TipoMovimiento;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrasladoService - Unit Tests (RF-18, RF-19)")
class TrasladoServiceTest {

    @Mock
    private TrasladoDAO trasladoDao;

    @Mock
    private EjemplarService ejemplarService;

    @Mock
    private SedeService sedeService;

    @Mock
    private MovimientoEjemplarService movimientoService;

    @InjectMocks
    private TrasladoServiceImpl trasladoService;

    /** ejemplar 1 hacia la sede 2 */
    private TrasladoCreateDTO crearDto() {
        TrasladoCreateDTO dto = new TrasladoCreateDTO();
        dto.setEjemplarId(1L);
        dto.setSedeDestinoId(2L);
        dto.setObservaciones("Solicitado por la sede Norte");
        return dto;
    }

    // ---------- RF-18 registrar traslado ----------

    @Test
    void testCreateTraslado_Success() {
        TrasladoCreateDTO dto = crearDto();
        when(ejemplarService.getEjemplarById(1L))
                .thenReturn(ejemplar(1L, "EJ-001", EstadoEjemplar.DISPONIBLE, 1L, 1L));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Norte", true));
        when(trasladoDao.save(dto, 1L)).thenReturn(traslado(7L, EstadoTraslado.EN_TRANSITO));

        TrasladoDTO result = trasladoService.createTraslado(dto);

        assertEquals(7L, result.getId());
        assertEquals(EstadoTraslado.EN_TRANSITO, result.getEstado());
        verify(ejemplarService, times(1)).actualizarEstadoYUbicacion(1L, EstadoEjemplar.EN_TRASLADO, null);

        ArgumentCaptor<MovimientoEjemplarCreateDTO> captor = ArgumentCaptor.forClass(MovimientoEjemplarCreateDTO.class);
        verify(movimientoService, times(1)).registrarMovimiento(captor.capture());
        assertEquals(TipoMovimiento.TRASLADO_SALIDA, captor.getValue().getTipo());
        assertEquals(1L, captor.getValue().getSedeOrigenId());
        assertEquals(2L, captor.getValue().getSedeDestinoId());
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEjemplar.class, names = {"PRESTADO", "EN_TRASLADO"})
    void testCreateTraslado_SoloEjemplaresDisponibles(EstadoEjemplar estado) {
        TrasladoCreateDTO dto = crearDto();
        when(ejemplarService.getEjemplarById(1L)).thenReturn(ejemplar(1L, "EJ-001", estado, 1L, 1L));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> trasladoService.createTraslado(dto));

        assertTrue(ex.getMessage().contains(estado.name()));
        verifyNoInteractions(trasladoDao, movimientoService, sedeService);
    }

    @Test
    void testCreateTraslado_SedeDestinoDesactivada() {
        TrasladoCreateDTO dto = crearDto();
        when(ejemplarService.getEjemplarById(1L))
                .thenReturn(ejemplar(1L, "EJ-001", EstadoEjemplar.DISPONIBLE, 1L, 1L));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Norte", false));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> trasladoService.createTraslado(dto));

        assertEquals("La sede destino se encuentra desactivada", ex.getMessage());
        verifyNoInteractions(trasladoDao);
    }

    @Test
    void testCreateTraslado_DestinoIgualALaSedeActual() {
        TrasladoCreateDTO dto = crearDto();
        when(ejemplarService.getEjemplarById(1L))
                .thenReturn(ejemplar(1L, "EJ-001", EstadoEjemplar.DISPONIBLE, 1L, 2L));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Norte", true));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> trasladoService.createTraslado(dto));

        assertEquals("La sede destino debe ser diferente a la sede donde se encuentra el ejemplar", ex.getMessage());
        verifyNoInteractions(trasladoDao, movimientoService);
    }

    @Test
    void testCreateTraslado_EjemplarNoExiste() {
        TrasladoCreateDTO dto = crearDto();
        when(ejemplarService.getEjemplarById(1L))
                .thenThrow(new RecursoNoEncontradoException("Ejemplar no encontrado con ID: 1"));

        assertThrows(RecursoNoEncontradoException.class, () -> trasladoService.createTraslado(dto));

        verifyNoInteractions(trasladoDao, sedeService);
    }

    @Test
    void testCreateTraslado_SedeDestinoNoExiste() {
        TrasladoCreateDTO dto = crearDto();
        when(ejemplarService.getEjemplarById(1L))
                .thenReturn(ejemplar(1L, "EJ-001", EstadoEjemplar.DISPONIBLE, 1L, 1L));
        when(sedeService.getSedeById(2L)).thenThrow(new RecursoNoEncontradoException("Sede no encontrada con ID: 2"));

        assertThrows(RecursoNoEncontradoException.class, () -> trasladoService.createTraslado(dto));

        verifyNoInteractions(trasladoDao);
    }

    // ---------- consultas ----------

    @Test
    void testGetTrasladoById_Found() {
        when(trasladoDao.findById(7L)).thenReturn(Optional.of(traslado(7L, EstadoTraslado.EN_TRANSITO)));

        assertEquals(7L, trasladoService.getTrasladoById(7L).getId());
    }

    @Test
    void testGetTrasladoById_NotFound() {
        when(trasladoDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> trasladoService.getTrasladoById(99L));

        assertEquals("Traslado no encontrado con ID: 99", ex.getMessage());
    }

    @Test
    void testGetAllTraslados_ReturnsList() {
        when(trasladoDao.findAll()).thenReturn(List.of(
                traslado(1L, EstadoTraslado.EN_TRANSITO), traslado(2L, EstadoTraslado.RECIBIDO)));

        assertEquals(2, trasladoService.getAllTraslados().size());
    }

    @Test
    void testGetTrasladosBySede_SinFiltroDeEstado() {
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Norte", true));
        when(trasladoDao.findBySedeId(2L)).thenReturn(List.of(
                traslado(1L, EstadoTraslado.EN_TRANSITO), traslado(2L, EstadoTraslado.RECIBIDO)));

        assertEquals(2, trasladoService.getTrasladosBySede(2L, null).size());
    }

    @Test
    void testGetTrasladosBySede_FiltraPorEstado() {
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Norte", true));
        when(trasladoDao.findBySedeId(2L)).thenReturn(List.of(
                traslado(1L, EstadoTraslado.EN_TRANSITO),
                traslado(2L, EstadoTraslado.RECIBIDO),
                traslado(3L, EstadoTraslado.EN_TRANSITO)));

        List<TrasladoDTO> result = trasladoService.getTrasladosBySede(2L, EstadoTraslado.EN_TRANSITO);

        assertEquals(List.of(1L, 3L), result.stream().map(TrasladoDTO::getId).toList());
    }

    @Test
    void testGetTrasladosBySede_SedeNoExiste() {
        when(sedeService.getSedeById(99L)).thenThrow(new RecursoNoEncontradoException("Sede no encontrada con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> trasladoService.getTrasladosBySede(99L, null));

        verifyNoInteractions(trasladoDao);
    }

    @Test
    void testGetTrasladosPorRango_CubreElDiaCompleto() {
        LocalDate desde = LocalDate.of(2026, 10, 1);
        LocalDate hasta = LocalDate.of(2026, 10, 31);
        when(trasladoDao.findByFechaSolicitudBetween(
                LocalDateTime.of(2026, 10, 1, 0, 0), hasta.atTime(LocalTime.MAX)))
                .thenReturn(List.of(traslado(1L, EstadoTraslado.RECIBIDO)));

        assertEquals(1, trasladoService.getTrasladosPorRango(desde, hasta).size());
    }

    // ---------- RF-19 confirmar recepcion ----------

    @Test
    void testConfirmarRecepcion_ActualizaLaUbicacionDelEjemplar() {
        when(trasladoDao.findById(7L)).thenReturn(Optional.of(traslado(7L, EstadoTraslado.EN_TRANSITO)));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Norte", true));
        when(trasladoDao.actualizarEstado(eq(7L), eq(EstadoTraslado.RECIBIDO), any(LocalDateTime.class)))
                .thenReturn(Optional.of(traslado(7L, EstadoTraslado.RECIBIDO)));

        TrasladoDTO result = trasladoService.confirmarRecepcion(7L);

        assertEquals(EstadoTraslado.RECIBIDO, result.getEstado());
        // RF-19: el ejemplar queda disponible en la sede destino
        verify(ejemplarService, times(1)).actualizarEstadoYUbicacion(1L, EstadoEjemplar.DISPONIBLE, 2L);

        ArgumentCaptor<MovimientoEjemplarCreateDTO> captor = ArgumentCaptor.forClass(MovimientoEjemplarCreateDTO.class);
        verify(movimientoService, times(1)).registrarMovimiento(captor.capture());
        assertEquals(TipoMovimiento.TRASLADO_RECEPCION, captor.getValue().getTipo());
        assertEquals(1L, captor.getValue().getSedeOrigenId());
        assertEquals(2L, captor.getValue().getSedeDestinoId());
    }

    @ParameterizedTest
    @EnumSource(value = EstadoTraslado.class, names = {"RECIBIDO", "CANCELADO"})
    void testConfirmarRecepcion_SoloSiEstaEnTransito(EstadoTraslado estado) {
        when(trasladoDao.findById(7L)).thenReturn(Optional.of(traslado(7L, estado)));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> trasladoService.confirmarRecepcion(7L));

        assertTrue(ex.getMessage().contains(estado.name()));
        verify(trasladoDao, never()).actualizarEstado(any(), any(), any());
        verifyNoInteractions(ejemplarService, movimientoService);
    }

    @Test
    void testConfirmarRecepcion_NotFound() {
        when(trasladoDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> trasladoService.confirmarRecepcion(99L));
    }

    @Test
    void testConfirmarRecepcion_SedeDestinoDesactivada() {
        when(trasladoDao.findById(7L)).thenReturn(Optional.of(traslado(7L, EstadoTraslado.EN_TRANSITO)));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Norte", false));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> trasladoService.confirmarRecepcion(7L));

        assertEquals("La sede destino se encuentra desactivada", ex.getMessage());
        verify(trasladoDao, never()).actualizarEstado(any(), any(), any());
        verifyNoInteractions(ejemplarService);
    }

    @Test
    void testConfirmarRecepcion_DesaparecioAlActualizar() {
        when(trasladoDao.findById(7L)).thenReturn(Optional.of(traslado(7L, EstadoTraslado.EN_TRANSITO)));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Norte", true));
        when(trasladoDao.actualizarEstado(eq(7L), eq(EstadoTraslado.RECIBIDO), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> trasladoService.confirmarRecepcion(7L));

        verifyNoInteractions(ejemplarService, movimientoService);
    }

    // ---------- cancelar traslado ----------

    @Test
    void testCancelarTraslado_ElEjemplarVuelveAEstarDisponibleEnSuOrigen() {
        when(trasladoDao.findById(7L)).thenReturn(Optional.of(traslado(7L, EstadoTraslado.EN_TRANSITO)));
        when(trasladoDao.actualizarEstado(7L, EstadoTraslado.CANCELADO, null))
                .thenReturn(Optional.of(traslado(7L, EstadoTraslado.CANCELADO)));

        TrasladoDTO result = trasladoService.cancelarTraslado(7L);

        assertEquals(EstadoTraslado.CANCELADO, result.getEstado());
        verify(ejemplarService, times(1)).actualizarEstadoYUbicacion(1L, EstadoEjemplar.DISPONIBLE, null);

        ArgumentCaptor<MovimientoEjemplarCreateDTO> captor = ArgumentCaptor.forClass(MovimientoEjemplarCreateDTO.class);
        verify(movimientoService, times(1)).registrarMovimiento(captor.capture());
        assertEquals(TipoMovimiento.TRASLADO_CANCELADO, captor.getValue().getTipo());
    }

    @ParameterizedTest
    @EnumSource(value = EstadoTraslado.class, names = {"RECIBIDO", "CANCELADO"})
    void testCancelarTraslado_SoloSiEstaEnTransito(EstadoTraslado estado) {
        when(trasladoDao.findById(7L)).thenReturn(Optional.of(traslado(7L, estado)));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> trasladoService.cancelarTraslado(7L));

        assertTrue(ex.getMessage().contains(estado.name()));
        verify(trasladoDao, never()).actualizarEstado(any(), any(), any());
        verifyNoInteractions(ejemplarService, movimientoService);
    }

    @Test
    void testCancelarTraslado_NotFound() {
        when(trasladoDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> trasladoService.cancelarTraslado(99L));
    }

    @Test
    void testCancelarTraslado_DesaparecioAlActualizar() {
        when(trasladoDao.findById(7L)).thenReturn(Optional.of(traslado(7L, EstadoTraslado.EN_TRANSITO)));
        when(trasladoDao.actualizarEstado(7L, EstadoTraslado.CANCELADO, null)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> trasladoService.cancelarTraslado(7L));

        verifyNoInteractions(ejemplarService, movimientoService);
    }
}
