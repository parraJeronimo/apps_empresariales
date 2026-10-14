package com.taller.bibliotecas.businessLayer;

import static com.taller.bibliotecas.businessLayer.TestData.categoria;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.CategoriaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.impl.CategoriaServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.CategoriaDAO;
import com.taller.bibliotecas.persistenceLayer.dao.LibroDAO;
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
@DisplayName("CategoriaService - Unit Tests (RF-05)")
class CategoriaServiceTest {

    @Mock
    private CategoriaDAO categoriaDao;

    @Mock
    private LibroDAO libroDao;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private CategoriaCreateDTO crearDto(String nombre, Long padreId) {
        CategoriaCreateDTO dto = new CategoriaCreateDTO();
        dto.setNombre(nombre);
        dto.setCategoriaPadreId(padreId);
        return dto;
    }

    @Test
    void testCreateCategoria_Raiz() {
        CategoriaCreateDTO dto = crearDto("Literatura", null);
        when(categoriaDao.save(dto)).thenReturn(categoria(1L, "Literatura", null));

        CategoriaDTO result = categoriaService.createCategoria(dto);

        assertEquals("Literatura", result.getNombre());
        assertNull(result.getCategoriaPadreId());
        verify(categoriaDao, never()).findById(any());
    }

    @Test
    void testCreateCategoria_Subcategoria() {
        CategoriaCreateDTO dto = crearDto("Novela", 1L);
        when(categoriaDao.findById(1L)).thenReturn(Optional.of(categoria(1L, "Literatura", null)));
        when(categoriaDao.save(dto)).thenReturn(categoria(2L, "Novela", 1L));

        CategoriaDTO result = categoriaService.createCategoria(dto);

        assertEquals(1L, result.getCategoriaPadreId());
        verify(categoriaDao, times(1)).save(dto);
    }

    @Test
    void testCreateCategoria_PadreNoExiste() {
        CategoriaCreateDTO dto = crearDto("Novela", 99L);
        when(categoriaDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> categoriaService.createCategoria(dto));

        assertEquals("Categoria no encontrada con ID: 99", ex.getMessage());
        verify(categoriaDao, never()).save(any());
    }

    @Test
    void testGetCategoriaById_Found() {
        when(categoriaDao.findById(1L)).thenReturn(Optional.of(categoria(1L, "Literatura", null)));

        assertEquals("Literatura", categoriaService.getCategoriaById(1L).getNombre());
    }

    @Test
    void testGetCategoriaById_NotFound() {
        when(categoriaDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> categoriaService.getCategoriaById(99L));

        assertEquals("Categoria no encontrada con ID: 99", ex.getMessage());
    }

    @Test
    void testGetAllCategorias_ReturnsList() {
        when(categoriaDao.findAll()).thenReturn(List.of(categoria(1L, "A", null), categoria(2L, "B", 1L)));

        assertEquals(2, categoriaService.getAllCategorias().size());
    }

    @Test
    void testGetCategoriasRaiz_ReturnsList() {
        when(categoriaDao.findRaices()).thenReturn(List.of(categoria(1L, "A", null)));

        List<CategoriaDTO> result = categoriaService.getCategoriasRaiz();

        assertEquals(1, result.size());
        verify(categoriaDao, times(1)).findRaices();
    }

    @Test
    void testGetSubcategorias_Success() {
        when(categoriaDao.findById(1L)).thenReturn(Optional.of(categoria(1L, "Literatura", null)));
        when(categoriaDao.findByPadreId(1L)).thenReturn(List.of(categoria(2L, "Novela", 1L)));

        List<CategoriaDTO> result = categoriaService.getSubcategorias(1L);

        assertEquals(1, result.size());
        assertEquals("Novela", result.get(0).getNombre());
    }

    @Test
    void testGetSubcategorias_PadreNoExiste() {
        when(categoriaDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> categoriaService.getSubcategorias(99L));

        verify(categoriaDao, never()).findByPadreId(any());
    }

    @Test
    void testUpdateCategoria_Success() {
        CategoriaUpdateDTO dto = new CategoriaUpdateDTO();
        dto.setNombre("Narrativa");
        when(categoriaDao.update(1L, dto)).thenReturn(Optional.of(categoria(1L, "Narrativa", null)));

        assertEquals("Narrativa", categoriaService.updateCategoria(1L, dto).getNombre());
    }

    @Test
    void testUpdateCategoria_NotFound() {
        CategoriaUpdateDTO dto = new CategoriaUpdateDTO();
        when(categoriaDao.update(99L, dto)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> categoriaService.updateCategoria(99L, dto));
    }

    @Test
    void testDeleteCategoria_Success() {
        when(categoriaDao.existsById(1L)).thenReturn(true);
        when(categoriaDao.existsByPadreId(1L)).thenReturn(false);
        when(libroDao.existsByCategoriaId(1L)).thenReturn(false);

        categoriaService.deleteCategoria(1L);

        verify(categoriaDao, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCategoria_NotFound() {
        when(categoriaDao.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> categoriaService.deleteCategoria(99L));

        verify(categoriaDao, never()).deleteById(any());
    }

    @Test
    void testDeleteCategoria_ConSubcategorias() {
        when(categoriaDao.existsById(1L)).thenReturn(true);
        when(categoriaDao.existsByPadreId(1L)).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> categoriaService.deleteCategoria(1L));

        assertEquals("No se puede eliminar la categoria porque tiene subcategorias", ex.getMessage());
        verifyNoInteractions(libroDao);
        verify(categoriaDao, never()).deleteById(any());
    }

    @Test
    void testDeleteCategoria_ConLibrosAsociados() {
        when(categoriaDao.existsById(1L)).thenReturn(true);
        when(categoriaDao.existsByPadreId(1L)).thenReturn(false);
        when(libroDao.existsByCategoriaId(1L)).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> categoriaService.deleteCategoria(1L));

        assertEquals("No se puede eliminar la categoria porque tiene libros asociados", ex.getMessage());
        verify(categoriaDao, never()).deleteById(any());
    }

    @Test
    void testGetIdsConDescendientes_RecorreTodosLosNiveles() {
        // 1 -> 2 -> 3
        when(categoriaDao.findById(1L)).thenReturn(Optional.of(categoria(1L, "Literatura", null)));
        when(categoriaDao.findByPadreId(1L)).thenReturn(List.of(categoria(2L, "Novela", 1L)));
        when(categoriaDao.findByPadreId(2L)).thenReturn(List.of(categoria(3L, "Novela historica", 2L)));
        when(categoriaDao.findByPadreId(3L)).thenReturn(List.of());

        List<Long> ids = categoriaService.getIdsConDescendientes(1L);

        assertEquals(List.of(1L, 2L, 3L), ids);
    }

    @Test
    void testGetIdsConDescendientes_SinHijas() {
        when(categoriaDao.findById(5L)).thenReturn(Optional.of(categoria(5L, "Hoja", null)));
        when(categoriaDao.findByPadreId(5L)).thenReturn(List.of());

        assertEquals(List.of(5L), categoriaService.getIdsConDescendientes(5L));
    }

    @Test
    void testGetIdsConDescendientes_NotFound() {
        when(categoriaDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> categoriaService.getIdsConDescendientes(99L));
    }
}
