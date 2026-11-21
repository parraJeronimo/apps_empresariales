import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { form, FormField as SignalFormField, FormRoot, max, min, ReadonlyFieldState, required } from '@angular/forms/signals';
import { PageMeta } from '../../../../core/models/api.model';
import { Sede } from '../../../../core/models/gestion.model';
import { Prestamo } from '../../../../core/models/operacion.model';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { PrestamoService } from '../../../../core/services/prestamo';
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
  selector: 'app-prestamos-page',
  imports: [
    DatePipe, SignalFormField, FormRoot, PageHeader, Button,
    StatusBadge, Paginator, EmptyState, Modal, FormField,
  ],
  template: `
    <app-page-header titulo="Préstamos y Devoluciones" subtitulo="Circulación de ejemplares entre sedes">
      <app-button
        [texto]="soloVencidos() ? 'Ver todos' : 'Ver vencidos'"
        variante="secundario"
        (presionado)="soloVencidos.update(v => !v)"
      />
      <app-button texto="+ Registrar préstamo" (presionado)="abrirCrear()" />
    </app-page-header>

    @if (prestamos().length === 0) {
      <app-empty-state titulo="No hay préstamos registrados" />
    } @else {
      <div class="tabla-contenedor">
        <table class="tabla">
          <thead>
            <tr>
              <th>ID</th>
              <th>Ejemplar</th>
              <th>Cliente</th>
              <th>Inicio / Límite</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (p of prestamos(); track p.id) {
              <tr>
                <td>#{{ p.id }}</td>
                <td>{{ p.ejemplarCodigo ?? ('Ejemplar #' + p.ejemplarId) }}</td>
                <td>{{ p.clienteNombre ?? ('Cliente #' + p.clienteId) }}</td>
                <td>{{ p.fechaInicio | date: 'shortDate' }} → {{ p.fechaLimite | date: 'shortDate' }}</td>
                <td><app-status-badge [estado]="p.vencido && p.estado === 'ACTIVO' ? 'VENCIDO' : p.estado" /></td>
                <td class="tabla__acciones">
                  @if (p.estado === 'ACTIVO') {
                    <app-button texto="Devolver" variante="secundario" (presionado)="abrirDevolucion(p)" />
                  }
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>

      @if (!soloVencidos()) {
        <app-paginator
          [(pagina)]="pagina"
          [totalPaginas]="meta().totalPages"
          [totalElementos]="meta().totalElements"
          etiqueta="préstamos"
        />
      }
    }

    <app-modal titulo="Registrar préstamo" [(abierto)]="modalCrear">
      <form id="prestamoForm" [formRoot]="formulario" class="panel-form panel-form--modal">
        @if (errorForm()) {
          <p class="mensaje-error" role="alert">{{ errorForm() }}</p>
        }
        <app-form-field etiqueta="ID Ejemplar" para="p-ej" [obligatorio]="true" [error]="errorDe(formulario.ejemplarId())">
          <input id="p-ej" type="number" class="campo" [formField]="formulario.ejemplarId" />
        </app-form-field>
        <app-form-field etiqueta="ID Cliente" para="p-cl" [obligatorio]="true" [error]="errorDe(formulario.clienteId())">
          <input id="p-cl" type="number" class="campo" [formField]="formulario.clienteId" />
        </app-form-field>
        <app-form-field etiqueta="Sede" para="p-sede" [obligatorio]="true" [error]="errorDe(formulario.sedeId())">
          <select id="p-sede" class="campo" [formField]="formulario.sedeId">
            <option value="" disabled>Seleccione una sede...</option>
            @for (s of sedes(); track s.id) {
              <option [value]="s.id">{{ s.nombre }}</option>
            }
          </select>
        </app-form-field>
        <app-form-field etiqueta="Días de préstamo (1-30)" para="p-dias" [error]="errorDe(formulario.diasPrestamo())">
          <input id="p-dias" type="number" class="campo" [formField]="formulario.diasPrestamo" />
        </app-form-field>
      </form>
      <ng-container modalPie>
        <app-button texto="Cancelar" variante="secundario" (presionado)="modalCrear.set(false)" />
        <app-button tipo="submit" formId="prestamoForm" texto="Registrar" [cargando]="guardando()" />
      </ng-container>
    </app-modal>

    <app-modal [titulo]="'Devolver préstamo #' + (prestamoDevolver()?.id ?? '')" [(abierto)]="modalDevolucion">
      @if (errorDev()) {
        <p class="mensaje-error" role="alert">{{ errorDev() }}</p>
      }
      <app-form-field etiqueta="Sede donde se devuelve" para="dev-sede" [obligatorio]="true">
        <select
          id="dev-sede"
          class="campo"
          [value]="sedeDevolucionId()"
          (change)="sedeDevolucionId.set($any($event.target).value)"
        >
          <option value="" disabled>Seleccione una sede...</option>
          @for (s of sedes(); track s.id) {
            <option [value]="s.id">{{ s.nombre }}</option>
          }
        </select>
      </app-form-field>
      <ng-container modalPie>
        <app-button texto="Cancelar" variante="secundario" (presionado)="modalDevolucion.set(false)" />
        <app-button
          texto="Confirmar devolución"
          [cargando]="guardando()"
          [deshabilitado]="!sedeDevolucionId()"
          (presionado)="confirmarDevolucion()"
        />
      </ng-container>
    </app-modal>
  `,
})
export class PrestamosPage {
  private readonly prestamoService = inject(PrestamoService);
  private readonly sedeService = inject(SedeService);
  private readonly notificaciones = inject(NotificacionesService);

  protected readonly sedes = toSignal(this.sedeService.listarTodas(), { initialValue: [] as Sede[] });
  protected readonly prestamos = signal<Prestamo[]>([]);
  protected readonly pagina = signal(0);
  protected readonly meta = signal<PageMeta>({ totalElements: 0, totalPages: 1, number: 0, size: 10 });
  protected readonly soloVencidos = signal(false);

  protected readonly modalCrear = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);
  protected readonly modelo = signal({ ejemplarId: 1, clienteId: 1, sedeId: '', diasPrestamo: 14 });

  protected readonly formulario = form(
    this.modelo,
    (p) => {
      min(p.ejemplarId, 1, { message: 'Ejemplar inválido' });
      min(p.clienteId, 1, { message: 'Cliente inválido' });
      required(p.sedeId, { message: 'La sede es obligatoria' });
      min(p.diasPrestamo, 1, { message: 'Mínimo 1 día' });
      max(p.diasPrestamo, 30, { message: 'Máximo 30 días' });
    },
    { submission: { action: async () => this.guardar() } },
  );

  protected readonly modalDevolucion = signal(false);
  protected readonly prestamoDevolver = signal<Prestamo | null>(null);
  protected readonly sedeDevolucionId = signal('');
  protected readonly errorDev = signal<string | null>(null);

  constructor() {
    effect(() => {
      const vencidos = this.soloVencidos();
      const p = this.pagina();
      this.cargar(vencidos, p);
    });
  }

  private cargar(vencidos: boolean, page: number): void {
    if (vencidos) {
      this.prestamoService.vencidos().subscribe((list) => this.prestamos.set(list));
    } else {
      this.prestamoService.listarPaginado(page, 10).subscribe((res) => {
        this.prestamos.set(res.items);
        this.meta.set(res.page);
      });
    }
  }

  protected abrirCrear(): void {
    this.errorForm.set(null);
    this.modelo.set({ ejemplarId: 1, clienteId: 1, sedeId: '', diasPrestamo: 14 });
    this.modalCrear.set(true);
  }

  protected errorDe(campo: ReadonlyFieldState<unknown>): string | null {
    return campo.touched() ? (campo.errors()[0]?.message ?? null) : null;
  }

  private guardar(): void {
    this.guardando.set(true);
    const v = this.modelo();
    this.prestamoService
      .crear({
        ejemplarId: Number(v.ejemplarId),
        clienteId: Number(v.clienteId),
        sedeId: Number(v.sedeId),
        diasPrestamo: Number(v.diasPrestamo),
      })
      .subscribe({
        next: (creado) => {
          this.guardando.set(false);
          this.modalCrear.set(false);
          this.notificaciones.mostrar(`Préstamo #${creado.id} registrado. Límite: ${creado.fechaLimite}`, 'exito');
          this.cargar(this.soloVencidos(), this.pagina());
        },
        error: (e: HttpErrorResponse) => {
          this.guardando.set(false);
          this.errorForm.set(mensajeDeError(e));
        },
      });
  }

  protected abrirDevolucion(p: Prestamo): void {
    this.prestamoDevolver.set(p);
    this.sedeDevolucionId.set(String(p.sedeId ?? ''));
    this.errorDev.set(null);
    this.modalDevolucion.set(true);
  }

  protected confirmarDevolucion(): void {
    const p = this.prestamoDevolver();
    if (!p || !this.sedeDevolucionId()) return;

    this.guardando.set(true);
    this.prestamoService.devolver(p.id, Number(this.sedeDevolucionId())).subscribe({
      next: () => {
        this.guardando.set(false);
        this.modalDevolucion.set(false);
        this.notificaciones.mostrar(`Devolución del préstamo #${p.id} registrada.`, 'exito');
        this.cargar(this.soloVencidos(), this.pagina());
      },
      error: (e: HttpErrorResponse) => {
        this.guardando.set(false);
        this.errorDev.set(mensajeDeError(e));
      },
    });
  }
}
