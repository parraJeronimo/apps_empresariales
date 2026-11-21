import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { form, FormField as SignalFormField, FormRoot, ReadonlyFieldState, required } from '@angular/forms/signals';
import { Categoria } from '../../../../core/models/gestion.model';
import { CategoriaService } from '../../../../core/services/categoria';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { mensajeDeError } from '../../../../core/utils/errores';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { EmptyState } from '../../../../shared/ui/molecules/empty-state/empty-state';
import { FormField } from '../../../../shared/ui/molecules/form-field/form-field';
import { Modal } from '../../../../shared/ui/molecules/modal/modal';
import { PageHeader } from '../../../../shared/ui/organisms/page-header/page-header';

@Component({
  selector: 'app-categorias-page',
  imports: [SignalFormField, FormRoot, PageHeader, Button, EmptyState, Modal, FormField],
  template: `
    <app-page-header titulo="Categorías" subtitulo="Clasificación jerárquica del catálogo">
      <app-button texto="+ Nueva categoría" (presionado)="abrirCrear()" />
    </app-page-header>

    @if (raices().length === 0) {
      <app-empty-state titulo="Sin categorías" descripcion="Crea la primera categoría raíz." />
    } @else {
      <div class="tabla-contenedor">
        <table class="tabla">
          <thead>
            <tr>
              <th>Categoría</th>
              <th>Descripción</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (cat of raices(); track cat.id) {
              <tr>
                <td><strong>{{ cat.nombre }}</strong></td>
                <td>{{ cat.descripcion || '—' }}</td>
                <td class="tabla__acciones">
                  <app-button
                    texto="Ver subcategorías"
                    variante="secundario"
                    (presionado)="cargarSubcategorias(cat.id)"
                  />
                </td>
              </tr>
              @for (sub of subcategorias()[cat.id] ?? []; track sub.id) {
                <tr style="background: var(--color-fondo)">
                  <td style="padding-left: var(--space-8)">↳ {{ sub.nombre }}</td>
                  <td>{{ sub.descripcion || '—' }}</td>
                  <td></td>
                </tr>
              }
            }
          </tbody>
        </table>
      </div>
    }

    <app-modal titulo="Nueva categoría" [(abierto)]="modalAbierto">
      <form id="catForm" [formRoot]="formulario" class="panel-form panel-form--modal">
        @if (errorForm()) {
          <p class="mensaje-error" role="alert">{{ errorForm() }}</p>
        }
        <app-form-field etiqueta="Nombre" para="c-nombre" [obligatorio]="true" [error]="errorDe(formulario.nombre())">
          <input id="c-nombre" class="campo" [formField]="formulario.nombre" />
        </app-form-field>
        <app-form-field etiqueta="Descripción" para="c-desc">
          <textarea id="c-desc" class="campo" rows="2" [formField]="formulario.descripcion"></textarea>
        </app-form-field>
        <app-form-field etiqueta="Categoría padre (opcional)" para="c-padre">
          <select id="c-padre" class="campo" [formField]="formulario.categoriaPadreId">
            <option value="">Ninguna (categoría raíz)</option>
            @for (r of raices(); track r.id) {
              <option [value]="r.id">{{ r.nombre }}</option>
            }
          </select>
        </app-form-field>
      </form>
      <ng-container modalPie>
        <app-button texto="Cancelar" variante="secundario" (presionado)="modalAbierto.set(false)" />
        <app-button tipo="submit" formId="catForm" texto="Guardar" [cargando]="guardando()" />
      </ng-container>
    </app-modal>
  `,
})
export class CategoriasPage implements OnInit {
  private readonly categoriaService = inject(CategoriaService);
  private readonly notificaciones = inject(NotificacionesService);

  protected readonly raices = signal<Categoria[]>([]);
  protected readonly subcategorias = signal<Record<number, Categoria[]>>({});
  protected readonly modalAbierto = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);

  protected readonly modelo = signal({ nombre: '', descripcion: '', categoriaPadreId: '' });
  protected readonly formulario = form(
    this.modelo,
    (p) => { required(p.nombre, { message: 'El nombre es obligatorio' }); },
    { submission: { action: async () => this.guardar() } },
  );

  ngOnInit(): void {
    this.cargarRaices();
  }

  protected cargarRaices(): void {
    this.categoriaService.listarRaiz().subscribe((data) => this.raices.set(data));
  }

  protected cargarSubcategorias(padreId: number): void {
    this.categoriaService.listarSubcategorias(padreId).subscribe((subs) => {
      this.subcategorias.update((actual) => ({ ...actual, [padreId]: subs }));
    });
  }

  protected abrirCrear(): void {
    this.errorForm.set(null);
    this.modelo.set({ nombre: '', descripcion: '', categoriaPadreId: '' });
    this.modalAbierto.set(true);
  }

  protected errorDe(campo: ReadonlyFieldState<unknown>): string | null {
    return campo.touched() ? (campo.errors()[0]?.message ?? null) : null;
  }

  private guardar(): void {
    this.guardando.set(true);
    const v = this.modelo();
    const padreId = v.categoriaPadreId ? Number(v.categoriaPadreId) : null;

    this.categoriaService
      .crear({
        nombre: v.nombre.trim(),
        descripcion: v.descripcion.trim() || null,
        categoriaPadreId: padreId,
      })
      .subscribe({
        next: () => {
          this.guardando.set(false);
          this.modalAbierto.set(false);
          this.notificaciones.mostrar('Categoría creada.', 'exito');
          if (padreId) this.cargarSubcategorias(padreId);
          else this.cargarRaices();
        },
        error: (e: HttpErrorResponse) => {
          this.guardando.set(false);
          this.errorForm.set(mensajeDeError(e));
        },
      });
  }
}
