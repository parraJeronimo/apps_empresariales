import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-auth-template',
  imports: [RouterOutlet],
  template: `
    <div class="auth-page">
      <div class="auth-card">
        <header class="auth-card__header">
          <div class="auth-card__logo">BG</div>
          <h1>Bibliotecas Global</h1>
          <p>Sistema de Gestión de Múltiples Bibliotecas</p>
        </header>
        <router-outlet />
      </div>
    </div>
  `,
  styles: `
    .auth-page {
      min-height: 100vh;
      display: grid;
      place-items: center;
      padding: var(--space-4);
      background: var(--color-neutro);
    }
    .auth-card {
      width: 100%;
      max-width: 420px;
      background: var(--color-superficie);
      padding: var(--space-8);
      border-radius: var(--radio-lg);
      box-shadow: var(--sombra-md);

      &__header {
        text-align: center;
        margin-bottom: var(--space-6);
        p { margin: var(--space-1) 0 0; color: var(--color-texto-suave); font-size: 14px; }
      }
      &__logo {
        width: 48px;
        height: 48px;
        margin: 0 auto var(--space-3);
        border-radius: var(--radio);
        background: var(--color-primario);
        color: #fff;
        display: grid;
        place-items: center;
        font-weight: 700;
        font-size: 18px;
      }
    }
  `,
})
export class AuthTemplate {}
