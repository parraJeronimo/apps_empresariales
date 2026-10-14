package com.taller.bibliotecas.persistenceLayer.repository;

import com.taller.bibliotecas.persistenceLayer.entity.MovimientoEjemplarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoEjemplarRepository extends JpaRepository<MovimientoEjemplarEntity, Long> {

    List<MovimientoEjemplarEntity> findByEjemplarEntityIdOrderByFechaMovimientoAscIdAsc(Long ejemplarId);
}
