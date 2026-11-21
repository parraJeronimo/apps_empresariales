import { HttpEventType, HttpInterceptorFn } from '@angular/common/http';
import { finalize, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export const registroInterceptor: HttpInterceptorFn = (req, next) => {
  if (environment.production) return next(req);

  const inicio = performance.now();
  let estado = 'sin respuesta';

  return next(req).pipe(
    tap({
      next: (e) => {
        if (e.type === HttpEventType.Response) estado = String(e.status);
      },
      error: (err) => {
        estado = String(err.status ?? 'error');
      },
    }),
    finalize(() => {
      const ms = Math.round(performance.now() - inicio);
      console.info(`[HTTP] ${req.method} ${req.urlWithParams} -> ${estado} (${ms} ms)`);
    }),
  );
};
