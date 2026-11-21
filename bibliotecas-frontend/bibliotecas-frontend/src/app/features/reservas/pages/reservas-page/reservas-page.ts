import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { PageMeta } from '../../../../core/models/api.model';
import { Reserva } from '../../../../core/models/operacion.model';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { ReservaService } from '../../../../core/services/reserva';
import { mensajeDeError } from '../../../../core/utils/errores';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { EmptyState } from '../../../../shared/ui/molecules/empty-state/empty-state';
import { FormField } from '../../../../shared/ui/molecules/form-field/form-field';
import { Modal } from '../../../../shared/ui/molecules/modal/modal';
import { Paginator } from '../../../../shared/ui/molecules/paginator/paginator';
import { StatusBadge } from '../../../../shared/ui/molecules/status-badge/status-badge';
import { PageHeader } from '../../../../shared/ui/organisms/page-header/page-header';

@Component({
  selector: 'app-reservas-page',
  imports: [PageHeader, Button, StatusBadge, Paginator, EmptyState, Modal, FormField],
  template: `
    <app-page-header titulo="Reservas" subtitulo="Gestión de solicitudes cuando no hay ejemplares disponibles" />

    @if (reservas().length === 0) {
      <app-empty-state titulo="No hay reservas registradas" />
    } @else {
      <div class="tabla-contenedor">
        <table class="tabla">
          <thead>
            <tr>
              <th>ID</th>
              <th>Cliente ID</th>
              <th>Libro ID</th>
              <th>Sede ID</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (r of reservas(); track r.id) {
              <tr>
                <td>#{{ r.id }}</td>
                <td>#{{ r.clienteId }}</td>
                <td>#{{ r.libroId }}</td>
                <td>#{{ r.sedeId }}</td>
                <td><app-status-badge [estado]="r.estado" /></td>
                <td class="tabla__acciones">
                  @if (r.estado === 'PENDIENTE') {
                    <app-button texto="Aprobar" (presionado)="aprobar(r.id)" />
                    <app-button texto="Rechazar" variante="peligro" (presionado)="abrirRechazo(r)" />
                  }
                  @if (r.estado === 'PENDIENTE' || r.estado === 'APROBADA') {
                    <app-button texto="Cancelar" variante="secundario" (presionado)="cancelar(r.id)" />
                  }
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>

      <app-paginator
        [(pagina)]="pagina"
        [totalPaginas]="meta().totalPages"
        [totalElementos]="meta().totalElements"
        etiqueta="reservas"
      />
    }

    <app-modal [titulo]="'Rechazar reserva #' + (reservaRechazo()?.id ?? '')" [(abierto)]="modalRechazo">
      <app-form-field etiqueta="Motivo del rechazo" para="r-motivo" [obligatorio]="true" [error]="errorRechazo()">
        <textarea
          id="r-motivo"
          rows="3"
          class="campo"
          [value]="motivoRechazo()"
          (input)="motivoRechazo.set($any($event.target).value)"
        ></textarea>
      </app-form-field>
      <ng-container modalPie>
        <app-button texto="Cancelar" variante="secundario" (presionado)="modalRechazo.set(false)" />
        <app-button texto="Confirmar rechazo" variante="peligro" (presionado)="confirmarRechazo()" />
      </ng-container>
    </app-modal>
  `,
})
export class ReservasPage {
  private readonly reservaService = inject(ReservaService);
  private readonly notificaciones = inject(NotificacionesService);

  protected readonly reservas = signal<Reserva[]>([]);
  protected readonly pagina = signal(0);
  protected readonly meta = signal<PageMeta>({ totalElements: 0, totalPages: 1, number: 0, size: 10 });

  protected readonly modalRechazo = signal(false);
  protected readonly reservaRechazo = signal<Reserva | null>(null);
  protected readonly motivoRechazo = signal('');
  protected readonly errorRechazo = signal<string | null>(null);

  constructor() {
    effect(() => this.cargar(this.pagina()));
  }

  private cargar(page = this.pagina()): void {
    this.reservaService.listarPaginado(page, 10).subscribe((res) => {
      this.reservas.set(res.items);
      this.meta.set(res.page);
    });
  }

  protected aprobar(id: number): void {
    this.reservaService.aprobar(id).subscribe({
      next: () => {
        this.notificaciones.mostrar(`Reserva #${id} aprobada.`, 'exito');
        this.cargar();
      },
      error: (e: HttpErrorResponse) => this.notificaciones.mostrar(mensajeDeError(e), 'error'),
    });
  }

  protected cancelar(id: number): void {
    this.reservaService.cancelar(id).subscribe({
      next: () => {
        this.notificaciones.mostrar(`Reserva #${id} cancelada.`, 'info');
        this.cargar();
      },
      error: (e: HttpErrorResponse) => this.notificaciones.mostrar(mensajeDeError(e), 'error'),
    });
  }

  protected abrirRechazo(r: Reserva): void {
    this.reservaRechazo.set(r);
    this.motivoRechazo.set('');
    this.errorRechazo.set(null);
    this.modalRechazo.set(true);
  }

  protected confirmarRechazo(): void {
    const r = this.reservaRechazo();
    const motivo = this.motivoRechazo().trim();
    if (!r) return;
    if (!motivo) {
      this.errorRechazo.set('El motivo del rechazo es obligatorio');
      return;
    }

    this.reservaService.rechazar(r.id, motivo).subscribe({
      next: () => {
        this.modalRechazo.set(false);
        this.notificaciones.mostrar(`Reserva #${r.id} rechazada.`, 'info');
        this.cargar();
      },
      error: (e: HttpErrorResponse) => this.errorRechazo.set(mensajeDeError(e)),
    });
  }
}
