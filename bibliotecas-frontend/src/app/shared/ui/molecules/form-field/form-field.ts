import { Component, input } from '@angular/core';

@Component({
  selector: 'app-form-field',
  template: `
    <div class="fila" [class.fila--error]="error()">
      <label class="fila__etiqueta" [attr.for]="para()">
        {{ etiqueta() }}
        @if (obligatorio()) { <span class="fila__req" aria-hidden="true">*</span> }
      </label>

      <ng-content />

      @if (error()) {
        <small class="fila__error" role="alert">{{ error() }}</small>
      } @else if (ayuda()) {
        <small class="fila__ayuda">{{ ayuda() }}</small>
      }
    </div>
  `,
  styles: `
    .fila {
      display: flex;
      flex-direction: column;
      gap: var(--space-1);

      &__etiqueta { font-size: 14px; font-weight: 500; color: var(--color-texto); }
      &__req { color: var(--color-error); }
      &__error { font-size: 12px; color: var(--color-error); }
      &__ayuda { font-size: 12px; color: var(--color-texto-suave); }
    }
  `,
})
export class FormField {
  readonly etiqueta = input.required<string>();
  readonly para = input.required<string>();
  readonly obligatorio = input(false);
  readonly ayuda = input<string>();
  readonly error = input<string | null>(null);
}
