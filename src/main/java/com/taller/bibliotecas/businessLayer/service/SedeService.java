package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.SedeCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeUpdateDTO;

import java.util.List;

public interface SedeService {

    /** RF-01: Registrar una nueva sede (el nombre debe ser unico) */
    SedeDTO createSede(SedeCreateDTO createDTO);

    /** RF-02: Consultar una sede (404 si no existe) */
    SedeDTO getSedeById(Long id);

    /** RF-02: Consultar todas las sedes, activas e inactivas */
    List<SedeDTO> getAllSedes();

    /** RF-02: Consultar solo las sedes activas */
    List<SedeDTO> getSedesActivas();

    /** RF-02: Modificar la informacion de una sede */
    SedeDTO updateSede(Long id, SedeUpdateDTO updateDTO);

    /** RF-02: Desactivar una sede (no se permite si tiene ejemplares prestados o en traslado) */
    SedeDTO desactivarSede(Long id);

    /** RF-02: Volver a activar una sede desactivada */
    SedeDTO activarSede(Long id);
}
