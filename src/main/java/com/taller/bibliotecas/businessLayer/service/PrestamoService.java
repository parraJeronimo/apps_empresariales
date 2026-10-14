package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.DevolucionDTO;
import com.taller.bibliotecas.businessLayer.dto.FiltroPrestamo;
import com.taller.bibliotecas.businessLayer.dto.PrestamoCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.PrestamoDTO;

import java.time.LocalDate;
import java.util.List;

public interface PrestamoService {

    /** RF-11 / RF-12: Registrar un prestamo; fija la fecha de inicio (hoy) y la fecha limite */
    PrestamoDTO createPrestamo(PrestamoCreateDTO createDTO);

    PrestamoDTO getPrestamoById(Long id);

    List<PrestamoDTO> getAllPrestamos();

    /** RF-13: Prestamos de un cliente filtrados (activos, vencidos o finalizados); sin filtro devuelve todos */
    List<PrestamoDTO> getPrestamosByCliente(Long clienteId, FiltroPrestamo filtro);

    /** RF-25: Historial completo de prestamos y devoluciones de un cliente, del mas reciente al mas antiguo */
    List<PrestamoDTO> getHistorialByCliente(Long clienteId);

    /** Prestamos activos (vigentes y vencidos) de todas las sedes */
    List<PrestamoDTO> getPrestamosActivos();

    /** Prestamos activos cuya fecha limite ya fue superada */
    List<PrestamoDTO> getPrestamosVencidos();

    /** Prestamos cuya fecha de inicio esta en el rango [desde, hasta] (uso de reportes) */
    List<PrestamoDTO> getPrestamosPorRangoDeInicio(LocalDate desde, LocalDate hasta);

    /** Prestamos devueltos cuya fecha de devolucion esta en el rango [desde, hasta] (uso de reportes) */
    List<PrestamoDTO> getDevolucionesPorRango(LocalDate desde, LocalDate hasta);

    /**
     * RF-14 / RF-15 / RF-16 / RF-17: Registrar la devolucion de un ejemplar en cualquier sede.
     * Calcula si hubo retraso, guarda la sede de devolucion y deja el ejemplar disponible en esa sede.
     */
    PrestamoDTO registrarDevolucion(Long prestamoId, DevolucionDTO devolucionDTO);
}
