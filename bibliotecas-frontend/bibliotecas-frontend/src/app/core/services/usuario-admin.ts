import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse, toPaginatedResponse } from '../models/api.model';
import { UsuarioAdmin, UsuarioAdminRequest } from '../models/operacion.model';

@Service()
export class UsuarioAdminService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/usuarios-admin`;

  listarPaginado(page = 0, size = 10): Observable<PaginatedResponse<UsuarioAdmin>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<UsuarioAdmin[]>(this.endpoint, { params, observe: 'response' })
      .pipe(map((res) => toPaginatedResponse(res, page, size)));
  }

  crear(datos: UsuarioAdminRequest): Observable<UsuarioAdmin> {
    return this.http.post<UsuarioAdmin>(this.endpoint, datos);
  }

  actualizar(id: number, datos: Partial<UsuarioAdminRequest>): Observable<UsuarioAdmin> {
    return this.http.put<UsuarioAdmin>(`${this.endpoint}/${id}`, datos);
  }
}
