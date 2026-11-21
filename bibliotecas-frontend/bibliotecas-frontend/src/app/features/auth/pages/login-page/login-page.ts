import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { email, form, FormField, FormRoot, ReadonlyFieldState, required } from '@angular/forms/signals';
import { AuthService } from '../../../../core/services/auth';
import { mensajeDeError } from '../../../../core/utils/errores';

@Component({
  selector: 'app-login-page',
  imports: [FormField, FormRoot, RouterLink],
  templateUrl: './login-page.html',
  styleUrl: './login-page.scss',
})
export class LoginPage {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly redirect = input<string>();

  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal<string | null>(null);

  protected readonly modelo = signal({
    email: '',
    password: '',
  });

  protected readonly formulario = form(
    this.modelo,
    (p) => {
      required(p.email, { message: 'El correo es obligatorio' });
      email(p.email, { message: 'Ingresa un correo electrónico válido' });
      required(p.password, { message: 'La contraseña es obligatoria' });
    },
    {
      submission: {
        action: async () => this.iniciarSesion(),
      },
    },
  );

  protected errorDe(campo: ReadonlyFieldState<unknown>): string | null {
    return campo.touched() ? (campo.errors()[0]?.message ?? null) : null;
  }

  private iniciarSesion(): void {
    this.cargando.set(true);
    this.mensajeError.set(null);

    this.auth.login(this.modelo()).subscribe({
      next: () => {
        this.cargando.set(false);
        const destino = this.redirect() || '/catalogo';
        this.router.navigateByUrl(destino);
      },
      error: (err: HttpErrorResponse) => {
        this.cargando.set(false);
        this.mensajeError.set(
          err.status === 401
            ? 'Correo o contraseña incorrectos.'
            : mensajeDeError(err),
        );
      },
    });
  }
}
