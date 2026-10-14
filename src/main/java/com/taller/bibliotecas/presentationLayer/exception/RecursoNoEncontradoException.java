package com.taller.bibliotecas.presentationLayer.exception;

/** Se lanza cuando se consulta/actualiza/elimina un recurso que no existe -> HTTP 404 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
