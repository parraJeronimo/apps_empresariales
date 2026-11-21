import { Component, input } from '@angular/core';

export type VarianteBadge = 'exito' | 'advertencia' | 'peligro' | 'info' | 'neutro';

@Component({
  selector: 'app-badge',
  template: `<span class="badge" [class]="'badge badge--' + variante()">{{ texto() }}</span>`,
  styles: `
    .badge {
      display: inline-flex;
      align-items: center;
      padding: 2px 10px;
      border-radius: 999px;
      font-size: 12px;
      font-weight: 600;
      white-space: nowrap;

      &--exito { background: var(--color-exito-suave); color: var(--color-exito); }
      &--advertencia { background: var(--color-advertencia-suave); color: #b57810; }
      &--peligro { background: var(--color-error-suave); color: var(--color-error); }
      &--info { background: var(--color-info-suave); color: var(--color-info); }
      &--neutro { background: var(--color-fondo); color: var(--color-texto-suave); }
    }
  `,
})
export class Badge {
  readonly texto = input.required<string>();
  readonly variante = input<VarianteBadge>('neutro');
}
