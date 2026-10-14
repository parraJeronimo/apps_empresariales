package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.EstadoPrestamo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informacion de un prestamo")
public class PrestamoDTO {

    @Schema(description = "ID unico del prestamo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID del ejemplar prestado")
    private Long ejemplarId;

    @Schema(description = "Codigo del ejemplar", accessMode = Schema.AccessMode.READ_ONLY)
    private String ejemplarCodigo;

    @Schema(description = "ID del libro", accessMode = Schema.AccessMode.READ_ONLY)
    private Long libroId;

    @Schema(description = "Titulo del libro", accessMode = Schema.AccessMode.READ_ONLY)
    private String libroTitulo;

    @Schema(description = "ID del cliente")
    private Long clienteId;

    @Schema(description = "Nombre del cliente", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNombre;

    @Schema(description = "ID de la sede donde se presto")
    private Long sedePrestamoId;

    @Schema(description = "Nombre de la sede donde se presto", accessMode = Schema.AccessMode.READ_ONLY)
    private String sedePrestamoNombre;

    @Schema(description = "ID de la sede donde se devolvio (null si sigue activo)")
    private Long sedeDevolucionId;

    @Schema(description = "Nombre de la sede donde se devolvio", accessMode = Schema.AccessMode.READ_ONLY)
    private String sedeDevolucionNombre;

    @Schema(description = "Fecha de inicio del prestamo")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha limite de devolucion")
    private LocalDate fechaLimite;

    @Schema(description = "Fecha real de devolucion (null si sigue activo)")
    private LocalDate fechaDevolucion;

    @Schema(description = "Estado del prestamo", example = "ACTIVO")
    private EstadoPrestamo estado;

    @Schema(description = "Indica si el prestamo sigue activo y ya paso la fecha limite", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean vencido;

    @Schema(description = "Indica si fue devuelto despues de la fecha limite")
    private Boolean devueltoConRetraso;

    @Schema(description = "Dias de retraso (acumulados si esta vencido, o finales si ya fue devuelto)")
    private Integer diasRetraso;

    @Schema(description = "Fecha de creacion del registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de ultima actualizacion", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
