package com.taller.bibliotecas.businessLayer.dto;

import com.taller.bibliotecas.persistenceLayer.entity.EstadoEjemplar;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informacion de un ejemplar fisico, su sede actual y su estado")
public class EjemplarDTO {

    @Schema(description = "ID unico del ejemplar", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Codigo unico del ejemplar")
    private String codigo;

    @Schema(description = "Estado actual del ejemplar", example = "DISPONIBLE")
    private EstadoEjemplar estado;

    @Schema(description = "ID del libro")
    private Long libroId;

    @Schema(description = "Titulo del libro", accessMode = Schema.AccessMode.READ_ONLY)
    private String libroTitulo;

    @Schema(description = "ISBN del libro", accessMode = Schema.AccessMode.READ_ONLY)
    private String libroIsbn;

    @Schema(description = "ID de la sede a la que pertenece el ejemplar")
    private Long sedePropietariaId;

    @Schema(description = "Nombre de la sede propietaria", accessMode = Schema.AccessMode.READ_ONLY)
    private String sedePropietariaNombre;

    @Schema(description = "ID de la sede donde se encuentra actualmente")
    private Long sedeActualId;

    @Schema(description = "Nombre de la sede donde se encuentra actualmente", accessMode = Schema.AccessMode.READ_ONLY)
    private String sedeActualNombre;

    @Schema(description = "Fecha de creacion del registro", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de ultima actualizacion", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
