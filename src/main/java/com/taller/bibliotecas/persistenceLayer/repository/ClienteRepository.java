package com.taller.bibliotecas.persistenceLayer.repository;

import com.taller.bibliotecas.persistenceLayer.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByDocumento(String documento);

    boolean existsByDocumento(String documento);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
