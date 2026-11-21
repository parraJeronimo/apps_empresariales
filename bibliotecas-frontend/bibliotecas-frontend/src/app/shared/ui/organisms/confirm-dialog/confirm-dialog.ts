import { Component, input, model, output } from '@angular/core';
import { Button, VarianteBoton } from '../../atoms/button/button';
import { Modal } from '../../molecules/modal/modal';

@Component({
  selector: 'app-confirm-dialog',
  imports: [Modal, Button],
  template: `
    <app-modal [titulo]="titulo()" [(abierto)]="abierto">
      <p>{{ mensaje() }}</p>
      @if (error()) {
        <p class="mensaje-error" role="alert">{{ error() }}</p>
      }
      <ng-container modalPie>
        <app-button texto="Cancelar" variante="secundario" (presionado)="abierto.set(false)" />
        <app-button
          [texto]="textoConfirmar()"
          [variante]="varianteConfirmar()"
          [cargando]="cargando()"
          (presionado)="confirmar.emit()"
        />
      </ng-container>
    </app-modal>
  `,
})
export class ConfirmDialog {
  readonly titulo = input.required<string>();
  readonly mensaje = input.required<string>();
  readonly textoConfirmar = input('Confirmar');
  readonly varianteConfirmar = input<VarianteBoton>('peligro');
  readonly cargando = input(false);
  readonly error = input<string | null>(null);
  readonly abierto = model(false);
  readonly confirmar = output<void>();
}
