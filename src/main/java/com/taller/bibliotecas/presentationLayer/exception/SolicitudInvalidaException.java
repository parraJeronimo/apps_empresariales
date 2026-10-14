package com.taller.bibliotecas.presentationLayer.exception;

/** Se lanza cuando los datos de entrada o una regla de negocio no se cumplen -> HTTP 400 */
public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String message) {
        super(message);
    }
}
