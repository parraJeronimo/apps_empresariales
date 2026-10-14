package com.taller.bibliotecas.persistenceLayer.dao;

import com.taller.bibliotecas.businessLayer.dto.LibroCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EjemplarEntity;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.LibroEntity;
import com.taller.bibliotecas.persistenceLayer.mapper.LibroMapper;
import com.taller.bibliotecas.persistenceLayer.repository.CategoriaRepository;
import com.taller.bibliotecas.persistenceLayer.repository.LibroRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LibroDAO {

    private final LibroRepository libroRepository;
    private final CategoriaRepository categoriaRepository;
    private final LibroMapper libroMapper;

    public LibroDTO save(LibroCreateDTO createDTO) {
        LibroEntity entity = libroMapper.toEntity(createDTO);
        if (createDTO.getCategoriaId() != null) {
            entity.setCategoriaEntity(categoriaRepository.getReferenceById(createDTO.getCategoriaId()));
        }
        return libroMapper.toDTO(libroRepository.save(entity));
    }

    public Optional<LibroDTO> findById(Long id) {
        return libroRepository.findById(id).map(libroMapper::toDTO);
    }

    public List<LibroDTO> findAll() {
        return libroMapper.toDTOList(libroRepository.findAll());
    }

    public List<LibroDTO> findConEjemplaresDisponibles() {
        return libroMapper.toDTOList(libroRepository.findConEjemplaresEnEstado(EstadoEjemplar.DISPONIBLE));
    }

    /** Busqueda combinada (AND) por titulo, autor, ISBN (contienen el texto), categorias y codigo exacto de ejemplar. */
    public List<LibroDTO> buscar(String titulo, String autor, String isbn, List<Long> categoriaIds, String codigoEjemplar) {
        Specification<LibroEntity> spec = (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            if (tieneTexto(titulo)) {
                predicados.add(cb.like(cb.lower(root.get("titulo")), "%" + titulo.trim().toLowerCase() + "%"));
            }
            if (tieneTexto(autor)) {
                predicados.add(cb.like(cb.lower(root.get("autor")), "%" + autor.trim().toLowerCase() + "%"));
            }
            if (tieneTexto(isbn)) {
                predicados.add(cb.like(cb.lower(root.get("isbn")), "%" + isbn.trim().toLowerCase() + "%"));
            }
            if (categoriaIds != null && !categoriaIds.isEmpty()) {
                predicados.add(root.get("categoriaEntity").get("id").in(categoriaIds));
            }
            if (tieneTexto(codigoEjemplar)) {
                Join<LibroEntity, EjemplarEntity> ejemplares = root.join("ejemplares", JoinType.INNER);
                predicados.add(cb.equal(cb.lower(ejemplares.get("codigo")), codigoEjemplar.trim().toLowerCase()));
                query.distinct(true);
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };
        return libroMapper.toDTOList(libroRepository.findAll(spec));
    }

    public Optional<LibroDTO> update(Long id, LibroUpdateDTO updateDTO) {
        return libroRepository.findById(id).map(existing -> {
            libroMapper.updateEntityFromDTO(updateDTO, existing);
            if (updateDTO.getCategoriaId() != null) {
                existing.setCategoriaEntity(categoriaRepository.getReferenceById(updateDTO.getCategoriaId()));
            }
            return libroMapper.toDTO(libroRepository.save(existing));
        });
    }

    public boolean deleteById(Long id) {
        if (libroRepository.existsById(id)) {
            libroRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsById(Long id) {
        return libroRepository.existsById(id);
    }

    public boolean existsByIsbn(String isbn) {
        return libroRepository.existsByIsbn(isbn);
    }

    public boolean existsByIsbnAndIdNot(String isbn, Long id) {
        return libroRepository.existsByIsbnAndIdNot(isbn, id);
    }

    public boolean existsByCategoriaId(Long categoriaId) {
        return libroRepository.existsByCategoriaEntityId(categoriaId);
    }

    public long count() {
        return libroRepository.count();
    }

    private static boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}
