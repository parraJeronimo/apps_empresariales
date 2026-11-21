import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input, numberAttribute } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../../core/services/auth';
import { LibroService } from '../../../../core/services/libro';
import { Badge } from '../../../../shared/ui/atoms/badge/badge';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { PageHeader } from '../../../../shared/ui/organisms/page-header/page-header';

@Component({
  selector: 'app-libro-detalle-page',
  imports: [RouterLink, PageHeader, Badge, Button],
  template: `
    @if (libro.isLoading()) {
      <p>Cargando libro...</p>
    } @else if (noEncontrado()) {
      <div class="panel-form">
        <h2>Libro no encontrado</h2>
        <p>No existe un libro con el código solicitado.</p>
        <a routerLink="/catalogo">← Volver al catálogo</a>
      </div>
    } @else if (libro.hasValue()) {
      @let l = libro.value();
      <app-page-header [titulo]="l.titulo" [subtitulo]="l.autor">
        <a routerLink="/catalogo"><app-button texto="← Volver" variante="secundario" /></a>
        @if (auth.autenticado()) {
          <a [routerLink]="['/libros', l.id, 'editar']"><app-button texto="Editar libro" /></a>
        }
      </app-page-header>

      <div class="panel-form">
        @if (l.categoriaNombre) {
          <div><app-badge [texto]="l.categoriaNombre" variante="info" /></div>
        }
        <dl class="detalle-grid">
          <div><dt>ISBN</dt><dd>{{ l.isbn }}</dd></div>
          <div><dt>Editorial</dt><dd>{{ l.editorial || '—' }}</dd></div>
          <div><dt>Año de publicación</dt><dd>{{ l.anioPublicacion || '—' }}</dd></div>
        </dl>
        <div>
          <h4>Descripción</h4>
          <p style="margin: 8px 0 0; color: var(--color-texto-suave)">
            {{ l.descripcion || 'Sin descripción registrada.' }}
          </p>
        </div>
      </div>
    }
  `,
  styles: `
    .detalle-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
      gap: var(--space-4);
      margin: 0;
      dt { font-size: 12px; color: var(--color-texto-suave); text-transform: uppercase; }
      dd { margin: 4px 0 0; font-weight: 500; }
    }
  `,
})
export class LibroDetallePage {
  private readonly libroService = inject(LibroService);
  protected readonly auth = inject(AuthService);

  readonly id = input.required({ transform: numberAttribute });
  protected readonly libro = this.libroService.porId(this.id);

  protected readonly noEncontrado = computed(
    () =>
      Number.isNaN(this.id()) ||
      (this.libro.error() as HttpErrorResponse | undefined)?.status === 404,
  );
}
