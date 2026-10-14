package com.taller.bibliotecas.businessLayer.dto;

/** Filtros para consultar los prestamos de un cliente (RF-13). */
public enum FiltroPrestamo {
    /** Prestamos vigentes: estado ACTIVO y fecha limite aun no superada */
    ACTIVOS,
    /** Prestamos ACTIVO cuya fecha limite ya fue superada */
    VENCIDOS,
    /** Prestamos ya devueltos */
    FINALIZADOS
}
