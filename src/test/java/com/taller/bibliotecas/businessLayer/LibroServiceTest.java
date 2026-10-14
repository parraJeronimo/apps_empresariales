package com.taller.bibliotecas.businessLayer;

import static com.taller.bibliotecas.businessLayer.TestData.categoria;
import static com.taller.bibliotecas.businessLayer.TestData.libro;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.LibroCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.CategoriaService;
import com.taller.bibliotecas.businessLayer.service.impl.LibroServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.EjemplarDAO;
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
@DisplayName("LibroService - Unit Tests (RF-03, RF-05, RF-06, RF-07)")
class LibroServiceTest {

    @Mock
    private LibroDAO libroDao;

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private EjemplarDAO ejemplarDao;

    @InjectMocks
    private LibroServiceImpl libroService;

    private LibroCreateDTO crearDto(String isbn, Long categoriaId) {
        LibroCreateDTO dto = new LibroCreateDTO();
        dto.setTitulo("Cien anios de soledad");
        dto.setAutor("Gabriel Garcia Marquez");
        dto.setIsbn(isbn);
        dto.setCategoriaId(categoriaId);
        return dto;
    }

    // ---------- RF-03 registrar ----------

    @Test
    void testCreateLibro_SinCategoria() {
        LibroCreateDTO dto = crearDto("978-1", null);
        when(libroDao.existsByIsbn("978-1")).thenReturn(false);
        when(libroDao.save(dto)).thenReturn(libro(1L, "Cien anios de soledad", "978-1"));

        LibroDTO result = libroService.createLibro(dto);

        assertEquals(1L, result.getId());
        verifyNoInteractions(categoriaService);
    }

    @Test
    void testCreateLibro_ConCategoria() {
        LibroCreateDTO dto = crearDto("978-1", 5L);
        when(libroDao.existsByIsbn("978-1")).thenReturn(false);
        when(categoriaService.getCategoriaById(5L)).thenReturn(categoria(5L, "Novela", null));
        when(libroDao.save(dto)).thenReturn(libro(1L, "Cien anios de soledad", "978-1"));

        libroService.createLibro(dto);

        verify(categoriaService, times(1)).getCategoriaById(5L);
        verify(libroDao, times(1)).save(dto);
    }

    @Test
    void testCreateLibro_IsbnDuplicado() {
        LibroCreateDTO dto = crearDto("978-1", null);
        when(libroDao.existsByIsbn("978-1")).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> libroService.createLibro(dto));

        assertEquals("Ya existe un libro registrado con el ISBN: 978-1", ex.getMessage());
        verify(libroDao, never()).save(any());
    }

    @Test
    void testCreateLibro_CategoriaNoExiste() {
        LibroCreateDTO dto = crearDto("978-1", 99L);
        when(libroDao.existsByIsbn("978-1")).thenReturn(false);
        when(categoriaService.getCategoriaById(99L))
                .thenThrow(new RecursoNoEncontradoException("Categoria no encontrada con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> libroService.createLibro(dto));

        verify(libroDao, never()).save(any());
    }

    // ---------- consultas / RF-06 catalogo ----------

    @Test
    void testGetLibroById_Found() {
        when(libroDao.findById(1L)).thenReturn(Optional.of(libro(1L, "Rayuela", "978-2")));

        assertEquals("Rayuela", libroService.getLibroById(1L).getTitulo());
    }

    @Test
    void testGetLibroById_NotFound() {
        when(libroDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> libroService.getLibroById(99L));

        assertEquals("Libro no encontrado con ID: 99", ex.getMessage());
    }

    @Test
    void testGetAllLibros_ReturnsList() {
        when(libroDao.findAll()).thenReturn(List.of(libro(1L, "A", "1"), libro(2L, "B", "2")));

        assertEquals(2, libroService.getAllLibros().size());
    }

    @Test
    void testGetCatalogo_ReturnsBooksWithAvailableCopies() {
        when(libroDao.findConEjemplaresDisponibles()).thenReturn(List.of(libro(1L, "A", "1")));

        List<LibroDTO> result = libroService.getCatalogo();

        assertEquals(1, result.size());
        verify(libroDao, times(1)).findConEjemplaresDisponibles();
    }

    // ---------- RF-07 busqueda ----------

    @Test
    void testBuscarLibros_SinCategoria() {
        when(libroDao.buscar("rayuela", null, null, null, null)).thenReturn(List.of(libro(1L, "Rayuela", "978-2")));

        List<LibroDTO> result = libroService.buscarLibros("rayuela", null, null, null, null);

        assertEquals(1, result.size());
        verifyNoInteractions(categoriaService);
    }

    @Test
    void testBuscarLibros_ConCategoriaIncluyeSubcategorias() {
        when(categoriaService.getIdsConDescendientes(1L)).thenReturn(List.of(1L, 2L));
        when(libroDao.buscar(null, "cortazar", null, List.of(1L, 2L), "EJ-001"))
                .thenReturn(List.of(libro(1L, "Rayuela", "978-2")));

        List<LibroDTO> result = libroService.buscarLibros(null, "cortazar", null, 1L, "EJ-001");

        assertEquals(1, result.size());
        verify(categoriaService, times(1)).getIdsConDescendientes(1L);
    }

    @Test
    void testBuscarLibros_CategoriaNoExiste() {
        when(categoriaService.getIdsConDescendientes(99L))
                .thenThrow(new RecursoNoEncontradoException("Categoria no encontrada con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class,
                () -> libroService.buscarLibros(null, null, null, 99L, null));

        verifyNoInteractions(libroDao);
    }

    // ---------- actualizar ----------

    @Test
    void testUpdateLibro_Success() {
        LibroUpdateDTO dto = new LibroUpdateDTO();
        dto.setEditorial("Alfaguara");
        when(libroDao.update(1L, dto)).thenReturn(Optional.of(libro(1L, "Rayuela", "978-2")));

        LibroDTO result = libroService.updateLibro(1L, dto);

        assertEquals(1L, result.getId());
        verify(libroDao, never()).existsByIsbnAndIdNot(any(), any());
        verifyNoInteractions(categoriaService);
    }

    @Test
    void testUpdateLibro_ReclasificaEnOtraCategoria() {
        LibroUpdateDTO dto = new LibroUpdateDTO();
        dto.setCategoriaId(7L);
        when(categoriaService.getCategoriaById(7L)).thenReturn(categoria(7L, "Cuento", null));
        when(libroDao.update(1L, dto)).thenReturn(Optional.of(libro(1L, "Rayuela", "978-2")));

        libroService.updateLibro(1L, dto);

        verify(categoriaService, times(1)).getCategoriaById(7L);
    }

    @Test
    void testUpdateLibro_IsbnDuplicado() {
        LibroUpdateDTO dto = new LibroUpdateDTO();
        dto.setIsbn("978-9");
        when(libroDao.existsByIsbnAndIdNot("978-9", 1L)).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> libroService.updateLibro(1L, dto));

        assertEquals("Ya existe otro libro registrado con el ISBN: 978-9", ex.getMessage());
        verify(libroDao, never()).update(any(), any());
    }

    @Test
    void testUpdateLibro_CategoriaNoExiste() {
        LibroUpdateDTO dto = new LibroUpdateDTO();
        dto.setCategoriaId(99L);
        when(categoriaService.getCategoriaById(99L))
                .thenThrow(new RecursoNoEncontradoException("Categoria no encontrada con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> libroService.updateLibro(1L, dto));

        verify(libroDao, never()).update(any(), any());
    }

    @Test
    void testUpdateLibro_NotFound() {
        LibroUpdateDTO dto = new LibroUpdateDTO();
        when(libroDao.update(99L, dto)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> libroService.updateLibro(99L, dto));

        assertEquals("Libro no encontrado con ID: 99", ex.getMessage());
    }

    // ---------- eliminar ----------

    @Test
    void testDeleteLibro_Success() {
        when(libroDao.existsById(1L)).thenReturn(true);
        when(ejemplarDao.existsByLibroId(1L)).thenReturn(false);

        libroService.deleteLibro(1L);

        verify(libroDao, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteLibro_NotFound() {
        when(libroDao.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> libroService.deleteLibro(99L));

        verify(libroDao, never()).deleteById(any());
    }

    @Test
    void testDeleteLibro_ConEjemplares() {
        when(libroDao.existsById(1L)).thenReturn(true);
        when(ejemplarDao.existsByLibroId(1L)).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> libroService.deleteLibro(1L));

        assertEquals("No se puede eliminar el libro porque tiene ejemplares registrados", ex.getMessage());
        verify(libroDao, never()).deleteById(any());
    }
}
