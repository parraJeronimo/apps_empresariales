package com.taller.bibliotecas.persistenceLayer.repository;

import com.taller.bibliotecas.persistenceLayer.entity.UsuarioAdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioAdminRepository extends JpaRepository<UsuarioAdminEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<UsuarioAdminEntity> findBySedeEntityId(Long sedeId);
}
