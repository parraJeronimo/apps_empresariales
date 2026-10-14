package com.taller.bibliotecas.persistenceLayer.repository;

import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.LibroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<LibroEntity, Long>, JpaSpecificationExecutor<LibroEntity> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    boolean existsByCategoriaEntityId(Long categoriaId);

    @Query("select distinct l from LibroEntity l join l.ejemplares e where e.estado = :estado")
    List<LibroEntity> findConEjemplaresEnEstado(@Param("estado") EstadoEjemplar estado);
}
