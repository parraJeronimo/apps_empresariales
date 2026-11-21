import { HttpClient, HttpContext, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SIN_INDICADOR_CARGA } from '../interceptors/contexto';
import { PaginatedResponse, toPaginatedResponse } from '../models/api.model';
import { Sede, SedeRequest } from '../models/gestion.model';

@Service()
export class SedeService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/sedes`;

  listarPaginado(page = 0, size = 10): Observable<PaginatedResponse<Sede>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<Sede[]>(this.endpoint, { params, observe: 'response' })
      .pipe(map((res) => toPaginatedResponse(res, page, size)));
  }

  listarTodas(): Observable<Sede[]> {
    const context = new HttpContext().set(SIN_INDICADOR_CARGA, true);
    return this.http.get<Sede[]>(this.endpoint, { context });
  }

  crear(datos: SedeRequest): Observable<Sede> {
    return this.http.post<Sede>(this.endpoint, datos);
  }

  actualizar(id: number, datos: Partial<SedeRequest>): Observable<Sede> {
    return this.http.put<Sede>(`${this.endpoint}/${id}`, datos);
  }

  activar(id: number): Observable<Sede> {
    return this.http.patch<Sede>(`${this.endpoint}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<Sede> {
    return this.http.patch<Sede>(`${this.endpoint}/${id}/desactivar`, {});
  }
}
