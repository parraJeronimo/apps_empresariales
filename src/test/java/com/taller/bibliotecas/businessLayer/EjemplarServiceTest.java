package com.taller.bibliotecas.businessLayer;

import static com.taller.bibliotecas.businessLayer.TestData.ejemplar;
import static com.taller.bibliotecas.businessLayer.TestData.libro;
import static com.taller.bibliotecas.businessLayer.TestData.sede;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.EjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.service.LibroService;
import com.taller.bibliotecas.businessLayer.service.MovimientoEjemplarService;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.businessLayer.service.impl.EjemplarServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.EjemplarDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.TipoMovimiento;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("EjemplarService - Unit Tests (RF-04, RF-08)")
class EjemplarServiceTest {

    @Mock
    private EjemplarDAO ejemplarDao;

    @Mock
    private LibroService libroService;

    @Mock
    private SedeService sedeService;

    @Mock
    private MovimientoEjemplarService movimientoService;

    @InjectMocks
    private EjemplarServiceImpl ejemplarService;

    private EjemplarCreateDTO crearDto(String codigo) {
        EjemplarCreateDTO dto = new EjemplarCreateDTO();
        dto.setCodigo(codigo);
        dto.setLibroId(1L);
        dto.setSedeId(2L);
        return dto;
    }

    // ---------- RF-04 registrar ----------

    @Test
    void testCreateEjemplar_ConCodigoPropio() {
        EjemplarCreateDTO dto = crearDto("EJ-001");
        when(libroService.getLibroById(1L)).thenReturn(libro(1L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Central", true));
        when(ejemplarDao.existsByCodigo("EJ-001")).thenReturn(false);
        when(ejemplarDao.save(dto)).thenReturn(ejemplar(10L, "EJ-001", EstadoEjemplar.DISPONIBLE, 1L, 2L));

        EjemplarDTO result = ejemplarService.createEjemplar(dto);

        assertEquals(10L, result.getId());
        assertEquals(EstadoEjemplar.DISPONIBLE, result.getEstado());

        ArgumentCaptor<MovimientoEjemplarCreateDTO> captor = ArgumentCaptor.forClass(MovimientoEjemplarCreateDTO.class);
        verify(movimientoService, times(1)).registrarMovimiento(captor.capture());
        assertEquals(TipoMovimiento.REGISTRO, captor.getValue().getTipo());
        assertEquals(10L, captor.getValue().getEjemplarId());
        assertEquals(2L, captor.getValue().getSedeDestinoId());
        assertNull(captor.getValue().getSedeOrigenId());
    }

    @Test
    void testCreateEjemplar_RecortaEspaciosDelCodigo() {
        EjemplarCreateDTO dto = crearDto("  EJ-002  ");
        when(libroService.getLibroById(1L)).thenReturn(libro(1L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Central", true));
        when(ejemplarDao.existsByCodigo("EJ-002")).thenReturn(false);
        when(ejemplarDao.save(dto)).thenReturn(ejemplar(11L, "EJ-002", EstadoEjemplar.DISPONIBLE, 1L, 2L));

        ejemplarService.createEjemplar(dto);

        assertEquals("EJ-002", dto.getCodigo());
    }

    @Test
    void testCreateEjemplar_CodigoDuplicado() {
        EjemplarCreateDTO dto = crearDto("EJ-001");
        when(libroService.getLibroById(1L)).thenReturn(libro(1L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Central", true));
        when(ejemplarDao.existsByCodigo("EJ-001")).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> ejemplarService.createEjemplar(dto));

        assertEquals("Ya existe un ejemplar con el codigo: EJ-001", ex.getMessage());
        verify(ejemplarDao, never()).save(any());
        verifyNoInteractions(movimientoService);
    }

    @Test
    void testCreateEjemplar_GeneraCodigoUnico() {
        EjemplarCreateDTO dto = crearDto(null);
        when(libroService.getLibroById(1L)).thenReturn(libro(1L, "Rayuela", "978-0307-4747"));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Central", true));
        when(ejemplarDao.countByLibroId(1L)).thenReturn(0L);
        when(ejemplarDao.existsByCodigo("97803074747-001")).thenReturn(false);
        when(ejemplarDao.save(dto)).thenReturn(ejemplar(10L, "97803074747-001", EstadoEjemplar.DISPONIBLE, 1L, 2L));

        ejemplarService.createEjemplar(dto);

        assertEquals("97803074747-001", dto.getCodigo());
    }

    @Test
    void testCreateEjemplar_GeneraCodigoSaltandoLosOcupados() {
        EjemplarCreateDTO dto = crearDto("   ");
        when(libroService.getLibroById(1L)).thenReturn(libro(1L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Central", true));
        when(ejemplarDao.countByLibroId(1L)).thenReturn(1L);
        when(ejemplarDao.existsByCodigo("9782-002")).thenReturn(true);
        when(ejemplarDao.existsByCodigo("9782-003")).thenReturn(false);
        when(ejemplarDao.save(dto)).thenReturn(ejemplar(10L, "9782-003", EstadoEjemplar.DISPONIBLE, 1L, 2L));

        ejemplarService.createEjemplar(dto);

        assertEquals("9782-003", dto.getCodigo());
    }

    @Test
    void testCreateEjemplar_SedeDesactivada() {
        EjemplarCreateDTO dto = crearDto("EJ-001");
        when(libroService.getLibroById(1L)).thenReturn(libro(1L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Central", false));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> ejemplarService.createEjemplar(dto));

        assertEquals("No se pueden registrar ejemplares en una sede desactivada", ex.getMessage());
        verify(ejemplarDao, never()).save(any());
    }

    @Test
    void testCreateEjemplar_LibroNoExiste() {
        EjemplarCreateDTO dto = crearDto("EJ-001");
        when(libroService.getLibroById(1L)).thenThrow(new RecursoNoEncontradoException("Libro no encontrado con ID: 1"));

        assertThrows(RecursoNoEncontradoException.class, () -> ejemplarService.createEjemplar(dto));

        verifyNoInteractions(sedeService, ejemplarDao, movimientoService);
    }

    @Test
    void testCreateEjemplar_SedeNoExiste() {
        EjemplarCreateDTO dto = crearDto("EJ-001");
        when(libroService.getLibroById(1L)).thenReturn(libro(1L, "Rayuela", "978-2"));
        when(sedeService.getSedeById(2L)).thenThrow(new RecursoNoEncontradoException("Sede no encontrada con ID: 2"));

        assertThrows(RecursoNoEncontradoException.class, () -> ejemplarService.createEjemplar(dto));

        verifyNoInteractions(ejemplarDao);
    }

    // ---------- RF-08 consultas ----------

    @Test
    void testGetEjemplarById_Found() {
        when(ejemplarDao.findById(1L))
                .thenReturn(Optional.of(ejemplar(1L, "EJ-001", EstadoEjemplar.PRESTADO, 1L, 2L)));

        EjemplarDTO result = ejemplarService.getEjemplarById(1L);

        assertEquals("EJ-001", result.getCodigo());
        assertEquals(EstadoEjemplar.PRESTADO, result.getEstado());
        assertEquals(2L, result.getSedeActualId());
    }

    @Test
    void testGetEjemplarById_NotFound() {
        when(ejemplarDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> ejemplarService.getEjemplarById(99L));

        assertEquals("Ejemplar no encontrado con ID: 99", ex.getMessage());
    }

    @Test
    void testGetAllEjemplares_ReturnsList() {
        when(ejemplarDao.findAll()).thenReturn(List.of(
                ejemplar(1L, "A", EstadoEjemplar.DISPONIBLE, 1L, 1L),
                ejemplar(2L, "B", EstadoEjemplar.EN_TRASLADO, 1L, 1L)));

        assertEquals(2, ejemplarService.getAllEjemplares().size());
    }

    @Test
    void testGetEjemplarByCodigo_Found() {
        when(ejemplarDao.findByCodigo("EJ-001"))
                .thenReturn(Optional.of(ejemplar(1L, "EJ-001", EstadoEjemplar.DISPONIBLE, 1L, 2L)));

        assertEquals(1L, ejemplarService.getEjemplarByCodigo("EJ-001").getId());
    }

    @Test
    void testGetEjemplarByCodigo_NotFound() {
        when(ejemplarDao.findByCodigo("XX")).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> ejemplarService.getEjemplarByCodigo("XX"));

        assertEquals("Ejemplar no encontrado con codigo: XX", ex.getMessage());
    }

    @Test
    void testGetEjemplaresByLibro_Success() {
        when(libroService.getLibroById(1L)).thenReturn(libro(1L, "Rayuela", "978-2"));
        when(ejemplarDao.findByLibroId(1L)).thenReturn(List.of(
                ejemplar(1L, "A", EstadoEjemplar.DISPONIBLE, 1L, 1L),
                ejemplar(2L, "B", EstadoEjemplar.PRESTADO, 1L, 2L)));

        List<EjemplarDTO> result = ejemplarService.getEjemplaresByLibro(1L);

        assertEquals(2, result.size());
        assertEquals(2L, result.get(1).getSedeActualId());
    }

    @Test
    void testGetEjemplaresByLibro_LibroNoExiste() {
        when(libroService.getLibroById(99L)).thenThrow(new RecursoNoEncontradoException("Libro no encontrado con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> ejemplarService.getEjemplaresByLibro(99L));

        verifyNoInteractions(ejemplarDao);
    }

    @Test
    void testGetEjemplaresBySede_Success() {
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Central", true));
        when(ejemplarDao.findBySedeActualId(2L))
                .thenReturn(List.of(ejemplar(1L, "A", EstadoEjemplar.DISPONIBLE, 1L, 2L)));

        assertEquals(1, ejemplarService.getEjemplaresBySede(2L).size());
    }

    @Test
    void testGetEjemplaresBySede_SedeNoExiste() {
        when(sedeService.getSedeById(99L)).thenThrow(new RecursoNoEncontradoException("Sede no encontrada con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> ejemplarService.getEjemplaresBySede(99L));

        verifyNoInteractions(ejemplarDao);
    }

    @Test
    void testContarEjemplaresDisponibles() {
        when(ejemplarDao.countByLibroIdAndSedeActualIdAndEstado(1L, 2L, EstadoEjemplar.DISPONIBLE)).thenReturn(3L);

        assertEquals(3L, ejemplarService.contarEjemplaresDisponibles(1L, 2L));
    }

    // ---------- uso interno: estado y ubicacion ----------

    @Test
    void testActualizarEstadoYUbicacion_Success() {
        when(ejemplarDao.actualizarEstadoYSede(1L, EstadoEjemplar.DISPONIBLE, 5L))
                .thenReturn(Optional.of(ejemplar(1L, "A", EstadoEjemplar.DISPONIBLE, 1L, 5L)));

        EjemplarDTO result = ejemplarService.actualizarEstadoYUbicacion(1L, EstadoEjemplar.DISPONIBLE, 5L);

        assertEquals(5L, result.getSedeActualId());
    }

    @Test
    void testActualizarEstadoYUbicacion_NotFound() {
        when(ejemplarDao.actualizarEstadoYSede(99L, EstadoEjemplar.PRESTADO, null)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> ejemplarService.actualizarEstadoYUbicacion(99L, EstadoEjemplar.PRESTADO, null));

        assertEquals("Ejemplar no encontrado con ID: 99", ex.getMessage());
    }
}
