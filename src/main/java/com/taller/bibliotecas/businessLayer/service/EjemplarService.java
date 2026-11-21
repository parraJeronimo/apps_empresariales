package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.EjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EjemplarService {

    /** RF-04: Registrar un ejemplar fisico con codigo unico (si no se envia, se genera) */
    EjemplarDTO createEjemplar(EjemplarCreateDTO createDTO);

    EjemplarDTO getEjemplarById(Long id);

    List<EjemplarDTO> getAllEjemplares();

    /** Igual que getAllEjemplares() pero devuelve solo la pagina pedida (el controlador la expone por headers). */
    Page<EjemplarDTO> getAllEjemplares(Pageable pageable);

    EjemplarDTO getEjemplarByCodigo(String codigo);

    /** RF-08: Ejemplares de un libro con la sede donde estan y su estado */
    List<EjemplarDTO> getEjemplaresByLibro(Long libroId);

    /** RF-08: Ejemplares que se encuentran actualmente en una sede */
    List<EjemplarDTO> getEjemplaresBySede(Long sedeId);

    /** Cantidad de ejemplares DISPONIBLES de un libro en una sede */
    long contarEjemplaresDisponibles(Long libroId, Long sedeId);

    /** Uso interno (prestamos, devoluciones, traslados): cambia el estado y, si sedeActualId != null, la ubicacion */
    EjemplarDTO actualizarEstadoYUbicacion(Long ejemplarId, EstadoEjemplar estado, Long sedeActualId);
}
