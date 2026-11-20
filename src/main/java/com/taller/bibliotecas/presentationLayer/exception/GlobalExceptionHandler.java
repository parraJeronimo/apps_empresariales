package com.taller.bibliotecas.presentationLayer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Manejador global de excepciones.
 * Centraliza la traduccion de errores de negocio/validacion a codigos HTTP:
 * 400 Bad Request, 404 Not Found y 500 Internal Server Error.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RecursoNoEncontradoException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler({SolicitudInvalidaException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        log.warn("Solicitud invalida: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.warn("Errores de validacion: {}", detalles);
        return build(HttpStatus.BAD_REQUEST, "Datos de entrada invalidos", detalles);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleParametrosInvalidos(Exception ex) {
        log.warn("Parametros o cuerpo de la peticion invalidos: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Parametros o cuerpo de la peticion invalidos", null);
    }

    /** Credenciales incorrectas en el login. Mensaje unico para no revelar si el email existe. */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Inicio de sesion fallido");
        return build(HttpStatus.UNAUTHORIZED, "Email o contrasena incorrectos", null);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(DisabledException ex) {
        log.warn("Inicio de sesion con cuenta desactivada");
        return build(HttpStatus.FORBIDDEN, "La cuenta esta desactivada", null);
    }

    /** Sin esto, un @PreAuthorize que niega el acceso caeria en el handler generico y responderia 500. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "No tiene permisos para acceder a este recurso", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Error inesperado en el servidor", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado en el servidor", null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, List<String> detalles) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, detalles);
        return ResponseEntity.status(status).body(body);
    }
}
