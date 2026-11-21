import { Component, input } from '@angular/core';

@Component({
  selector: 'app-barra-carga',
  template: `
    @if (activa()) {
      <div class="barra" role="progressbar" aria-label="Cargando contenido"></div>
    }
  `,
  styles: `
    .barra {
      position: fixed;
      inset: 0 0 auto 0;
      z-index: 200;
      height: 3px;
      overflow: hidden;
      background: var(--color-primario-suave);

      &::after {
        content: '';
        position: absolute;
        inset: 0;
        width: 40%;
        background: var(--color-primario);
        animation: avanzar 1.1s ease-in-out infinite;
      }
    }
    @keyframes avanzar {
      from { transform: translateX(-100%); }
      to { transform: translateX(250%); }
    }
    @media (prefers-reduced-motion: reduce) {
      .barra::after { animation: none; width: 100%; }
    }
  `,
})
export class BarraCarga {
  readonly activa = input(false);
}
