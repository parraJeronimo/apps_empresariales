import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../models/api.model';

export function mensajeDeError(error: HttpErrorResponse): string {
  if (error.status === 0) {
    return 'No se pudo conectar con el servidor. Verifica que la API esté disponible.';
  }

  const body = error.error as Partial<ApiError> | null;
  if (body?.detalles?.length) {
    return `${body.message ?? 'Datos inválidos'}: ${body.detalles.join('. ')}`;
  }
  if (body?.message) {
    return body.message;
  }

  switch (error.status) {
    case 401:
      return 'Tu sesión ha expirado o las credenciales son inválidas.';
    case 403:
      return 'No tienes permisos para realizar esta acción.';
    case 404:
      return 'El recurso solicitado no fue encontrado.';
    default:
      return `Error inesperado (${error.status}). Intenta de nuevo.`;
  }
}
