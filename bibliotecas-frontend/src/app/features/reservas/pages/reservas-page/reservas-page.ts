import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { form, FormField as SignalFormField, FormRoot, min, ReadonlyFieldState, required } from '@angular/forms/signals';
import { PageMeta } from '../../../../core/models/api.model';
import { Sede } from '../../../../core/models/gestion.model';
import { Reserva } from '../../../../core/models/operacion.model';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { ReservaService } from '../../../../core/services/reserva';
import { SedeService } from '../../../../core/services/sede';
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
  imports: [
    SignalFormField, FormRoot, PageHeader, Button,
    StatusBadge, Paginator, EmptyState, Modal, FormField,
  ],
  template: `
    <app-page-header titulo="Reservas" subtitulo="Gestión de solicitudes cuando no hay ejemplares disponibles">
      <app-button texto="+ Nueva reserva" (presionado)="abrirCrear()" />
    </app-page-header>

    @if (reservas().length === 0) {
      <app-empty-state titulo="No hay reservas registradas" descripcion="Crea una solicitud cuando un libro no tenga ejemplares disponibles en la sede.">
        <app-button texto="Registrar reserva" (presionado)="abrirCrear()" />
      </app-empty-state>
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

    <!-- Modal Nueva Reserva (HU-14) -->
    <app-modal titulo="Nueva reserva" [(abierto)]="modalCrear">
      <form id="reservaForm" [formRoot]="formulario" class="panel-form panel-form--modal">
        @if (errorCrear()) {
          <p class="mensaje-error" role="alert">{{ errorCrear() }}</p>
        }
        <app-form-field etiqueta="ID Cliente" para="res-cliente" [obligatorio]="true" [error]="errorDe(formulario.clienteId())">
          <input id="res-cliente" type="number" class="campo" [formField]="formulario.clienteId" />
        </app-form-field>
        <app-form-field etiqueta="ID Libro" para="res-libro" [obligatorio]="true" [error]="errorDe(formulario.libroId())">
          <input id="res-libro" type="number" class="campo" [formField]="formulario.libroId" />
        </app-form-field>
        <app-form-field etiqueta="Sede de recogida" para="res-sede" [obligatorio]="true" [error]="errorDe(formulario.sedeId())">
          <select id="res-sede" class="campo" [formField]="formulario.sedeId">
            <option value="" disabled>Seleccione una sede...</option>
            @for (s of sedes(); track s.id) {
              <option [value]="s.id">{{ s.nombre }}</option>
            }
          </select>
        </app-form-field>
      </form>
      <ng-container modalPie>
        <app-button texto="Cancelar" variante="secundario" (presionado)="modalCrear.set(false)" />
        <app-button tipo="submit" formId="reservaForm" texto="Crear reserva" [cargando]="guardando()" />
      </ng-container>
    </app-modal>

    <!-- Modal Rechazar Reserva (HU-15) -->
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
  private readonly sedeService = inject(SedeService);
  private readonly notificaciones = inject(NotificacionesService);

  protected readonly sedes = toSignal(this.sedeService.listarTodas(), { initialValue: [] as Sede[] });
  protected readonly reservas = signal<Reserva[]>([]);
  protected readonly pagina = signal(0);
  protected readonly meta = signal<PageMeta>({ totalElements: 0, totalPages: 1, number: 0, size: 10 });

  // Estado Modal Nueva Reserva
  protected readonly modalCrear = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorCrear = signal<string | null>(null);
  protected readonly modelo = signal({ clienteId: 1, libroId: 1, sedeId: '' });

  protected readonly formulario = form(
    this.modelo,
    (p) => {
      min(p.clienteId, 1, { message: 'El ID del cliente es obligatorio' });
      min(p.libroId, 1, { message: 'El ID del libro es obligatorio' });
      required(p.sedeId, { message: 'La sede es obligatoria' });
    },
    { submission: { action: async () => this.guardar() } },
  );

  // Estado Modal Rechazo
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

  protected abrirCrear(): void {
    this.errorCrear.set(null);
    this.modelo.set({ clienteId: 1, libroId: 1, sedeId: '' });
    this.modalCrear.set(true);
  }

  protected errorDe(campo: ReadonlyFieldState<unknown>): string | null {
    return campo.touched() ? (campo.errors()[0]?.message ?? null) : null;
  }

  private guardar(): void {
    this.guardando.set(true);
    this.errorCrear.set(null);
    const v = this.modelo();

    this.reservaService
      .crear({
        clienteId: Number(v.clienteId),
        libroId: Number(v.libroId),
        sedeId: Number(v.sedeId),
      })
      .subscribe({
        next: (creada) => {
          this.guardando.set(false);
          this.modalCrear.set(false);
          this.notificaciones.mostrar(`Reserva #${creada.id} creada en estado ${creada.estado}.`, 'exito');
          this.cargar();
        },
        error: (e: HttpErrorResponse) => {
          this.guardando.set(false);
          this.errorCrear.set(mensajeDeError(e));
        },
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