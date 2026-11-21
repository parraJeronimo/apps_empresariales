import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse, toPaginatedResponse } from '../models/api.model';
import { Traslado, TrasladoCreateRequest } from '../models/operacion.model';

@Service()
export class TrasladoService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/traslados`;

  listarPaginado(page = 0, size = 10): Observable<PaginatedResponse<Traslado>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<Traslado[]>(this.endpoint, { params, observe: 'response' })
      .pipe(map((res) => toPaginatedResponse(res, page, size)));
  }

  crear(datos: TrasladoCreateRequest): Observable<Traslado> {
    return this.http.post<Traslado>(this.endpoint, datos);
  }

  confirmarRecepcion(id: number): Observable<Traslado> {
    return this.http.patch<Traslado>(`${this.endpoint}/${id}/recepcion`, {});
  }

  cancelar(id: number): Observable<Traslado> {
    return this.http.patch<Traslado>(`${this.endpoint}/${id}/cancelar`, {});
  }
}
