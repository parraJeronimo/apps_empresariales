package com.taller.bibliotecas.businessLayer;

import static com.taller.bibliotecas.businessLayer.TestData.sede;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.SedeCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.impl.SedeServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.EjemplarDAO;
import com.taller.bibliotecas.persistenceLayer.dao.SedeDAO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("SedeService - Unit Tests (RF-01, RF-02)")
class SedeServiceTest {

    @Mock
    private SedeDAO sedeDao;

    @Mock
    private EjemplarDAO ejemplarDao;

    @InjectMocks
    private SedeServiceImpl sedeService;

    private SedeCreateDTO crearDto(String nombre) {
        SedeCreateDTO dto = new SedeCreateDTO();
        dto.setNombre(nombre);
        dto.setDireccion("Carrera 14 # 20-30");
        return dto;
    }

    // ---------- RF-01 registrar ----------

    @Test
    void testCreateSede_Success() {
        SedeCreateDTO dto = crearDto("Sede Central");
        when(sedeDao.existsByNombre("Sede Central")).thenReturn(false);
        when(sedeDao.save(dto)).thenReturn(sede(1L, "Sede Central", true));

        SedeDTO result = sedeService.createSede(dto);

        assertEquals(1L, result.getId());
        assertEquals("Sede Central", result.getNombre());
        assertTrue(result.getActiva());
        verify(sedeDao, times(1)).save(dto);
    }

    @Test
    void testCreateSede_NombreDuplicado() {
        SedeCreateDTO dto = crearDto("Sede Central");
        when(sedeDao.existsByNombre("Sede Central")).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> sedeService.createSede(dto));

        assertEquals("Ya existe una sede registrada con el nombre: Sede Central", ex.getMessage());
        verify(sedeDao, never()).save(any());
    }

    // ---------- RF-02 consultar ----------

    @Test
    void testGetSedeById_Found() {
        when(sedeDao.findById(1L)).thenReturn(Optional.of(sede(1L, "Sede Central", true)));

        SedeDTO result = sedeService.getSedeById(1L);

        assertEquals("Sede Central", result.getNombre());
        verify(sedeDao, times(1)).findById(1L);
    }

    @Test
    void testGetSedeById_NotFound() {
        when(sedeDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> sedeService.getSedeById(99L));

        assertEquals("Sede no encontrada con ID: 99", ex.getMessage());
    }

    @Test
    void testGetAllSedes_ReturnsList() {
        when(sedeDao.findAll()).thenReturn(List.of(sede(1L, "Central", true), sede(2L, "Norte", false)));

        List<SedeDTO> result = sedeService.getAllSedes();

        assertEquals(2, result.size());
        verify(sedeDao, times(1)).findAll();
    }

    @Test
    void testGetSedesActivas_ReturnsOnlyActive() {
        when(sedeDao.findByActiva(true)).thenReturn(List.of(sede(1L, "Central", true)));

        List<SedeDTO> result = sedeService.getSedesActivas();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getActiva());
        verify(sedeDao, times(1)).findByActiva(true);
    }

    // ---------- RF-02 modificar ----------

    @Test
    void testUpdateSede_Success_SinCambiarNombre() {
        SedeUpdateDTO dto = new SedeUpdateDTO();
        dto.setCiudad("Pereira");
        SedeDTO actualizada = sede(1L, "Sede Central", true);
        actualizada.setCiudad("Pereira");
        when(sedeDao.update(1L, dto)).thenReturn(Optional.of(actualizada));

        SedeDTO result = sedeService.updateSede(1L, dto);

        assertEquals("Pereira", result.getCiudad());
        verify(sedeDao, never()).existsByNombreAndIdNot(any(), any());
    }

    @Test
    void testUpdateSede_NombreDuplicado() {
        SedeUpdateDTO dto = new SedeUpdateDTO();
        dto.setNombre("Sede Norte");
        when(sedeDao.existsByNombreAndIdNot("Sede Norte", 1L)).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> sedeService.updateSede(1L, dto));

        assertEquals("Ya existe otra sede registrada con el nombre: Sede Norte", ex.getMessage());
        verify(sedeDao, never()).update(any(), any());
    }

    @Test
    void testUpdateSede_NotFound() {
        SedeUpdateDTO dto = new SedeUpdateDTO();
        dto.setNombre("Nuevo Nombre");
        when(sedeDao.existsByNombreAndIdNot("Nuevo Nombre", 99L)).thenReturn(false);
        when(sedeDao.update(99L, dto)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> sedeService.updateSede(99L, dto));

        assertEquals("Sede no encontrada con ID: 99", ex.getMessage());
    }

    // ---------- RF-02 desactivar / activar ----------

    @Test
    void testDesactivarSede_Success() {
        when(sedeDao.findById(1L)).thenReturn(Optional.of(sede(1L, "Central", true)));
        when(ejemplarDao.countBySedeActualIdAndEstadoNot(1L, EstadoEjemplar.DISPONIBLE)).thenReturn(0L);
        when(sedeDao.cambiarEstado(1L, false)).thenReturn(Optional.of(sede(1L, "Central", false)));

        SedeDTO result = sedeService.desactivarSede(1L);

        assertFalse(result.getActiva());
        verify(sedeDao, times(1)).cambiarEstado(1L, false);
    }

    @Test
    void testDesactivarSede_YaDesactivada() {
        when(sedeDao.findById(1L)).thenReturn(Optional.of(sede(1L, "Central", false)));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> sedeService.desactivarSede(1L));

        assertEquals("La sede ya se encuentra desactivada", ex.getMessage());
        verify(sedeDao, never()).cambiarEstado(any(), anyBoolean());
        verifyNoInteractions(ejemplarDao);
    }

    @Test
    void testDesactivarSede_ConEjemplaresPrestadosOEnTraslado() {
        when(sedeDao.findById(1L)).thenReturn(Optional.of(sede(1L, "Central", true)));
        when(ejemplarDao.countBySedeActualIdAndEstadoNot(1L, EstadoEjemplar.DISPONIBLE)).thenReturn(3L);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> sedeService.desactivarSede(1L));

        assertTrue(ex.getMessage().contains("3 ejemplar(es)"));
        verify(sedeDao, never()).cambiarEstado(any(), anyBoolean());
    }

    @Test
    void testDesactivarSede_NotFound() {
        when(sedeDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> sedeService.desactivarSede(99L));

        verify(sedeDao, never()).cambiarEstado(any(), anyBoolean());
    }

    @Test
    void testDesactivarSede_CambioDeEstadoNoEncontrado() {
        when(sedeDao.findById(1L)).thenReturn(Optional.of(sede(1L, "Central", true)));
        when(ejemplarDao.countBySedeActualIdAndEstadoNot(1L, EstadoEjemplar.DISPONIBLE)).thenReturn(0L);
        when(sedeDao.cambiarEstado(1L, false)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> sedeService.desactivarSede(1L));
    }

    @Test
    void testActivarSede_Success() {
        when(sedeDao.findById(1L)).thenReturn(Optional.of(sede(1L, "Central", false)));
        when(sedeDao.cambiarEstado(1L, true)).thenReturn(Optional.of(sede(1L, "Central", true)));

        SedeDTO result = sedeService.activarSede(1L);

        assertTrue(result.getActiva());
        verify(sedeDao, times(1)).cambiarEstado(1L, true);
    }

    @Test
    void testActivarSede_YaActiva() {
        when(sedeDao.findById(1L)).thenReturn(Optional.of(sede(1L, "Central", true)));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> sedeService.activarSede(1L));

        assertEquals("La sede ya se encuentra activa", ex.getMessage());
        verify(sedeDao, never()).cambiarEstado(any(), anyBoolean());
    }

    @Test
    void testActivarSede_CambioDeEstadoNoEncontrado() {
        when(sedeDao.findById(1L)).thenReturn(Optional.of(sede(1L, "Central", false)));
        when(sedeDao.cambiarEstado(1L, true)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> sedeService.activarSede(1L));
    }
}
