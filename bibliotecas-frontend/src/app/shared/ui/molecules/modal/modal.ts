import { Component, input, model } from '@angular/core';

@Component({
  selector: 'app-modal',
  template: `
    @if (abierto()) {
      <div class="overlay" (click)="cerrar()" role="presentation">
        <div
          class="modal"
          role="dialog"
          aria-modal="true"
          [attr.aria-label]="titulo()"
          (click)="$event.stopPropagation()"
        >
          <header class="modal__header">
            <h3>{{ titulo() }}</h3>
            <button type="button" class="modal__cerrar" aria-label="Cerrar" (click)="cerrar()">×</button>
          </header>

          <div class="modal__cuerpo">
            <ng-content />
          </div>

          <footer class="modal__pie">
            <ng-content select="[modalPie]" />
          </footer>
        </div>
      </div>
    }
  `,
  styles: `
    .overlay {
      position: fixed;
      inset: 0;
      z-index: 120;
      background: rgba(32, 38, 37, 0.45);
      display: grid;
      place-items: center;
      padding: var(--space-4);
    }
    .modal {
      width: 100%;
      max-width: 500px;
      max-height: 90vh;
      display: flex;
      flex-direction: column;
      background: var(--color-superficie);
      border-radius: var(--radio-lg);
      box-shadow: var(--sombra-md);
      overflow: hidden;

      &__header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: var(--space-4) var(--space-6);
        border-bottom: 1px solid var(--color-borde);
      }
      &__cerrar {
        border: none;
        background: transparent;
        font-size: 22px;
        line-height: 1;
        color: var(--color-texto-suave);
        cursor: pointer;
      }
      &__cuerpo {
        padding: var(--space-6);
        font-size: 14px;
        overflow-y: auto;
      }
      &__pie {
        display: flex;
        justify-content: flex-end;
        gap: var(--space-2);
        padding: var(--space-4) var(--space-6);
        background: var(--color-fondo);
        border-top: 1px solid var(--color-borde);
      }
    }
  `,
})
export class Modal {
  readonly titulo = input.required<string>();
  readonly abierto = model(false);

  protected cerrar(): void {
    this.abierto.set(false);
  }
}
