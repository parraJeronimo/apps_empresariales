import { HttpClient, HttpParams, httpResource } from '@angular/common/http';
import { inject, Service, Signal } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse, toPaginatedResponse } from '../models/api.model';
import { Libro, LibroRequest } from '../models/gestion.model';

export interface FiltrosLibro {
  titulo?: string;
  autor?: string;
  isbn?: string;
  categoriaId?: number;
}

@Service()
export class LibroService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/libros`;

  listarPaginado(page = 0, size = 10): Observable<PaginatedResponse<Libro>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<Libro[]>(this.endpoint, { params, observe: 'response' })
      .pipe(map((res) => toPaginatedResponse(res, page, size)));
  }

  catalogo(page = 0, size = 12): Observable<PaginatedResponse<Libro>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<Libro[]>(`${this.endpoint}/catalogo`, { params, observe: 'response' })
      .pipe(map((res) => toPaginatedResponse(res, page, size)));
  }

  buscar(filtros: FiltrosLibro): Observable<Libro[]> {
    let params = new HttpParams();
    if (filtros.titulo) params = params.set('titulo', filtros.titulo);
    if (filtros.autor) params = params.set('autor', filtros.autor);
    if (filtros.isbn) params = params.set('isbn', filtros.isbn);
    if (filtros.categoriaId) params = params.set('categoriaId', filtros.categoriaId);
    return this.http.get<Libro[]>(`${this.endpoint}/buscar`, { params });
  }

  porId(id: Signal<number>) {
    return httpResource<Libro>(() =>
      Number.isNaN(id()) ? undefined : `${this.endpoint}/${id()}`,
    );
  }

  crear(datos: LibroRequest): Observable<Libro> {
    return this.http.post<Libro>(this.endpoint, datos);
  }

  actualizar(id: number, datos: Partial<LibroRequest>): Observable<Libro> {
    return this.http.put<Libro>(`${this.endpoint}/${id}`, datos);
  }
}
