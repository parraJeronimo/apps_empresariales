import { Component, input } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  template: `
    <div class="vacio">
      <h4>{{ titulo() }}</h4>
      @if (descripcion()) {
        <p>{{ descripcion() }}</p>
      }
      <ng-content />
    </div>
  `,
  styles: `
    .vacio {
      text-align: center;
      padding: var(--space-12) var(--space-4);
      background: var(--color-superficie);
      border: 1px dashed var(--color-borde);
      border-radius: var(--radio-lg);
      p { color: var(--color-texto-suave); font-size: 14px; margin: var(--space-2) 0 var(--space-4); }
    }
  `,
})
export class EmptyState {
  readonly titulo = input('No hay resultados');
  readonly descripcion = input<string>();
}
