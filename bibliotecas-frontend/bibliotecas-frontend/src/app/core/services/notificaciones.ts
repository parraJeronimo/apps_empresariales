import { Service, signal } from '@angular/core';

export type TipoNotificacion = 'exito' | 'error' | 'advertencia' | 'info';

export interface Notificacion {
  id: number;
  tipo: TipoNotificacion;
  texto: string;
}

@Service()
export class NotificacionesService {
  private siguienteId = 1;
  private readonly lista = signal<Notificacion[]>([]);
  readonly notificaciones = this.lista.asReadonly();

  mostrar(texto: string, tipo: TipoNotificacion = 'info', duracionMs = 5000): void {
    if (this.lista().some((n) => n.texto === texto)) return;

    const id = this.siguienteId++;
    this.lista.update((actual) => [...actual, { id, tipo, texto }]);

    if (duracionMs > 0) {
      setTimeout(() => this.cerrar(id), duracionMs);
    }
  }

  cerrar(id: number): void {
    this.lista.update((actual) => actual.filter((n) => n.id !== id));
  }
}
