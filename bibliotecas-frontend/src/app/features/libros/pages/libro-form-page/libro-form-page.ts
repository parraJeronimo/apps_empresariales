import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input, linkedSignal, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { form, FormField as SignalFormField, FormRoot, ReadonlyFieldState, required } from '@angular/forms/signals';
import { Categoria } from '../../../../core/models/gestion.model';
import { CategoriaService } from '../../../../core/services/categoria';
import { LibroService } from '../../../../core/services/libro';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { mensajeDeError } from '../../../../core/utils/errores';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { FormField } from '../../../../shared/ui/molecules/form-field/form-field';
import { PageHeader } from '../../../../shared/ui/organisms/page-header/page-header';

@Component({
  selector: 'app-libro-form-page',
  imports: [SignalFormField, FormRoot, PageHeader, FormField, Button],
  template: `
    <app-page-header [titulo]="esEdicion() ? 'Editar libro' : 'Nuevo libro'" />

    <form [formRoot]="formulario" class="panel-form">
      @if (errorForm()) {
        <p class="mensaje-error" role="alert">{{ errorForm() }}</p>
      }
      <app-form-field etiqueta="Título" para="l-titulo" [obligatorio]="true" [error]="errorDe(formulario.titulo())">
        <input id="l-titulo" class="campo" [formField]="formulario.titulo" />
      </app-form-field>

      <div class="panel-form__grid">
        <app-form-field etiqueta="Autor" para="l-autor" [obligatorio]="true" [error]="errorDe(formulario.autor())">
          <input id="l-autor" class="campo" [formField]="formulario.autor" />
        </app-form-field>
        <app-form-field etiqueta="ISBN" para="l-isbn" [obligatorio]="true" [error]="errorDe(formulario.isbn())">
          <input id="l-isbn" class="campo" [formField]="formulario.isbn" />
        </app-form-field>
      </div>

      <div class="panel-form__grid">
        <app-form-field etiqueta="Editorial" para="l-editorial">
          <input id="l-editorial" class="campo" [formField]="formulario.editorial" />
        </app-form-field>
        <app-form-field etiqueta="Año publicación" para="l-anio">
          <input id="l-anio" type="number" class="campo" [formField]="formulario.anioPublicacion" />
        </app-form-field>
      </div>

      <app-form-field etiqueta="Categoría" para="l-cat">
        <select id="l-cat" class="campo" [formField]="formulario.categoriaId">
          <option value="">Sin categoría</option>
          @for (c of categorias(); track c.id) {
            <option [value]="c.id">{{ c.nombre }}</option>
          }
        </select>
      </app-form-field>

      <app-form-field etiqueta="Descripción" para="l-desc">
        <textarea id="l-desc" rows="3" class="campo" [formField]="formulario.descripcion"></textarea>
      </app-form-field>

      <div class="panel-form__pie">
        <app-button texto="Cancelar" variante="secundario" (presionado)="cancelar()" />
        <app-button tipo="submit" texto="Guardar libro" [cargando]="guardando()" />
      </div>
    </form>
  `,
})
export class LibroFormPage {
  private readonly libroService = inject(LibroService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly notificaciones = inject(NotificacionesService);
  private readonly router = inject(Router);

  readonly id = input<string>();
  protected readonly esEdicion = computed(() => this.id() !== undefined);
  private readonly idNumero = computed(() => Number(this.id()));

  protected readonly libroResource = this.libroService.porId(this.idNumero);
  protected readonly categorias = toSignal(this.categoriaService.listarTodas(), { initialValue: [] as Categoria[] });
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);

  protected readonly modelo = linkedSignal(() => {
    const l = this.libroResource.hasValue() ? this.libroResource.value() : undefined;
    return {
      titulo: l?.titulo ?? '',
      autor: l?.autor ?? '',
      isbn: l?.isbn ?? '',
      editorial: l?.editorial ?? '',
      anioPublicacion: l?.anioPublicacion ?? new Date().getFullYear(),
      descripcion: l?.descripcion ?? '',
      categoriaId: l?.categoriaId ? String(l.categoriaId) : '',
    };
  });

  protected readonly formulario = form(
    this.modelo,
    (p) => {
      required(p.titulo, { message: 'El título es obligatorio' });
      required(p.autor, { message: 'El autor es obligatorio' });
      required(p.isbn, { message: 'El ISBN es obligatorio' });
    },
    { submission: { action: async () => this.guardar() } },
  );

  protected errorDe(campo: ReadonlyFieldState<unknown>): string | null {
    return campo.touched() ? (campo.errors()[0]?.message ?? null) : null;
  }

  private guardar(): void {
    this.guardando.set(true);
    this.errorForm.set(null);
    const v = this.modelo();
    const payload = {
      titulo: v.titulo.trim(),
      autor: v.autor.trim(),
      isbn: v.isbn.trim(),
      editorial: v.editorial.trim() || null,
      anioPublicacion: v.anioPublicacion ? Number(v.anioPublicacion) : null,
      descripcion: v.descripcion.trim() || null,
      categoriaId: v.categoriaId ? Number(v.categoriaId) : null,
    };

    const req$ = this.esEdicion()
      ? this.libroService.actualizar(this.idNumero(), payload)
      : this.libroService.crear(payload);

    req$.subscribe({
      next: (guardado) => {
        this.guardando.set(false);
        this.notificaciones.mostrar('Libro guardado correctamente.', 'exito');
        this.router.navigate(['/libros', guardado.id]);
      },
      error: (e: HttpErrorResponse) => {
        this.guardando.set(false);
        this.errorForm.set(mensajeDeError(e));
      },
    });
  }

  protected cancelar(): void {
    this.router.navigate(['/catalogo']);
  }
}
