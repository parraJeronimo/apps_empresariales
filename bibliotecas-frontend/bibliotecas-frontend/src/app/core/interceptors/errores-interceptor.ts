import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificacionesService } from '../services/notificaciones';
import { SIN_NOTIFICACION_ERROR } from './contexto';

export const erroresInterceptor: HttpInterceptorFn = (req, next) => {
  const notificaciones = inject(NotificacionesService);

  return next(req).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && !req.context.get(SIN_NOTIFICACION_ERROR)) {
        if (error.status === 0) {
          notificaciones.mostrar('Sin conexión con la API. Verifica tu red o el servidor.', 'error');
        } else if (error.status >= 500) {
          notificaciones.mostrar(`Error interno del servidor (${error.status}). Intenta más tarde.`, 'error');
        } else if (error.status === 403) {
          notificaciones.mostrar('No tienes permisos para realizar esta acción.', 'advertencia');
        }
      }
      return throwError(() => error);
    }),
  );
};
