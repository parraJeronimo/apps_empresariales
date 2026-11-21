import { Component, input, output } from '@angular/core';
import { TipoNotificacion } from '../../../../core/services/notificaciones';

@Component({
  selector: 'app-toast',
  template: `
    <div
      class="toast"
      [class]="'toast toast--' + tipo()"
      [attr.role]="tipo() === 'error' ? 'alert' : 'status'"
    >
      <span class="toast__texto">{{ texto() }}</span>
      <button type="button" class="toast__cerrar" aria-label="Cerrar notificación" (click)="cerrar.emit()">
        ×
      </button>
    </div>
  `,
  styles: `
    .toast {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: var(--space-3);
      min-width: 280px;
      max-width: 420px;
      padding: var(--space-3) var(--space-4);
      border-radius: var(--radio);
      background: var(--color-superficie);
      box-shadow: var(--sombra-md);
      border-left: 4px solid var(--color-info);
      font-size: 14px;

      &--exito { border-left-color: var(--color-exito); }
      &--error { border-left-color: var(--color-error); }
      &--advertencia { border-left-color: var(--color-advertencia); }
      &--info { border-left-color: var(--color-info); }

      &__cerrar {
        border: none;
        background: transparent;
        color: var(--color-texto-suave);
        font-size: 18px;
        cursor: pointer;
        padding: 0 var(--space-1);
        line-height: 1;
      }
    }
  `,
})
export class Toast {
  readonly texto = input.required<string>();
  readonly tipo = input<TipoNotificacion>('info');
  readonly cerrar = output<void>();
}
