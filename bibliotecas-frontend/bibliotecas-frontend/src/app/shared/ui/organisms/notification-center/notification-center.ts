import { Component, input, output } from '@angular/core';
import { Notificacion } from '../../../../core/services/notificaciones';
import { Toast } from '../../molecules/toast/toast';

@Component({
  selector: 'app-notification-center',
  imports: [Toast],
  template: `
    <div class="centro" aria-live="polite">
      @for (n of notificaciones(); track n.id) {
        <app-toast [texto]="n.texto" [tipo]="n.tipo" (cerrar)="cerrar.emit(n.id)" />
      }
    </div>
  `,
  styles: `
    .centro {
      position: fixed;
      right: var(--space-4);
      bottom: var(--space-4);
      z-index: 150;
      display: flex;
      flex-direction: column;
      gap: var(--space-2);
    }
  `,
})
export class NotificationCenter {
  readonly notificaciones = input.required<Notificacion[]>();
  readonly cerrar = output<number>();
}
