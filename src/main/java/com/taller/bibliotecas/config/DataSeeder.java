package com.taller.bibliotecas.config;

import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminCreateDTO;
import com.taller.bibliotecas.persistenceLayer.dao.UsuarioAdminDAO;
import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Crea al arrancar el primer ADMINISTRADOR_GENERAL: sin el no habria quien inicie sesion y registre a los demas usuarios.
 * Es idempotente (no duplica) y, si no se define ninguna clave (app.admin.password vacio), no crea nada.
 */
@Configuration
@Slf4j
public class DataSeeder {

    @Bean
    CommandLineRunner crearAdministradorGeneral(UsuarioAdminDAO usuarioAdminDAO,
                                                PasswordEncoder passwordEncoder,
                                                @Value("${app.admin.email:admin@biblioteca.com}") String email,
                                                @Value("${app.admin.password:}") String password) {
        return args -> {
            if (password == null || password.isBlank()) {
                log.warn("app.admin.password no esta definida: no se crea el administrador general inicial");
                return;
            }
            String emailNormalizado = email.trim().toLowerCase();
            if (usuarioAdminDAO.existsByEmail(emailNormalizado)) {
                return;
            }
            UsuarioAdminCreateDTO admin = new UsuarioAdminCreateDTO();
            admin.setNombre("Administrador General");
            admin.setEmail(emailNormalizado);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRol(RolUsuario.ADMINISTRADOR_GENERAL);
            usuarioAdminDAO.save(admin);
            log.info("Administrador general inicial creado: {}", emailNormalizado);
        };
    }
}
