package com.taller.bibliotecas.businessLayer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarDTO;
import com.taller.bibliotecas.businessLayer.service.impl.MovimientoEjemplarServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.EjemplarDAO;
import com.taller.bibliotecas.persistenceLayer.dao.MovimientoEjemplarDAO;
import com.taller.bibliotecas.persistenceLayer.entity.TipoMovimiento;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimientoEjemplarService - Unit Tests (RF-20)")
class MovimientoEjemplarServiceTest {

    @Mock
    private MovimientoEjemplarDAO movimientoDao;

    @Mock
    private EjemplarDAO ejemplarDao;

    @InjectMocks
    private MovimientoEjemplarServiceImpl movimientoService;

    private MovimientoEjemplarDTO movimiento(Long id, TipoMovimiento tipo) {
        MovimientoEjemplarDTO dto = new MovimientoEjemplarDTO();
        dto.setId(id);
        dto.setEjemplarId(1L);
        dto.setTipo(tipo);
        return dto;
    }

    @Test
    void testRegistrarMovimiento_Success() {
        MovimientoEjemplarCreateDTO dto =
                new MovimientoEjemplarCreateDTO(1L, TipoMovimiento.PRESTAMO, 2L, null, "Prestado");
        when(movimientoDao.save(dto)).thenReturn(movimiento(5L, TipoMovimiento.PRESTAMO));

        MovimientoEjemplarDTO result = movimientoService.registrarMovimiento(dto);

        assertEquals(5L, result.getId());
        assertEquals(TipoMovimiento.PRESTAMO, result.getTipo());
        verify(movimientoDao, times(1)).save(dto);
    }

    @Test
    void testGetHistorialByEjemplar_Success() {
        when(ejemplarDao.existsById(1L)).thenReturn(true);
        when(movimientoDao.findByEjemplarId(1L)).thenReturn(List.of(
                movimiento(1L, TipoMovimiento.REGISTRO),
                movimiento(2L, TipoMovimiento.PRESTAMO),
                movimiento(3L, TipoMovimiento.DEVOLUCION)));

        List<MovimientoEjemplarDTO> result = movimientoService.getHistorialByEjemplar(1L);

        assertEquals(3, result.size());
        assertEquals(TipoMovimiento.REGISTRO, result.get(0).getTipo());
        assertEquals(TipoMovimiento.DEVOLUCION, result.get(2).getTipo());
    }

    @Test
    void testGetHistorialByEjemplar_EjemplarNoExiste() {
        when(ejemplarDao.existsById(99L)).thenReturn(false);

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> movimientoService.getHistorialByEjemplar(99L));

        assertEquals("Ejemplar no encontrado con ID: 99", ex.getMessage());
        verify(movimientoDao, never()).findByEjemplarId(any());
    }
}
