package com.taller.bibliotecas.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "traslados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrasladoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejemplar_id", nullable = false)
    private EjemplarEntity ejemplarEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_origen_id", nullable = false)
    private SedeEntity sedeOrigenEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_destino_id", nullable = false)
    private SedeEntity sedeDestinoEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTraslado estado;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.fechaSolicitud == null) {
            this.fechaSolicitud = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoTraslado.EN_TRANSITO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
