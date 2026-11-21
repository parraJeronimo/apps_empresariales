import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse, toPaginatedResponse } from '../models/api.model';
import { Prestamo, PrestamoCreateRequest } from '../models/operacion.model';

@Service()
export class PrestamoService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/prestamos`;

  listarPaginado(page = 0, size = 10): Observable<PaginatedResponse<Prestamo>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<Prestamo[]>(this.endpoint, { params, observe: 'response' })
      .pipe(map((res) => toPaginatedResponse(res, page, size)));
  }

  vencidos(): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(`${this.endpoint}/vencidos`);
  }

  crear(datos: PrestamoCreateRequest): Observable<Prestamo> {
    return this.http.post<Prestamo>(this.endpoint, datos);
  }

  devolver(id: number, sedeDevolucionId: number): Observable<Prestamo> {
    return this.http.post<Prestamo>(`${this.endpoint}/${id}/devolucion`, { sedeDevolucionId });
  }
}
