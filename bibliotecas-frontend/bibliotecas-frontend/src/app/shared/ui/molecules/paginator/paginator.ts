import { Component, computed, input, model } from '@angular/core';
import { Button } from '../../atoms/button/button';

@Component({
  selector: 'app-paginator',
  imports: [Button],
  template: `
    @if (totalPaginas() > 1) {
      <nav class="paginador" aria-label="Paginación">
        <app-button
          texto="Anterior"
          variante="secundario"
          [deshabilitado]="esPrimera()"
          (presionado)="anterior()"
        />
        <span class="paginador__texto">
          Página <strong>{{ pagina() + 1 }}</strong> de {{ totalPaginas() }} ·
          {{ totalElementos() }} {{ etiqueta() }}
        </span>
        <app-button
          texto="Siguiente"
          variante="secundario"
          [deshabilitado]="esUltima()"
          (presionado)="siguiente()"
        />
      </nav>
    }
  `,
  styles: `
    .paginador {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: center;
      gap: var(--space-4);
      padding-top: var(--space-4);

      &__texto {
        font-size: 14px;
        color: var(--color-texto-suave);
        font-variant-numeric: tabular-nums;
      }
    }
  `,
})
export class Paginator {
  readonly pagina = model.required<number>();
  readonly totalPaginas = input.required<number>();
  readonly totalElementos = input.required<number>();
  readonly etiqueta = input('registros');

  protected readonly esPrimera = computed(() => this.pagina() <= 0);
  protected readonly esUltima = computed(() => this.pagina() >= this.totalPaginas() - 1);

  protected anterior(): void {
    this.pagina.update((p) => Math.max(0, p - 1));
  }

  protected siguiente(): void {
    this.pagina.update((p) => Math.min(this.totalPaginas() - 1, p + 1));
  }
}
