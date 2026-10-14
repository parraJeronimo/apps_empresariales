package com.taller.bibliotecas.persistenceLayer.repository;

import com.taller.bibliotecas.businessLayer.dto.ConteoLibroDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoReserva;
import com.taller.bibliotecas.persistenceLayer.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    List<ReservaEntity> findByClienteEntityId(Long clienteId);

    List<ReservaEntity> findBySedeEntityId(Long sedeId);

    List<ReservaEntity> findBySedeEntityIdAndEstado(Long sedeId, EstadoReserva estado);

    boolean existsByClienteEntityIdAndLibroEntityIdAndSedeEntityIdAndEstado(
            Long clienteId, Long libroId, Long sedeId, EstadoReserva estado);

    @Query("select new com.taller.bibliotecas.businessLayer.dto.ConteoLibroDTO(r.libroEntity.id, count(r)) "
            + "from ReservaEntity r group by r.libroEntity.id")
    List<ConteoLibroDTO> contarReservasPorLibro();
}
