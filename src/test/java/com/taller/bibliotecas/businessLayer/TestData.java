package com.taller.bibliotecas.businessLayer;

import com.taller.bibliotecas.businessLayer.dto.CategoriaDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteDTO;
import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.dto.TrasladoDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoPrestamo;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoReserva;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoTraslado;
import com.taller.bibliotecas.persistenceLayer.entity.RolUsuario;

import java.time.LocalDate;

/** Fabrica de DTOs de prueba compartida por los tests de los servicios. */
final class TestData {

    private TestData() {
    }

    static SedeDTO sede(Long id, String nombre, boolean activa) {
        SedeDTO sede = new SedeDTO();
        sede.setId(id);
        sede.setNombre(nombre);
        sede.setDireccion("Carrera 14 # 20-30");
        sede.setCiudad("Armenia");
        sede.setActiva(activa);
        return sede;
    }

    static ClienteDTO cliente(Long id, String nombre) {
        ClienteDTO cliente = new ClienteDTO();
        cliente.setId(id);
        cliente.setNombre(nombre);
        cliente.setDocumento("1094" + id);
        cliente.setEmail(nombre.toLowerCase() + "@email.com");
        return cliente;
    }

    static LibroDTO libro(Long id, String titulo, String isbn) {
        LibroDTO libro = new LibroDTO();
        libro.setId(id);
        libro.setTitulo(titulo);
        libro.setAutor("Autor de " + titulo);
        libro.setIsbn(isbn);
        return libro;
    }

    static CategoriaDTO categoria(Long id, String nombre, Long padreId) {
        CategoriaDTO categoria = new CategoriaDTO();
        categoria.setId(id);
        categoria.setNombre(nombre);
        categoria.setCategoriaPadreId(padreId);
        return categoria;
    }

    static EjemplarDTO ejemplar(Long id, String codigo, EstadoEjemplar estado, Long libroId, Long sedeActualId) {
        EjemplarDTO ejemplar = new EjemplarDTO();
        ejemplar.setId(id);
        ejemplar.setCodigo(codigo);
        ejemplar.setEstado(estado);
        ejemplar.setLibroId(libroId);
        ejemplar.setSedePropietariaId(sedeActualId);
        ejemplar.setSedeActualId(sedeActualId);
        return ejemplar;
    }

    /** Prestamo del ejemplar 1, al cliente 2, hecho en la sede 3. */
    static PrestamoDTO prestamo(Long id, EstadoPrestamo estado, LocalDate inicio, LocalDate limite) {
        PrestamoDTO prestamo = new PrestamoDTO();
        prestamo.setId(id);
        prestamo.setEjemplarId(1L);
        prestamo.setClienteId(2L);
        prestamo.setSedePrestamoId(3L);
        prestamo.setEstado(estado);
        prestamo.setFechaInicio(inicio);
        prestamo.setFechaLimite(limite);
        return prestamo;
    }

    static ReservaDTO reserva(Long id, EstadoReserva estado) {
        ReservaDTO reserva = new ReservaDTO();
        reserva.setId(id);
        reserva.setClienteId(1L);
        reserva.setLibroId(2L);
        reserva.setSedeId(3L);
        reserva.setEstado(estado);
        return reserva;
    }

    /** Traslado del ejemplar 1, desde la sede 1 hacia la sede 2. */
    static TrasladoDTO traslado(Long id, EstadoTraslado estado) {
        TrasladoDTO traslado = new TrasladoDTO();
        traslado.setId(id);
        traslado.setEjemplarId(1L);
        traslado.setSedeOrigenId(1L);
        traslado.setSedeDestinoId(2L);
        traslado.setEstado(estado);
        return traslado;
    }

    static UsuarioAdminDTO usuario(Long id, RolUsuario rol, Long sedeId) {
        UsuarioAdminDTO usuario = new UsuarioAdminDTO();
        usuario.setId(id);
        usuario.setNombre("Usuario " + id);
        usuario.setEmail("usuario" + id + "@biblioteca.com");
        usuario.setRol(rol);
        usuario.setSedeId(sedeId);
        usuario.setActivo(true);
        return usuario;
    }
}
