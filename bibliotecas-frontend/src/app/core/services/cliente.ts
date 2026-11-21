import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse, toPaginatedResponse } from '../models/api.model';
import { Cliente, ClienteRequest } from '../models/gestion.model';

@Service()
export class ClienteService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/clientes`;

  listarPaginado(page = 0, size = 10): Observable<PaginatedResponse<Cliente>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<Cliente[]>(this.endpoint, { params, observe: 'response' })
      .pipe(map((res) => toPaginatedResponse(res, page, size)));
  }

  porId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.endpoint}/${id}`);
  }

  porDocumento(documento: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.endpoint}/documento/${encodeURIComponent(documento)}`);
  }

  crear(datos: ClienteRequest): Observable<Cliente> {
    return this.http.post<Cliente>(this.endpoint, datos);
  }

  actualizar(id: number, datos: Partial<ClienteRequest>): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.endpoint}/${id}`, datos);
  }
}
