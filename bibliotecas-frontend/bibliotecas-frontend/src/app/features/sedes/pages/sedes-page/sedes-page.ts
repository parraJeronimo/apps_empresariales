import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { form, FormField as SignalFormField, FormRoot, ReadonlyFieldState, required } from '@angular/forms/signals';
import { PageMeta } from '../../../../core/models/api.model';
import { Sede } from '../../../../core/models/gestion.model';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { SedeService } from '../../../../core/services/sede';
import { mensajeDeError } from '../../../../core/utils/errores';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { EmptyState } from '../../../../shared/ui/molecules/empty-state/empty-state';
import { FormField } from '../../../../shared/ui/molecules/form-field/form-field';
import { Modal } from '../../../../shared/ui/molecules/modal/modal';
import { Paginator } from '../../../../shared/ui/molecules/paginator/paginator';
import { StatusBadge } from '../../../../shared/ui/molecules/status-badge/status-badge';
import { ConfirmDialog } from '../../../../shared/ui/organisms/confirm-dialog/confirm-dialog';
import { PageHeader } from '../../../../shared/ui/organisms/page-header/page-header';

@Component({
  selector: 'app-sedes-page',
  imports: [
    SignalFormField, FormRoot, PageHeader, Button, StatusBadge,
    Paginator, EmptyState, Modal, FormField, ConfirmDialog,
  ],
  templateUrl: './sedes-page.html',
})
export class SedesPage {
  private readonly sedeService = inject(SedeService);
  private readonly notificaciones = inject(NotificacionesService);

  protected readonly sedes = signal<Sede[]>([]);
  protected readonly pagina = signal(0);
  protected readonly meta = signal<PageMeta>({ totalElements: 0, totalPages: 1, number: 0, size: 10 });
  protected readonly cargando = signal(false);

  protected readonly modalAbierto = signal(false);
  protected readonly sedeEditando = signal<Sede | null>(null);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);

  protected readonly modelo = signal({ nombre: '', direccion: '', ciudad: '', telefono: '' });
  protected readonly formulario = form(
    this.modelo,
    (p) => {
      required(p.nombre, { message: 'El nombre de la sede es obligatorio' });
      required(p.direccion, { message: 'La dirección es obligatoria' });
    },
    { submission: { action: async () => this.guardar() } },
  );

  protected readonly confirmAbierto = signal(false);
  protected readonly sedeToggle = signal<Sede | null>(null);
  protected readonly procesandoToggle = signal(false);
  protected readonly errorToggle = signal<string | null>(null);

  constructor() {
    effect(() => {
      const p = this.pagina();
      this.cargarSedes(p);
    });
  }

  protected cargarSedes(page = this.pagina()): void {
    this.cargando.set(true);
    this.sedeService.listarPaginado(page, 10).subscribe({
      next: (res) => {
        this.sedes.set(res.items);
        this.meta.set(res.page);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  protected abrirCrear(): void {
    this.sedeEditando.set(null);
    this.errorForm.set(null);
    this.modelo.set({ nombre: '', direccion: '', ciudad: '', telefono: '' });
    this.modalAbierto.set(true);
  }

  protected abrirEditar(sede: Sede): void {
    this.sedeEditando.set(sede);
    this.errorForm.set(null);
    this.modelo.set({
      nombre: sede.nombre,
      direccion: sede.direccion,
      ciudad: sede.ciudad ?? '',
      telefono: sede.telefono ?? '',
    });
    this.modalAbierto.set(true);
  }

  protected errorDe(campo: ReadonlyFieldState<unknown>): string | null {
    return campo.touched() ? (campo.errors()[0]?.message ?? null) : null;
  }

  private guardar(): void {
    this.guardando.set(true);
    this.errorForm.set(null);
    const v = this.modelo();
    const payload = {
      nombre: v.nombre.trim(),
      direccion: v.direccion.trim(),
      ciudad: v.ciudad.trim() || null,
      telefono: v.telefono.trim() || null,
    };

    const editando = this.sedeEditando();
    const req$ = editando
      ? this.sedeService.actualizar(editando.id, payload)
      : this.sedeService.crear(payload);

    req$.subscribe({
      next: () => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.notificaciones.mostrar(
          editando ? 'Sede actualizada correctamente.' : 'Sede registrada correctamente.',
          'exito',
        );
        this.cargarSedes();
      },
      error: (e: HttpErrorResponse) => {
        this.guardando.set(false);
        this.errorForm.set(mensajeDeError(e));
      },
    });
  }

  protected pedirCambioEstado(sede: Sede): void {
    this.sedeToggle.set(sede);
    this.errorToggle.set(null);
    this.confirmAbierto.set(true);
  }

  protected confirmarCambioEstado(): void {
    const sede = this.sedeToggle();
    if (!sede) return;

    this.procesandoToggle.set(true);
    this.errorToggle.set(null);
    const req$ = sede.activa
      ? this.sedeService.desactivar(sede.id)
      : this.sedeService.activar(sede.id);

    req$.subscribe({
      next: () => {
        this.procesandoToggle.set(false);
        this.confirmAbierto.set(false);
        this.notificaciones.mostrar(sede.activa ? 'Sede desactivada.' : 'Sede activada.', 'exito');
        this.cargarSedes();
      },
      error: (e: HttpErrorResponse) => {
        this.procesandoToggle.set(false);
        this.errorToggle.set(mensajeDeError(e));
      },
    });
  }
}
