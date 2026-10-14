package com.taller.bibliotecas.presentationLayer.controller;

import com.taller.bibliotecas.businessLayer.dto.LibroSolicitadoDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteClientesDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteDevolucionesDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteLibrosDTO;
import com.taller.bibliotecas.businessLayer.dto.ReporteMovimientosDTO;
import com.taller.bibliotecas.businessLayer.dto.ReportePrestamosDTO;
import com.taller.bibliotecas.businessLayer.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "RF-24: reportes de prestamos, devoluciones, libros, clientes y movimientos entre sedes")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/prestamos")
    @Operation(summary = "RF-24: Reporte de prestamos", description = "Fechas en formato yyyy-MM-dd; sedeId opcional")
    public ResponseEntity<ReportePrestamosDTO> prestamos(
            @Parameter(description = "Fecha inicial", example = "2026-10-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha final", example = "2026-10-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Long sedeId) {
        return ResponseEntity.ok(reporteService.generarReportePrestamos(desde, hasta, sedeId));
    }

    @GetMapping("/devoluciones")
    @Operation(summary = "RF-24: Reporte de devoluciones", description = "Incluye cuantas fueron con retraso y cuantas en una sede distinta a la del prestamo")
    public ResponseEntity<ReporteDevolucionesDTO> devoluciones(
            @Parameter(description = "Fecha inicial", example = "2026-10-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha final", example = "2026-10-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Long sedeId) {
        return ResponseEntity.ok(reporteService.generarReporteDevoluciones(desde, hasta, sedeId));
    }

    @GetMapping("/libros")
    @Operation(summary = "RF-24: Reporte de libros y ejemplares por estado y por sede")
    public ResponseEntity<ReporteLibrosDTO> libros() {
        return ResponseEntity.ok(reporteService.generarReporteLibros());
    }

    @GetMapping("/clientes")
    @Operation(summary = "RF-24: Reporte de clientes con prestamos activos y vencidos")
    public ResponseEntity<ReporteClientesDTO> clientes() {
        return ResponseEntity.ok(reporteService.generarReporteClientes());
    }

    @GetMapping("/movimientos-sedes")
    @Operation(summary = "RF-24: Reporte de traslados de ejemplares entre sedes")
    public ResponseEntity<ReporteMovimientosDTO> movimientos(
            @Parameter(description = "Fecha inicial", example = "2026-10-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha final", example = "2026-10-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Long sedeId) {
        return ResponseEntity.ok(reporteService.generarReporteMovimientos(desde, hasta, sedeId));
    }

    @GetMapping("/libros-mas-solicitados")
    @Operation(summary = "Libros mas solicitados", description = "Ranking por prestamos + reservas, de mayor a menor (limite por defecto: 10)")
    public ResponseEntity<List<LibroSolicitadoDTO>> librosMasSolicitados(
            @RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(reporteService.getLibrosMasSolicitados(limite));
    }
}
