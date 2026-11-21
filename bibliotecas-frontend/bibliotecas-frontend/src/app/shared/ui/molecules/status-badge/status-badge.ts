import { Component, computed, input } from '@angular/core';
import { Badge, VarianteBadge } from '../../atoms/badge/badge';

const MAPA_ESTADOS: Record<string, { texto: string; variante: VarianteBadge }> = {
  DISPONIBLE: { texto: 'Disponible', variante: 'exito' },
  RECIBIDO: { texto: 'Recibido', variante: 'exito' },
  APROBADA: { texto: 'Aprobada', variante: 'exito' },
  FINALIZADO: { texto: 'Finalizado', variante: 'exito' },
  ACTIVA: { texto: 'Activa', variante: 'exito' },
  PENDIENTE: { texto: 'Pendiente', variante: 'advertencia' },
  EN_TRANSITO: { texto: 'En tránsito', variante: 'advertencia' },
  EN_TRASLADO: { texto: 'En traslado', variante: 'advertencia' },
  VENCIDO: { texto: 'Vencido', variante: 'peligro' },
  RECHAZADA: { texto: 'Rechazada', variante: 'peligro' },
  PRESTADO: { texto: 'Prestado', variante: 'info' },
  ACTIVO: { texto: 'Activo', variante: 'info' },
  CANCELADA: { texto: 'Cancelada', variante: 'neutro' },
  CANCELADO: { texto: 'Cancelado', variante: 'neutro' },
  INACTIVA: { texto: 'Inactiva', variante: 'neutro' },
};

@Component({
  selector: 'app-status-badge',
  imports: [Badge],
  template: `<app-badge [texto]="config().texto" [variante]="config().variante" />`,
})
export class StatusBadge {
  readonly estado = input.required<string>();

  protected readonly config = computed(
    () => MAPA_ESTADOS[this.estado()] ?? { texto: this.estado(), variante: 'neutro' as const },
  );
}
