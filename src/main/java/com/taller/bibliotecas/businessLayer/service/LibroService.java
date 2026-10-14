package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.LibroCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroUpdateDTO;

import java.util.List;

public interface LibroService {

    /** RF-03: Registrar un libro (el ISBN debe ser unico) */
    LibroDTO createLibro(LibroCreateDTO createDTO);

    LibroDTO getLibroById(Long id);

    /** Todos los libros registrados */
    List<LibroDTO> getAllLibros();

    /** RF-06: Catalogo general: libros con al menos un ejemplar disponible en cualquier sede */
    List<LibroDTO> getCatalogo();

    /**
     * RF-07: Buscar por titulo, autor, ISBN, categoria (incluye subcategorias) o codigo de ejemplar.
     * Todos los filtros son opcionales y se combinan con AND.
     */
    List<LibroDTO> buscarLibros(String titulo, String autor, String isbn, Long categoriaId, String codigoEjemplar);

    /** RF-03 / RF-05: Actualizar datos del libro y/o reclasificarlo en otra categoria */
    LibroDTO updateLibro(Long id, LibroUpdateDTO updateDTO);

    /** Elimina un libro solo si no tiene ejemplares registrados */
    void deleteLibro(Long id);
}
