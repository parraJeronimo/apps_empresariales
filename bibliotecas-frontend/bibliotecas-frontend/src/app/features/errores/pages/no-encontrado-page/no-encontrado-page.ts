import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Button } from '../../../../shared/ui/atoms/button/button';

@Component({
  selector: 'app-no-encontrado-page',
  imports: [RouterLink, Button],
  template: `
    <section class="no-encontrado">
      <p class="no-encontrado__codigo">404</p>
      <h1>Página no encontrada</h1>
      <p>La dirección <code>{{ urlSolicitada }}</code> no existe en la aplicación.</p>
      <a routerLink="/catalogo"><app-button texto="Ir al catálogo" /></a>
    </section>
  `,
  styles: `
    .no-encontrado {
      max-width: 480px;
      margin: var(--space-12) auto;
      text-align: center;
      background: var(--color-superficie);
      padding: var(--space-8);
      border-radius: var(--radio-lg);
      border: 1px solid var(--color-borde);
      &__codigo { font-size: 48px; font-family: var(--font-display); color: var(--color-primario); margin: 0; }
      p { color: var(--color-texto-suave); margin: var(--space-3) 0 var(--space-6); }
    }
  `,
})
export class NoEncontradoPage {
  protected readonly urlSolicitada = inject(Router).url;
}
