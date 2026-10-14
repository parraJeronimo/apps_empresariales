package com.taller.bibliotecas.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Historial de movimientos y ubicaciones de un ejemplar (RF-20). */
@Entity
@Table(name = "movimientos_ejemplar")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoEjemplarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejemplar_id", nullable = false)
    private EjemplarEntity ejemplarEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMovimiento tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_origen_id")
    private SedeEntity sedeOrigenEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_destino_id")
    private SedeEntity sedeDestinoEntity;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(length = 500)
    private String detalle;

    @PrePersist
    protected void onCreate() {
        if (this.fechaMovimiento == null) {
            this.fechaMovimiento = LocalDateTime.now();
        }
    }
}
