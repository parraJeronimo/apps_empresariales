package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.TrasladoCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.TrasladoDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoTraslado;

import java.time.LocalDate;
import java.util.List;

public interface TrasladoService {

    /** RF-18: Registrar el traslado de un ejemplar disponible hacia otra sede; el ejemplar queda EN_TRASLADO */
    TrasladoDTO createTraslado(TrasladoCreateDTO createDTO);

    TrasladoDTO getTrasladoById(Long id);

    List<TrasladoDTO> getAllTraslados();

    /** Traslados en los que participa una sede (como origen o destino); estado opcional */
    List<TrasladoDTO> getTrasladosBySede(Long sedeId, EstadoTraslado estado);

    /** Traslados solicitados en el rango [desde, hasta] (uso de reportes) */
    List<TrasladoDTO> getTrasladosPorRango(LocalDate desde, LocalDate hasta);

    /** RF-19: La sede destino confirma la recepcion; el ejemplar cambia automaticamente de ubicacion */
    TrasladoDTO confirmarRecepcion(Long id);

    /** Cancela un traslado en transito; el ejemplar vuelve a estar disponible en su sede de origen */
    TrasladoDTO cancelarTraslado(Long id);
}
