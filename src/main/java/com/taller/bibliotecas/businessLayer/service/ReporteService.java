package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.LibroSolicitadoDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteClientesDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteDevolucionesDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteLibrosDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteMovimientosDTO;
import com.taller.bibliotecas.businessLayer.dto.ReportePrestamosDTO;

import java.time.LocalDate;
import java.util.List;

/** RF-24: Reportes sobre prestamos, devoluciones, libros, clientes y movimientos entre sedes. */
public interface ReporteService {

    ReportePrestamosDTO generarReportePrestamos(LocalDate desde, LocalDate hasta, Long sedeId);

    ReporteDevolucionesDTO generarReporteDevoluciones(LocalDate desde, LocalDate hasta, Long sedeId);

    ReporteLibrosDTO generarReporteLibros();

    ReporteClientesDTO generarReporteClientes();

    ReporteMovimientosDTO generarReporteMovimientos(LocalDate desde, LocalDate hasta, Long sedeId);

    /** Libros mas solicitados (prestamos + reservas), de mayor a menor, limitados a las primeras N posiciones */
    List<LibroSolicitadoDTO> getLibrosMasSolicitados(int limite);
}
