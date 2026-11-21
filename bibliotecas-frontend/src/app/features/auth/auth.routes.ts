import { Routes } from '@angular/router';

export const AUTH_ROUTES: Routes = [
  {
    path: 'login',
    title: 'Iniciar sesión — Bibliotecas Global',
    loadComponent: () =>
      import('./pages/login-page/login-page').then((m) => m.LoginPage),
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
];
