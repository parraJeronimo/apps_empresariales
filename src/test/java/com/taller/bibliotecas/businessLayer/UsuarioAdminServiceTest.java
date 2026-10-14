package com.taller.bibliotecas.businessLayer;

import static com.taller.bibliotecas.businessLayer.TestData.sede;
import static com.taller.bibliotecas.businessLayer.TestData.usuario;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.SedeService;
import com.taller.bibliotecas.businessLayer.service.impl.UsuarioAdminServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.UsuarioAdminDAO;
import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioAdminService - Unit Tests (RF-23)")
class UsuarioAdminServiceTest {

    @Mock
    private UsuarioAdminDAO usuarioDao;

    @Mock
    private SedeService sedeService;

    @InjectMocks
    private UsuarioAdminServiceImpl usuarioService;

    private UsuarioAdminCreateDTO crearDto(RolUsuario rol, Long sedeId) {
        UsuarioAdminCreateDTO dto = new UsuarioAdminCreateDTO();
        dto.setNombre("Maria Lopez");
        dto.setEmail("maria@biblioteca.com");
        dto.setRol(rol);
        dto.setSedeId(sedeId);
        return dto;
    }

    // ---------- RF-23 registrar ----------

    @Test
    void testCreateUsuario_AdministradorDeSede() {
        UsuarioAdminCreateDTO dto = crearDto(RolUsuario.ADMINISTRADOR_SEDE, 1L);
        when(usuarioDao.existsByEmail("maria@biblioteca.com")).thenReturn(false);
        when(sedeService.getSedeById(1L)).thenReturn(sede(1L, "Central", true));
        when(usuarioDao.save(dto)).thenReturn(usuario(1L, RolUsuario.ADMINISTRADOR_SEDE, 1L));

        UsuarioAdminDTO result = usuarioService.createUsuario(dto);

        assertEquals(RolUsuario.ADMINISTRADOR_SEDE, result.getRol());
        assertEquals(1L, result.getSedeId());
        verify(sedeService, times(1)).getSedeById(1L);
    }

    @Test
    void testCreateUsuario_AdministradorGeneralNoNecesitaSede() {
        UsuarioAdminCreateDTO dto = crearDto(RolUsuario.ADMINISTRADOR_GENERAL, null);
        when(usuarioDao.existsByEmail("maria@biblioteca.com")).thenReturn(false);
        when(usuarioDao.save(dto)).thenReturn(usuario(1L, RolUsuario.ADMINISTRADOR_GENERAL, null));

        UsuarioAdminDTO result = usuarioService.createUsuario(dto);

        assertEquals(RolUsuario.ADMINISTRADOR_GENERAL, result.getRol());
        assertNull(result.getSedeId());
        verifyNoInteractions(sedeService);
    }

    @Test
    void testCreateUsuario_BibliotecarioSinSede() {
        UsuarioAdminCreateDTO dto = crearDto(RolUsuario.BIBLIOTECARIO, null);
        when(usuarioDao.existsByEmail("maria@biblioteca.com")).thenReturn(false);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> usuarioService.createUsuario(dto));

        assertTrue(ex.getMessage().contains("BIBLIOTECARIO"));
        verify(usuarioDao, never()).save(any());
    }

    @Test
    void testCreateUsuario_EmailDuplicado() {
        UsuarioAdminCreateDTO dto = crearDto(RolUsuario.ADMINISTRADOR_SEDE, 1L);
        when(usuarioDao.existsByEmail("maria@biblioteca.com")).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> usuarioService.createUsuario(dto));

        assertEquals("Ya existe un usuario registrado con el email: maria@biblioteca.com", ex.getMessage());
        verify(usuarioDao, never()).save(any());
    }

    @Test
    void testCreateUsuario_SedeNoExiste() {
        UsuarioAdminCreateDTO dto = crearDto(RolUsuario.ADMINISTRADOR_SEDE, 99L);
        when(usuarioDao.existsByEmail("maria@biblioteca.com")).thenReturn(false);
        when(sedeService.getSedeById(99L)).thenThrow(new RecursoNoEncontradoException("Sede no encontrada con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.createUsuario(dto));

        verify(usuarioDao, never()).save(any());
    }

    // ---------- consultas ----------

    @Test
    void testGetUsuarioById_Found() {
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario(1L, RolUsuario.ADMINISTRADOR_SEDE, 1L)));

        assertEquals(1L, usuarioService.getUsuarioById(1L).getId());
    }

    @Test
    void testGetUsuarioById_NotFound() {
        when(usuarioDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.getUsuarioById(99L));

        assertEquals("Usuario no encontrado con ID: 99", ex.getMessage());
    }

    @Test
    void testGetAllUsuarios_ReturnsList() {
        when(usuarioDao.findAll()).thenReturn(List.of(
                usuario(1L, RolUsuario.ADMINISTRADOR_GENERAL, null),
                usuario(2L, RolUsuario.BIBLIOTECARIO, 1L)));

        assertEquals(2, usuarioService.getAllUsuarios().size());
    }

    @Test
    void testGetUsuariosBySede_Success() {
        when(sedeService.getSedeById(1L)).thenReturn(sede(1L, "Central", true));
        when(usuarioDao.findBySedeId(1L)).thenReturn(List.of(usuario(2L, RolUsuario.BIBLIOTECARIO, 1L)));

        assertEquals(1, usuarioService.getUsuariosBySede(1L).size());
    }

    @Test
    void testGetUsuariosBySede_SedeNoExiste() {
        when(sedeService.getSedeById(99L)).thenThrow(new RecursoNoEncontradoException("Sede no encontrada con ID: 99"));

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.getUsuariosBySede(99L));

        verifyNoInteractions(usuarioDao);
    }

    // ---------- RF-23 actualizar / asignar rol ----------

    @Test
    void testUpdateUsuario_SoloNombre() {
        UsuarioAdminUpdateDTO dto = new UsuarioAdminUpdateDTO();
        dto.setNombre("Maria Gomez");
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario(1L, RolUsuario.ADMINISTRADOR_SEDE, 1L)));
        when(sedeService.getSedeById(1L)).thenReturn(sede(1L, "Central", true));
        when(usuarioDao.update(1L, dto)).thenReturn(Optional.of(usuario(1L, RolUsuario.ADMINISTRADOR_SEDE, 1L)));

        assertNotNull(usuarioService.updateUsuario(1L, dto));

        verify(usuarioDao, never()).existsByEmailAndIdNot(any(), any());
    }

    @Test
    void testUpdateUsuario_CambiaElRolYLaSede() {
        UsuarioAdminUpdateDTO dto = new UsuarioAdminUpdateDTO();
        dto.setRol(RolUsuario.BIBLIOTECARIO);
        dto.setSedeId(2L);
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario(1L, RolUsuario.ADMINISTRADOR_SEDE, 1L)));
        when(sedeService.getSedeById(2L)).thenReturn(sede(2L, "Norte", true));
        when(usuarioDao.update(1L, dto)).thenReturn(Optional.of(usuario(1L, RolUsuario.BIBLIOTECARIO, 2L)));

        UsuarioAdminDTO result = usuarioService.updateUsuario(1L, dto);

        assertEquals(RolUsuario.BIBLIOTECARIO, result.getRol());
        assertEquals(2L, result.getSedeId());
    }

    @Test
    void testUpdateUsuario_DeGeneralASedeRequiereSede() {
        UsuarioAdminUpdateDTO dto = new UsuarioAdminUpdateDTO();
        dto.setRol(RolUsuario.BIBLIOTECARIO);
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario(1L, RolUsuario.ADMINISTRADOR_GENERAL, null)));

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> usuarioService.updateUsuario(1L, dto));

        assertTrue(ex.getMessage().contains("BIBLIOTECARIO"));
        verify(usuarioDao, never()).update(any(), any());
    }

    @Test
    void testUpdateUsuario_EmailDeOtroUsuario() {
        UsuarioAdminUpdateDTO dto = new UsuarioAdminUpdateDTO();
        dto.setEmail("ocupado@biblioteca.com");
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario(1L, RolUsuario.ADMINISTRADOR_SEDE, 1L)));
        when(usuarioDao.existsByEmailAndIdNot("ocupado@biblioteca.com", 1L)).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> usuarioService.updateUsuario(1L, dto));

        assertEquals("Ya existe otro usuario registrado con el email: ocupado@biblioteca.com", ex.getMessage());
        verify(usuarioDao, never()).update(any(), any());
    }

    @Test
    void testUpdateUsuario_DesactivarUsuario() {
        UsuarioAdminUpdateDTO dto = new UsuarioAdminUpdateDTO();
        dto.setActivo(false);
        UsuarioAdminDTO desactivado = usuario(1L, RolUsuario.ADMINISTRADOR_GENERAL, null);
        desactivado.setActivo(false);
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario(1L, RolUsuario.ADMINISTRADOR_GENERAL, null)));
        when(usuarioDao.update(1L, dto)).thenReturn(Optional.of(desactivado));

        assertFalse(usuarioService.updateUsuario(1L, dto).getActivo());
    }

    @Test
    void testUpdateUsuario_NotFound() {
        when(usuarioDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.updateUsuario(99L, new UsuarioAdminUpdateDTO()));

        verify(usuarioDao, never()).update(any(), any());
    }

    @Test
    void testUpdateUsuario_DesaparecioAlActualizar() {
        UsuarioAdminUpdateDTO dto = new UsuarioAdminUpdateDTO();
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario(1L, RolUsuario.ADMINISTRADOR_GENERAL, null)));
        when(usuarioDao.update(1L, dto)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.updateUsuario(1L, dto));
    }
}
