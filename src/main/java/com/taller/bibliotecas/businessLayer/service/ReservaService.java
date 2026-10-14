package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.ReservaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaDTO;
import com.taller.bibliotecas.businessLayer.dto.ReservaRechazoDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoReserva;

import java.util.List;

public interface ReservaService {

    /** RF-21: Reservar un libro que no tiene ejemplares disponibles en la sede solicitada */
    ReservaDTO createReserva(ReservaCreateDTO createDTO);

    /** RF-22: Consultar una reserva */
    ReservaDTO getReservaById(Long id);

    List<ReservaDTO> getAllReservas();

    /** RF-22: Reservas realizadas por un cliente */
    List<ReservaDTO> getReservasByCliente(Long clienteId);

    /** Solicitudes recibidas por una sede (para sus administradores); estado opcional para filtrar (ej. PENDIENTE) */
    List<ReservaDTO> getReservasBySede(Long sedeId, EstadoReserva estado);

    /** RF-22: El administrador acepta una solicitud pendiente */
    ReservaDTO aprobarReserva(Long id);

    /** RF-22: El administrador rechaza una solicitud pendiente indicando obligatoriamente el motivo */
    ReservaDTO rechazarReserva(Long id, ReservaRechazoDTO rechazoDTO);

    /** RF-22: El cliente cancela su reserva (pendiente o aprobada) */
    ReservaDTO cancelarReserva(Long id);
}
