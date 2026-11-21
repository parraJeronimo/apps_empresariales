import { Component, input } from '@angular/core';

@Component({
  selector: 'app-page-header',
  template: `
    <header class="page-header">
      <div>
        <h1>{{ titulo() }}</h1>
        @if (subtitulo()) {
          <p class="page-header__sub">{{ subtitulo() }}</p>
        }
      </div>
      <div class="page-header__acciones">
        <ng-content />
      </div>
    </header>
  `,
  styles: `
    .page-header {
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      justify-content: space-between;
      gap: var(--space-4);
      margin-bottom: var(--space-6);

      &__sub {
        margin: var(--space-1) 0 0;
        color: var(--color-texto-suave);
        font-size: 14px;
      }
      &__acciones {
        display: flex;
        gap: var(--space-2);
      }
    }
  `,
})
export class PageHeader {
  readonly titulo = input.required<string>();
  readonly subtitulo = input<string>();
}
