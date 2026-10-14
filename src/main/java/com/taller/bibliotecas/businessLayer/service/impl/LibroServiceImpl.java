package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.LibroCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.CategoriaService;
import com.taller.bibliotecas.businessLayer.service.LibroService;
import com.taller.bibliotecas.persistenceLayer.dao.EjemplarDAO;
import com.taller.bibliotecas.persistenceLayer.dao.LibroDAO;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LibroServiceImpl implements LibroService {

    private final LibroDAO libroDAO;
    private final CategoriaService categoriaService;
    private final EjemplarDAO ejemplarDAO;

    @Override
    public LibroDTO createLibro(LibroCreateDTO createDTO) {
        log.info("Registrando libro: {}", createDTO.getTitulo());
        if (libroDAO.existsByIsbn(createDTO.getIsbn())) {
            throw new SolicitudInvalidaException("Ya existe un libro registrado con el ISBN: " + createDTO.getIsbn());
        }
        if (createDTO.getCategoriaId() != null) {
            categoriaService.getCategoriaById(createDTO.getCategoriaId());
        }
        return libroDAO.save(createDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public LibroDTO getLibroById(Long id) {
        return libroDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroDTO> getAllLibros() {
        return libroDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroDTO> getCatalogo() {
        return libroDAO.findConEjemplaresDisponibles();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroDTO> buscarLibros(String titulo, String autor, String isbn, Long categoriaId, String codigoEjemplar) {
        List<Long> categoriaIds = categoriaId == null ? null : categoriaService.getIdsConDescendientes(categoriaId);
        return libroDAO.buscar(titulo, autor, isbn, categoriaIds, codigoEjemplar);
    }

    @Override
    public LibroDTO updateLibro(Long id, LibroUpdateDTO updateDTO) {
        if (updateDTO.getIsbn() != null && libroDAO.existsByIsbnAndIdNot(updateDTO.getIsbn(), id)) {
            throw new SolicitudInvalidaException("Ya existe otro libro registrado con el ISBN: " + updateDTO.getIsbn());
        }
        if (updateDTO.getCategoriaId() != null) {
            categoriaService.getCategoriaById(updateDTO.getCategoriaId());
        }
        return libroDAO.update(id, updateDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con ID: " + id));
    }

    @Override
    public void deleteLibro(Long id) {
        if (!libroDAO.existsById(id)) {
            throw new RecursoNoEncontradoException("Libro no encontrado con ID: " + id);
        }
        if (ejemplarDAO.existsByLibroId(id)) {
            throw new SolicitudInvalidaException("No se puede eliminar el libro porque tiene ejemplares registrados");
        }
        libroDAO.deleteById(id);
    }
}
