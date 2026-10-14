package com.taller.bibliotecas.persistenceLayer.repository;

import com.taller.bibliotecas.persistenceLayer.entity.EjemplarEntity;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjemplarRepository extends JpaRepository<EjemplarEntity, Long> {

    boolean existsByCodigo(String codigo);

    Optional<EjemplarEntity> findByCodigo(String codigo);

    List<EjemplarEntity> findByLibroEntityId(Long libroId);

    List<EjemplarEntity> findBySedeActualEntityId(Long sedeId);

    boolean existsByLibroEntityId(Long libroId);

    long countByLibroEntityId(Long libroId);

    long countByEstado(EstadoEjemplar estado);

    long countBySedeActualEntityId(Long sedeId);

    long countByLibroEntityIdAndSedeActualEntityIdAndEstado(Long libroId, Long sedeId, EstadoEjemplar estado);

    long countBySedeActualEntityIdAndEstadoNot(Long sedeId, EstadoEjemplar estado);
}
