package com.taller.bibliotecas.persistenceLayer.repository;

import com.taller.bibliotecas.persistenceLayer.entity.TrasladoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrasladoRepository extends JpaRepository<TrasladoEntity, Long> {

    List<TrasladoEntity> findBySedeOrigenEntityIdOrSedeDestinoEntityId(Long sedeOrigenId, Long sedeDestinoId);

    List<TrasladoEntity> findByFechaSolicitudBetween(LocalDateTime desde, LocalDateTime hasta);
}
