import { HttpClient, HttpContext } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SIN_INDICADOR_CARGA, SIN_NOTIFICACION_ERROR } from '../interceptors/contexto';
import { Categoria, CategoriaRequest } from '../models/gestion.model';

@Service()
export class CategoriaService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/categorias`;

  listarTodas(): Observable<Categoria[]> {
    const context = new HttpContext()
      .set(SIN_INDICADOR_CARGA, true)
      .set(SIN_NOTIFICACION_ERROR, true);
    return this.http.get<Categoria[]>(this.endpoint, { context }).pipe(catchError(() => of([])));
  }

  listarRaiz(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${this.endpoint}/raiz`);
  }

  listarSubcategorias(padreId: number): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${this.endpoint}/${padreId}/subcategorias`);
  }

  crear(datos: CategoriaRequest): Observable<Categoria> {
    return this.http.post<Categoria>(this.endpoint, datos);
  }
}
