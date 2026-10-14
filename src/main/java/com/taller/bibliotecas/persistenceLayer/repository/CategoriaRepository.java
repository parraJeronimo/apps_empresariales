package com.taller.bibliotecas.persistenceLayer.repository;

import com.taller.bibliotecas.persistenceLayer.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {

    List<CategoriaEntity> findByCategoriaPadreEntityIsNull();

    List<CategoriaEntity> findByCategoriaPadreEntityId(Long categoriaPadreId);

    boolean existsByCategoriaPadreEntityId(Long categoriaPadreId);
}
