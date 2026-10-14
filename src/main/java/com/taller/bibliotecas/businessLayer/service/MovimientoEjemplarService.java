package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarDTO;

import java.util.List;

public interface MovimientoEjemplarService {

    /** Uso interno: deja constancia de un movimiento del ejemplar (registro, prestamo, devolucion, traslado) */
    MovimientoEjemplarDTO registrarMovimiento(MovimientoEjemplarCreateDTO createDTO);

    /** RF-20: Historial de movimientos y ubicaciones de un ejemplar, del mas antiguo al mas reciente */
    List<MovimientoEjemplarDTO> getHistorialByEjemplar(Long ejemplarId);
}
