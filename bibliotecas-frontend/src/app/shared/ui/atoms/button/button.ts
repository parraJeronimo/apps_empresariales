import { Component, input, output } from '@angular/core';

export type VarianteBoton = 'primario' | 'secundario' | 'peligro';
export type TipoBoton = 'button' | 'submit';

@Component({
  selector: 'app-button',
  template: `
    <button
      [type]="tipo()"
      [attr.form]="formId()"
      class="btn"
      [class]="'btn btn--' + variante()"
      [disabled]="deshabilitado() || cargando()"
      (click)="presionado.emit()"
    >
      {{ cargando() ? textoCargando() : texto() }}
    </button>
  `,
  styles: `
    :host { display: inline-block; }
    .btn {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      gap: var(--space-2);
      padding: var(--space-2) var(--space-4);
      border-radius: var(--radio);
      font: inherit;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      border: 1px solid transparent;
      transition: background 0.15s ease, opacity 0.15s ease;

      &--primario {
        background: var(--color-primario);
        color: #fff;
        &:hover:not(:disabled) { background: var(--color-primario-hover); }
      }
      &--secundario {
        background: transparent;
        border-color: var(--color-borde);
        color: var(--color-texto);
        &:hover:not(:disabled) { background: var(--color-fondo); }
      }
      &--peligro {
        background: var(--color-error);
        color: #fff;
        &:hover:not(:disabled) { opacity: 0.9; }
      }
      &:disabled {
        opacity: 0.55;
        cursor: not-allowed;
      }
    }
  `,
})
export class Button {
  readonly texto = input.required<string>();
  readonly variante = input<VarianteBoton>('primario');
  readonly tipo = input<TipoBoton>('button');
  readonly formId = input<string | null>(null);
  readonly deshabilitado = input(false);
  readonly cargando = input(false);
  readonly textoCargando = input('Procesando...');
  readonly presionado = output<void>();
}
