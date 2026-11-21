import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { PageMeta } from '../../../../core/models/api.model';
import { Sede } from '../../../../core/models/gestion.model';
import { Traslado } from '../../../../core/models/operacion.model';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { SedeService } from '../../../../core/services/sede';
import { TrasladoService } from '../../../../core/services/traslado';
import { mensajeDeError } from '../../../../core/utils/errores';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { EmptyState } from '../../../../shared/ui/molecules/empty-state/empty-state';
import { FormField } from '../../../../shared/ui/molecules/form-field/form-field';
import { Modal } from '../../../../shared/ui/molecules/modal/modal';
import { Paginator } from '../../../../shared/ui/molecules/paginator/paginator';
import { StatusBadge } from '../../../../shared/ui/molecules/status-badge/status-badge';
import { PageHeader } from '../../../../shared/ui/organisms/page-header/page-header';

@Component({
  selector: 'app-traslados-page',
  imports: [DatePipe, PageHeader, Button, StatusBadge, Paginator, EmptyState, Modal, FormField],
  template: `
    <app-page-header titulo="Traslados" subtitulo="Movimientos logísticos de ejemplares entre sedes">
      <app-button texto="+ Nuevo traslado" (presionado)="abrirCrear()" />
    </app-page-header>

    @if (traslados().length === 0) {
      <app-empty-state titulo="No hay traslados registrados" />
    } @else {
      <div class="tabla-contenedor">
        <table class="tabla">
          <thead>
            <tr>
              <th>ID</th>
              <th>Ejemplar ID</th>
              <th>Sede Destino</th>
              <th>Estado</th>
              <th>Recepción</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (t of traslados(); track t.id) {
              <tr>
                <td>#{{ t.id }}</td>
                <td>#{{ t.ejemplarId }}</td>
                <td>Sede #{{ t.sedeDestinoId }}</td>
                <td><app-status-badge [estado]="t.estado" /></td>
                <td>{{ t.fechaRecepcion ? (t.fechaRecepcion | date: 'short') : '—' }}</td>
                <td class="tabla__acciones">
                  @if (t.estado === 'EN_TRANSITO') {
                    <app-button texto="Confirmar recepción" (presionado)="recibir(t.id)" />
                    <app-button texto="Cancelar" variante="peligro" (presionado)="cancelar(t.id)" />
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
        etiqueta="traslados"
      />
    }

    <app-modal titulo="Registrar traslado" [(abierto)]="modalCrear">
      @if (errorForm()) {
        <p class="mensaje-error" role="alert">{{ errorForm() }}</p>
      }
      <div class="panel-form panel-form--modal">
        <app-form-field etiqueta="ID Ejemplar" para="t-ej" [obligatorio]="true">
          <input id="t-ej" type="number" class="campo" [value]="ejemplarId()" (input)="ejemplarId.set(+$any($event.target).value)" />
        </app-form-field>
        <app-form-field etiqueta="Sede destino" para="t-sede" [obligatorio]="true">
          <select id="t-sede" class="campo" [value]="sedeDestinoId()" (change)="sedeDestinoId.set($any($event.target).value)">
            <option value="" disabled>Seleccione destino...</option>
            @for (s of sedes(); track s.id) {
              <option [value]="s.id">{{ s.nombre }}</option>
            }
          </select>
        </app-form-field>
        <app-form-field etiqueta="Observaciones" para="t-obs">
          <textarea id="t-obs" rows="2" class="campo" [value]="observaciones()" (input)="observaciones.set($any($event.target).value)"></textarea>
        </app-form-field>
      </div>
      <ng-container modalPie>
        <app-button texto="Cancelar" variante="secundario" (presionado)="modalCrear.set(false)" />
        <app-button texto="Crear traslado" [deshabilitado]="!sedeDestinoId()" (presionado)="guardar()" />
      </ng-container>
    </app-modal>
  `,
})
export class TrasladosPage {
  private readonly trasladoService = inject(TrasladoService);
  private readonly sedeService = inject(SedeService);
  private readonly notificaciones = inject(NotificacionesService);

  protected readonly sedes = toSignal(this.sedeService.listarTodas(), { initialValue: [] as Sede[] });
  protected readonly traslados = signal<Traslado[]>([]);
  protected readonly pagina = signal(0);
  protected readonly meta = signal<PageMeta>({ totalElements: 0, totalPages: 1, number: 0, size: 10 });

  protected readonly modalCrear = signal(false);
  protected readonly ejemplarId = signal(1);
  protected readonly sedeDestinoId = signal('');
  protected readonly observaciones = signal('');
  protected readonly errorForm = signal<string | null>(null);

  constructor() {
    effect(() => this.cargar(this.pagina()));
  }

  private cargar(page = this.pagina()): void {
    this.trasladoService.listarPaginado(page, 10).subscribe((res) => {
      this.traslados.set(res.items);
      this.meta.set(res.page);
    });
  }

  protected abrirCrear(): void {
    this.errorForm.set(null);
    this.sedeDestinoId.set('');
    this.observaciones.set('');
    this.modalCrear.set(true);
  }

  protected guardar(): void {
    this.trasladoService
      .crear({
        ejemplarId: this.ejemplarId(),
        sedeDestinoId: Number(this.sedeDestinoId()),
        observaciones: this.observaciones().trim() || null,
      })
      .subscribe({
        next: () => {
          this.modalCrear.set(false);
          this.notificaciones.mostrar('Traslado registrado en tránsito.', 'exito');
          this.cargar();
        },
        error: (e: HttpErrorResponse) => this.errorForm.set(mensajeDeError(e)),
      });
  }

  protected recibir(id: number): void {
    this.trasladoService.confirmarRecepcion(id).subscribe({
      next: () => {
        this.notificaciones.mostrar(`Recepción de traslado #${id} confirmada.`, 'exito');
        this.cargar();
      },
      error: (e: HttpErrorResponse) => this.notificaciones.mostrar(mensajeDeError(e), 'error'),
    });
  }

  protected cancelar(id: number): void {
    this.trasladoService.cancelar(id).subscribe({
      next: () => {
        this.notificaciones.mostrar(`Traslado #${id} cancelado.`, 'info');
        this.cargar();
      },
      error: (e: HttpErrorResponse) => this.notificaciones.mostrar(mensajeDeError(e), 'error'),
    });
  }
}
