import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { form, FormField as SignalFormField, FormRoot, ReadonlyFieldState, required } from '@angular/forms/signals';
import { PageMeta } from '../../../../core/models/api.model';
import { Ejemplar, MovimientoEjemplar, Sede } from '../../../../core/models/gestion.model';
import { EjemplarService } from '../../../../core/services/ejemplar';
import { NotificacionesService } from '../../../../core/services/notificaciones';
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
  selector: 'app-ejemplares-page',
  imports: [
    DatePipe, SignalFormField, FormRoot, PageHeader, Button,
    StatusBadge, Paginator, EmptyState, Modal, FormField,
  ],
  template: `
    <app-page-header titulo="Ejemplares" subtitulo="Inventario físico por sede e historial de movimientos">
      <app-button texto="+ Nuevo ejemplar" (presionado)="abrirCrear()" />
    </app-page-header>

    <div class="barra-filtros">
      <select class="campo" style="max-width: 260px" [value]="sedeFiltro()" (change)="sedeFiltro.set($any($event.target).value)">
        <option value="">Todas las sedes (paginado)</option>
        @for (s of sedes(); track s.id) {
          <option [value]="s.id">{{ s.nombre }}</option>
        }
      </select>
    </div>

    @if (ejemplares().length === 0) {
      <app-empty-state titulo="No hay ejemplares" />
    } @else {
      <div class="tabla-contenedor">
        <table class="tabla">
          <thead>
            <tr>
              <th>Código</th>
              <th>Libro</th>
              <th>Estado</th>
              <th>Sede Actual</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (e of ejemplares(); track e.id) {
              <tr>
                <td><strong>{{ e.codigo }}</strong></td>
                <td>{{ e.libroTitulo ?? ('Libro #' + e.libroId) }}</td>
                <td><app-status-badge [estado]="e.estado" /></td>
                <td>{{ e.sedeActualNombre ?? ('Sede #' + e.sedeActualId) }}</td>
                <td class="tabla__acciones">
                  <app-button texto="Historial" variante="secundario" (presionado)="verHistorial(e)" />
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>

      @if (!sedeFiltro()) {
        <app-paginator
          [(pagina)]="pagina"
          [totalPaginas]="meta().totalPages"
          [totalElementos]="meta().totalElements"
          etiqueta="ejemplares"
        />
      }
    }

    <app-modal titulo="Registrar ejemplar" [(abierto)]="modalCrear">
      <form id="ejForm" [formRoot]="formulario" class="panel-form panel-form--modal">
        @if (errorForm()) {
          <p class="mensaje-error" role="alert">{{ errorForm() }}</p>
        }
        <app-form-field etiqueta="Código" para="e-cod" ayuda="Opcional: si lo dejas vacío el sistema genera el código">
          <input id="e-cod" class="campo" [formField]="formulario.codigo" />
        </app-form-field>
        <app-form-field etiqueta="ID del Libro" para="e-libro" [obligatorio]="true" [error]="errorDe(formulario.libroId())">
          <input id="e-libro" type="number" class="campo" [formField]="formulario.libroId" />
        </app-form-field>
        <app-form-field etiqueta="Sede" para="e-sede" [obligatorio]="true" [error]="errorDe(formulario.sedeId())">
          <select id="e-sede" class="campo" [formField]="formulario.sedeId">
            <option value="" disabled>Seleccione una sede...</option>
            @for (s of sedes(); track s.id) {
              <option [value]="s.id">{{ s.nombre }}</option>
            }
          </select>
        </app-form-field>
      </form>
      <ng-container modalPie>
        <app-button texto="Cancelar" variante="secundario" (presionado)="modalCrear.set(false)" />
        <app-button tipo="submit" formId="ejForm" texto="Registrar" [cargando]="guardando()" />
      </ng-container>
    </app-modal>

    <app-modal [titulo]="'Historial: ' + (ejemplarActivo()?.codigo ?? '')" [(abierto)]="modalHistorial">
      @for (m of movimientos(); track m.id) {
        <div style="padding: var(--space-2) 0; border-bottom: 1px solid var(--color-borde)">
          <strong>{{ m.tipo }}</strong> · <small>{{ m.fecha | date: 'short' }}</small>
          @if (m.observaciones) { <p style="margin: 4px 0 0">{{ m.observaciones }}</p> }
        </div>
      } @empty {
        <p>Sin movimientos registrados.</p>
      }
      <ng-container modalPie>
        <app-button texto="Cerrar" variante="secundario" (presionado)="modalHistorial.set(false)" />
      </ng-container>
    </app-modal>
  `,
})
export class EjemplaresPage {
  private readonly ejemplarService = inject(EjemplarService);
  private readonly sedeService = inject(SedeService);
  private readonly notificaciones = inject(NotificacionesService);

  protected readonly sedes = toSignal(this.sedeService.listarTodas(), { initialValue: [] as Sede[] });
  protected readonly sedeFiltro = signal('');
  protected readonly pagina = signal(0);
  protected readonly ejemplares = signal<Ejemplar[]>([]);
  protected readonly meta = signal<PageMeta>({ totalElements: 0, totalPages: 1, number: 0, size: 10 });

  protected readonly modalCrear = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);
  protected readonly modelo = signal({ codigo: '', libroId: 1, sedeId: '' });

  protected readonly formulario = form(
    this.modelo,
    (p) => { required(p.sedeId, { message: 'La sede es obligatoria' }); },
    { submission: { action: async () => this.guardar() } },
  );

  protected readonly modalHistorial = signal(false);
  protected readonly ejemplarActivo = signal<Ejemplar | null>(null);
  protected readonly movimientos = signal<MovimientoEjemplar[]>([]);

  constructor() {
    effect(() => {
      const s = this.sedeFiltro();
      const p = this.pagina();
      if (s) {
        this.ejemplarService.porSede(Number(s)).subscribe((list) => this.ejemplares.set(list));
      } else {
        this.ejemplarService.listarPaginado(p, 10).subscribe((res) => {
          this.ejemplares.set(res.items);
          this.meta.set(res.page);
        });
      }
    });
  }

  protected abrirCrear(): void {
    this.errorForm.set(null);
    this.modelo.set({ codigo: '', libroId: 1, sedeId: '' });
    this.modalCrear.set(true);
  }

  protected errorDe(campo: ReadonlyFieldState<unknown>): string | null {
    return campo.touched() ? (campo.errors()[0]?.message ?? null) : null;
  }

  private guardar(): void {
    this.guardando.set(true);
    const v = this.modelo();
    this.ejemplarService
      .crear({
        codigo: v.codigo.trim() || null,
        libroId: Number(v.libroId),
        sedeId: Number(v.sedeId),
      })
      .subscribe({
        next: () => {
          this.guardando.set(false);
          this.modalCrear.set(false);
          this.notificaciones.mostrar('Ejemplar registrado.', 'exito');
          this.pagina.set(0);
        },
        error: (e: HttpErrorResponse) => {
          this.guardando.set(false);
          this.errorForm.set(mensajeDeError(e));
        },
      });
  }

  protected verHistorial(ejemplar: Ejemplar): void {
    this.ejemplarActivo.set(ejemplar);
    this.ejemplarService.historial(ejemplar.id).subscribe((movs) => {
      this.movimientos.set(movs);
      this.modalHistorial.set(true);
    });
  }
}
