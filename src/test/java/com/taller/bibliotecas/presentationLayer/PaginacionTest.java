package com.taller.bibliotecas.presentationLayer;

import static org.junit.jupiter.api.Assertions.*;

import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import com.taller.bibliotecas.presentationLayer.pagination.Paginacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@DisplayName("Paginacion opcional por headers - Unit Tests")
class PaginacionTest {

    @Test
    void sinPageNiSize_noPagina() {
        assertTrue(Paginacion.resolver(null, null).isEmpty());
    }

    @Test
    void soloPage_usaTamanoPorDefecto() {
        Pageable p = Paginacion.resolver(2, null).orElseThrow();
        assertEquals(2, p.getPageNumber());
        assertEquals(Paginacion.TAMANO_POR_DEFECTO, p.getPageSize());
    }

    @Test
    void soloSize_empiezaEnLaPaginaCero() {
        Pageable p = Paginacion.resolver(null, 5).orElseThrow();
        assertEquals(0, p.getPageNumber());
        assertEquals(5, p.getPageSize());
    }

    @Test
    void ordenaSiemprePorIdAscendente() {
        Pageable p = Paginacion.resolver(0, 10).orElseThrow();
        assertEquals(Sort.Direction.ASC, p.getSort().getOrderFor("id").getDirection());
    }

    @Test
    void pageNegativo_lanzaSolicitudInvalida() {
        assertThrows(SolicitudInvalidaException.class, () -> Paginacion.resolver(-1, 10));
    }

    @Test
    void sizeFueraDeRango_lanzaSolicitudInvalida() {
        assertThrows(SolicitudInvalidaException.class, () -> Paginacion.resolver(0, 0));
        assertThrows(SolicitudInvalidaException.class, () -> Paginacion.resolver(0, Paginacion.TAMANO_MAXIMO + 1));
        assertTrue(Paginacion.resolver(0, Paginacion.TAMANO_MAXIMO).isPresent());
    }

    @Test
    void respuesta_cuerpoEsArregloYDatosVanEnHeaders() {
        Pageable pageable = PageRequest.of(1, 2);
        ResponseEntity<List<String>> r = Paginacion.respuesta(new PageImpl<>(List.of("c", "d"), pageable, 5));

        assertEquals(List.of("c", "d"), r.getBody());
        assertEquals("5", r.getHeaders().getFirst(Paginacion.HEADER_TOTAL_COUNT));
        assertEquals("3", r.getHeaders().getFirst(Paginacion.HEADER_TOTAL_PAGES));
        assertEquals("1", r.getHeaders().getFirst(Paginacion.HEADER_PAGE));
        assertEquals("2", r.getHeaders().getFirst(Paginacion.HEADER_PAGE_SIZE));
    }
}
