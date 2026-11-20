package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.AuthResponseDTO;
import com.taller.bibliotecas.businessLayer.dto.LoginRequestDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;

public interface AuthService {

    /** RNF-01: Validar las credenciales y entregar un token JWT (401 si son incorrectas, 403 si la cuenta esta inactiva) */
    AuthResponseDTO login(LoginRequestDTO request);

    /** Datos del usuario autenticado a partir del email del token */
    UsuarioAdminDTO getPerfil(String email);
}
