import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../../core/services/auth';
import { CargaService } from '../../../../core/services/carga';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { BarraCarga } from '../../atoms/barra-carga/barra-carga';
import { NotificationCenter } from '../../organisms/notification-center/notification-center';

interface MenuItem {
  ruta: string;
  etiqueta: string;
}

@Component({
  selector: 'app-main-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, BarraCarga, NotificationCenter],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.scss',
})
export class MainLayout {
  protected readonly carga = inject(CargaService);
  protected readonly notificaciones = inject(NotificacionesService);
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly menuAbierto = signal(false);

  protected readonly enlaces: MenuItem[] = [
    { ruta: '/catalogo', etiqueta: 'Catálogo' },
    { ruta: '/sedes', etiqueta: 'Sedes' },
    { ruta: '/categorias', etiqueta: 'Categorías' },
    { ruta: '/libros/nuevo', etiqueta: 'Registrar Libro' },
    { ruta: '/ejemplares', etiqueta: 'Ejemplares' },
    { ruta: '/clientes', etiqueta: 'Clientes' },
    { ruta: '/prestamos', etiqueta: 'Préstamos' },
    { ruta: '/reservas', etiqueta: 'Reservas' },
    { ruta: '/traslados', etiqueta: 'Traslados' },
    { ruta: '/usuarios-admin', etiqueta: 'Usuarios Admin' },
    { ruta: '/reportes', etiqueta: 'Reportes' },
  ];

  protected cerrarSesion(): void {
    this.auth.cerrarSesion();
    this.router.navigate(['/auth/login']);
  }
}
