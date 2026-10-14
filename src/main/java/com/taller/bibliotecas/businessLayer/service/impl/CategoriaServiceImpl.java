package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.CategoriaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.CategoriaService;
import com.taller.bibliotecas.persistenceLayer.dao.CategoriaDAO;
import com.taller.bibliotecas.persistenceLayer.dao.LibroDAO;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaDAO categoriaDAO;
    private final LibroDAO libroDAO;

    @Override
    public CategoriaDTO createCategoria(CategoriaCreateDTO createDTO) {
        log.info("Creando categoria: {}", createDTO.getNombre());
        if (createDTO.getCategoriaPadreId() != null) {
            // Valida que la categoria padre exista (404 si no)
            getCategoriaById(createDTO.getCategoriaPadreId());
        }
        return categoriaDAO.save(createDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO getCategoriaById(Long id) {
        return categoriaDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> getAllCategorias() {
        return categoriaDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> getCategoriasRaiz() {
        return categoriaDAO.findRaices();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> getSubcategorias(Long categoriaPadreId) {
        getCategoriaById(categoriaPadreId);
        return categoriaDAO.findByPadreId(categoriaPadreId);
    }

    @Override
    public CategoriaDTO updateCategoria(Long id, CategoriaUpdateDTO updateDTO) {
        return categoriaDAO.update(id, updateDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoria no encontrada con ID: " + id));
    }

    @Override
    public void deleteCategoria(Long id) {
        if (!categoriaDAO.existsById(id)) {
            throw new RecursoNoEncontradoException("Categoria no encontrada con ID: " + id);
        }
        if (categoriaDAO.existsByPadreId(id)) {
            throw new SolicitudInvalidaException("No se puede eliminar la categoria porque tiene subcategorias");
        }
        if (libroDAO.existsByCategoriaId(id)) {
            throw new SolicitudInvalidaException("No se puede eliminar la categoria porque tiene libros asociados");
        }
        categoriaDAO.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getIdsConDescendientes(Long categoriaId) {
        getCategoriaById(categoriaId);
        List<Long> ids = new ArrayList<>();
        Deque<Long> pendientes = new ArrayDeque<>();
        pendientes.add(categoriaId);
        while (!pendientes.isEmpty()) {
            Long actual = pendientes.poll();
            ids.add(actual);
            categoriaDAO.findByPadreId(actual).forEach(hija -> pendientes.add(hija.getId()));
        }
        return ids;
    }
}
