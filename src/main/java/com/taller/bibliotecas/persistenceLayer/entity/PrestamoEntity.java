package com.taller.bibliotecas.persistenceLayer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejemplar_id", nullable = false)
    private EjemplarEntity ejemplarEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity clienteEntity;

    /** Sede donde se realizo el prestamo */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_prestamo_id", nullable = false)
    private SedeEntity sedePrestamoEntity;

    /** Sede donde se realizo la devolucion (null mientras el prestamo siga activo) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_devolucion_id")
    private SedeEntity sedeDevolucionEntity;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Column(name = "devuelto_con_retraso")
    private Boolean devueltoConRetraso;

    @Column(name = "dias_retraso")
    private Integer diasRetraso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrestamo estado;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoPrestamo.ACTIVO;
        }
        if (this.devueltoConRetraso == null) {
            this.devueltoConRetraso = Boolean.FALSE;
        }
        if (this.diasRetraso == null) {
            this.diasRetraso = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
