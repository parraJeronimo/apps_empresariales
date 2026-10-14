package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.CategoriaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaUpdateDTO;

import java.util.List;

public interface CategoriaService {

    /** RF-05: Crear una categoria o, si trae categoriaPadreId, una subcategoria */
    CategoriaDTO createCategoria(CategoriaCreateDTO createDTO);

    CategoriaDTO getCategoriaById(Long id);

    List<CategoriaDTO> getAllCategorias();

    /** Categorias de primer nivel (sin padre) */
    List<CategoriaDTO> getCategoriasRaiz();

    /** Subcategorias directas de una categoria */
    List<CategoriaDTO> getSubcategorias(Long categoriaPadreId);

    CategoriaDTO updateCategoria(Long id, CategoriaUpdateDTO updateDTO);

    /** Elimina una categoria solo si no tiene subcategorias ni libros asociados */
    void deleteCategoria(Long id);

    /** Uso interno (busquedas): ID de la categoria + los IDs de todas sus subcategorias, a cualquier nivel */
    List<Long> getIdsConDescendientes(Long categoriaId);
}
