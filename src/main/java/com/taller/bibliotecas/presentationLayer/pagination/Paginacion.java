package com.taller.bibliotecas.presentationLayer.pagination;

import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

/**
 * Paginacion opcional de los listados base, sin cambiar el cuerpo de la respuesta.
 *
 * <ul>
 *   <li>Sin {@code page} ni {@code size}: el listado se comporta como siempre (arreglo JSON completo, sin headers).</li>
 *   <li>Con {@code page} y/o {@code size}: el cuerpo sigue siendo un arreglo JSON, pero solo con los elementos de esa
 *       pagina, y los datos de paginacion viajan en headers (ver constantes {@code HEADER_*}).</li>
 * </ul>
 *
 * {@code page} empieza en 0. Si solo llega {@code page}, {@code size} vale {@link #TAMANO_POR_DEFECTO}; si solo llega
 * {@code size}, {@code page} vale 0. El orden de las paginas es siempre por {@code id} ascendente, para que dos
 * paginas consecutivas nunca repitan ni se salten elementos.
 */
public final class Paginacion {

    public static final String HEADER_TOTAL_COUNT = "X-Total-Count";
    public static final String HEADER_TOTAL_PAGES = "X-Total-Pages";
    public static final String HEADER_PAGE = "X-Page";
    public static final String HEADER_PAGE_SIZE = "X-Page-Size";

    public static final int TAMANO_POR_DEFECTO = 20;
    public static final int TAMANO_MAXIMO = 100;

    private Paginacion() {
    }

    /**
     * Interpreta los parametros de la peticion.
     *
     * @return vacio si no se pidio paginar (ni page ni size); de lo contrario, el Pageable a usar
     * @throws SolicitudInvalidaException (HTTP 400) si page es negativo o size esta fuera de 1..{@value #TAMANO_MAXIMO}
     */
    public static Optional<Pageable> resolver(Integer page, Integer size) {
        if (page == null && size == null) {
            return Optional.empty();
        }
        int pagina = page != null ? page : 0;
        int tamano = size != null ? size : TAMANO_POR_DEFECTO;
        if (pagina < 0) {
            throw new SolicitudInvalidaException("El parametro page debe ser mayor o igual a 0");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new SolicitudInvalidaException("El parametro size debe estar entre 1 y " + TAMANO_MAXIMO);
        }
        return Optional.of(PageRequest.of(pagina, tamano, Sort.by(Sort.Direction.ASC, "id")));
    }

    /** Respuesta 200 con el arreglo de la pagina en el cuerpo y los datos de paginacion en los headers. */
    public static <T> ResponseEntity<List<T>> respuesta(Page<T> pagina) {
        return ResponseEntity.ok()
                .header(HEADER_TOTAL_COUNT, String.valueOf(pagina.getTotalElements()))
                .header(HEADER_TOTAL_PAGES, String.valueOf(pagina.getTotalPages()))
                .header(HEADER_PAGE, String.valueOf(pagina.getNumber()))
                .header(HEADER_PAGE_SIZE, String.valueOf(pagina.getSize()))
                .body(pagina.getContent());
    }
}
