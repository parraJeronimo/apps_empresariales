import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { AuthTemplate } from './shared/ui/templates/auth-template/auth-template';
import { MainLayout } from './shared/ui/templates/main-layout/main-layout';

export const routes: Routes = [
  {
    path: 'auth',
    component: AuthTemplate,
    canActivate: [guestGuard],
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.AUTH_ROUTES),
  },
  {
    path: '',
    component: MainLayout,
    children: [
      { path: '', redirectTo: 'catalogo', pathMatch: 'full' },
      {
        path: 'catalogo',
        title: 'Catálogo — Bibliotecas Global',
        loadComponent: () =>
          import('./features/libros/pages/catalogo-page/catalogo-page').then((m) => m.CatalogoPage),
      },
      {
        path: 'sedes',
        title: 'Sedes — Bibliotecas Global',
        canActivate: [authGuard, roleGuard(['ADMINISTRADOR_GENERAL'])],
        loadComponent: () =>
          import('./features/sedes/pages/sedes-page/sedes-page').then((m) => m.SedesPage),
      },
      {
        path: 'categorias',
        title: 'Categorías — Bibliotecas Global',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/categorias/pages/categorias-page/categorias-page').then(
            (m) => m.CategoriasPage,
          ),
      },
      {
        path: 'libros',
        children: [
          { path: '', redirectTo: '/catalogo', pathMatch: 'full' },
          {
            path: 'nuevo',
            title: 'Nuevo libro — Bibliotecas Global',
            canActivate: [authGuard],
            loadComponent: () =>
              import('./features/libros/pages/libro-form-page/libro-form-page').then(
                (m) => m.LibroFormPage,
              ),
          },
          {
            path: ':id',
            title: 'Detalle del libro — Bibliotecas Global',
            loadComponent: () =>
              import('./features/libros/pages/libro-detalle-page/libro-detalle-page').then(
                (m) => m.LibroDetallePage,
              ),
          },
          {
            path: ':id/editar',
            title: 'Editar libro — Bibliotecas Global',
            canActivate: [authGuard],
            loadComponent: () =>
              import('./features/libros/pages/libro-form-page/libro-form-page').then(
                (m) => m.LibroFormPage,
              ),
          },
        ],
      },
      {
        path: 'ejemplares',
        title: 'Ejemplares — Bibliotecas Global',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/ejemplares/pages/ejemplares-page/ejemplares-page').then(
            (m) => m.EjemplaresPage,
          ),
      },
      {
        path: 'clientes',
        title: 'Clientes — Bibliotecas Global',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/clientes/pages/clientes-page/clientes-page').then(
            (m) => m.ClientesPage,
          ),
      },
      {
        path: 'prestamos',
        title: 'Préstamos — Bibliotecas Global',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/prestamos/pages/prestamos-page/prestamos-page').then(
            (m) => m.PrestamosPage,
          ),
      },
      {
        path: 'reservas',
        title: 'Reservas — Bibliotecas Global',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/reservas/pages/reservas-page/reservas-page').then(
            (m) => m.ReservasPage,
          ),
      },
      {
        path: 'traslados',
        title: 'Traslados — Bibliotecas Global',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/traslados/pages/traslados-page/traslados-page').then(
            (m) => m.TrasladosPage,
          ),
      },
      {
        path: 'usuarios-admin',
        title: 'Usuarios Administrativos — Bibliotecas Global',
        canActivate: [authGuard, roleGuard(['ADMINISTRADOR_GENERAL'])],
        loadComponent: () =>
          import('./features/usuarios-admin/pages/usuarios-admin-page/usuarios-admin-page').then(
            (m) => m.UsuariosAdminPage,
          ),
      },
      {
        path: 'reportes',
        title: 'Reportes — Bibliotecas Global',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/reportes/pages/reportes-page/reportes-page').then(
            (m) => m.ReportesPage,
          ),
      },
      {
        path: '**',
        title: 'Página no encontrada — Bibliotecas Global',
        loadComponent: () =>
          import('./features/errores/pages/no-encontrado-page/no-encontrado-page').then(
            (m) => m.NoEncontradoPage,
          ),
      },
    ],
  },
];
