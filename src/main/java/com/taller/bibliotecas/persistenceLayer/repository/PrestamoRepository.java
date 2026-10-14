package com.taller.bibliotecas.persistenceLayer.repository;

import com.taller.bibliotecas.businessLayer.dto.ConteoLibroDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoPrestamo;
import com.taller.bibliotecas.persistenceLayer.entity.PrestamoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<PrestamoEntity, Long> {

    List<PrestamoEntity> findByClienteEntityId(Long clienteId);

    List<PrestamoEntity> findByEstado(EstadoPrestamo estado);

    List<PrestamoEntity> findByFechaInicioBetween(LocalDate desde, LocalDate hasta);

    List<PrestamoEntity> findByFechaDevolucionBetween(LocalDate desde, LocalDate hasta);

    @Query("select new com.taller.bibliotecas.businessLayer.dto.ConteoLibroDTO(e.libroEntity.id, count(p)) "
            + "from PrestamoEntity p join p.ejemplarEntity e group by e.libroEntity.id")
    List<ConteoLibroDTO> contarPrestamosPorLibro();
}
