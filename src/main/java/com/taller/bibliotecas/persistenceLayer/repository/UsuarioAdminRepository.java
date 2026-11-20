package com.taller.bibliotecas.persistenceLayer.repository;

import com.taller.bibliotecas.persistenceLayer.entity.UsuarioAdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioAdminRepository extends JpaRepository<UsuarioAdminEntity, Long> {

    Optional<UsuarioAdminEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<UsuarioAdminEntity> findBySedeEntityId(Long sedeId);
}
