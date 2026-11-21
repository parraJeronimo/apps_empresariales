import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService, RolUsuario } from '../services/auth';
import { NotificacionesService } from '../services/notificaciones';

export function roleGuard(rolesPermitidos: RolUsuario[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const notificaciones = inject(NotificacionesService);

    if (auth.tieneRol(rolesPermitidos)) {
      return true;
    }

    notificaciones.mostrar('No tienes permisos para acceder a esta sección.', 'advertencia');
    return router.createUrlTree(['/catalogo']);
  };
}
