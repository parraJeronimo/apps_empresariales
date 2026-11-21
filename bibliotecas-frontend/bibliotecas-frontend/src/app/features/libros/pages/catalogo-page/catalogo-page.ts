import { Component, effect, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { PageMeta } from '../../../../core/models/api.model';
import { Categoria, Libro } from '../../../../core/models/gestion.model';
import { CategoriaService } from '../../../../core/services/categoria';
import { LibroService } from '../../../../core/services/libro';
import { Badge } from '../../../../shared/ui/atoms/badge/badge';
import { EmptyState } from '../../../../shared/ui/molecules/empty-state/empty-state';
import { Paginator } from '../../../../shared/ui/molecules/paginator/paginator';
import { PageHeader } from '../../../../shared/ui/organisms/page-header/page-header';

@Component({
  selector: 'app-catalogo-page',
  imports: [RouterLink, PageHeader, Badge, Paginator, EmptyState],
  template: `
    <app-page-header titulo="Catálogo de Libros" subtitulo="Disponibilidad en la red de bibliotecas" />

    <div class="barra-filtros">
      <input
        type="search"
        class="campo"
        style="max-width: 320px"
        placeholder="Buscar por título..."
        [value]="texto()"
        (input)="texto.set($any($event.target).value)"
      />
      <select
        class="campo"
        style="max-width: 240px"
        [value]="categoriaId()"
        (change)="categoriaId.set($any($event.target).value)"
      >
        <option value="">Todas las categorías</option>
        @for (c of categorias(); track c.id) {
          <option [value]="c.id">{{ c.nombre }}</option>
        }
      </select>
    </div>

    @if (libros().length === 0) {
      <app-empty-state titulo="No se encontraron libros" descripcion="Prueba cambiando el término de búsqueda." />
    } @else {
      <div class="catalogo-grid">
        @for (libro of libros(); track libro.id) {
          <article class="libro-card">
            <div class="libro-card__top">
              @if (libro.categoriaNombre) {
                <app-badge [texto]="libro.categoriaNombre" variante="info" />
              }
              <span class="libro-card__isbn">ISBN {{ libro.isbn }}</span>
            </div>
            <h3>{{ libro.titulo }}</h3>
            <p class="libro-card__autor">{{ libro.autor }}</p>
            <a class="libro-card__link" [routerLink]="['/libros', libro.id]">Ver detalle →</a>
          </article>
        }
      </div>

      @if (!hayFiltroActivo()) {
        <app-paginator
          [(pagina)]="pagina"
          [totalPaginas]="meta().totalPages"
          [totalElementos]="meta().totalElements"
          etiqueta="libros"
        />
      }
    }
  `,
  styles: `
    .catalogo-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
      gap: var(--space-4);
    }
    .libro-card {
      background: var(--color-superficie);
      border: 1px solid var(--color-borde);
      border-radius: var(--radio-lg);
      padding: var(--space-4);
      display: flex;
      flex-direction: column;
      gap: var(--space-2);

      &__top { display: flex; justify-content: space-between; align-items: center; gap: var(--space-2); }
      &__isbn { font-size: 12px; color: var(--color-texto-suave); }
      &__autor { margin: 0; font-size: 14px; color: var(--color-texto-suave); }
      &__link {
        margin-top: auto;
        padding-top: var(--space-2);
        color: var(--color-primario-hover);
        text-decoration: none;
        font-size: 14px;
        font-weight: 500;
      }
    }
  `,
})
export class CatalogoPage {
  private readonly libroService = inject(LibroService);
  private readonly categoriaService = inject(CategoriaService);

  protected readonly categorias = toSignal(this.categoriaService.listarTodas(), { initialValue: [] as Categoria[] });
  protected readonly texto = signal('');
  protected readonly categoriaId = signal('');
  protected readonly pagina = signal(0);
  protected readonly libros = signal<Libro[]>([]);
  protected readonly meta = signal<PageMeta>({ totalElements: 0, totalPages: 1, number: 0, size: 12 });

  private readonly textoDebounced = toSignal(
    toObservable(this.texto).pipe(debounceTime(300), distinctUntilChanged()),
    { initialValue: '' },
  );

  protected hayFiltroActivo = () => Boolean(this.textoDebounced().trim() || this.categoriaId());

  constructor() {
    effect(() => {
      const q = this.textoDebounced().trim();
      const cat = this.categoriaId() ? Number(this.categoriaId()) : undefined;
      const p = this.pagina();

      if (q || cat) {
        this.libroService.buscar({ titulo: q || undefined, categoriaId: cat }).subscribe((res) => {
          this.libros.set(res);
        });
      } else {
        this.libroService.catalogo(p, 12).subscribe((res) => {
          this.libros.set(res.items);
          this.meta.set(res.page);
        });
      }
    });
  }
}
