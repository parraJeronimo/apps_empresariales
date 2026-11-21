import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse, toPaginatedResponse } from '../models/api.model';
import { Ejemplar, EjemplarRequest, MovimientoEjemplar } from '../models/gestion.model';

@Service()
export class EjemplarService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/ejemplares`;

  listarPaginado(page = 0, size = 10): Observable<PaginatedResponse<Ejemplar>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<Ejemplar[]>(this.endpoint, { params, observe: 'response' })
      .pipe(map((res) => toPaginatedResponse(res, page, size)));
  }

  porSede(sedeId: number): Observable<Ejemplar[]> {
    return this.http.get<Ejemplar[]>(`${this.endpoint}/sede/${sedeId}`);
  }

  historial(id: number): Observable<MovimientoEjemplar[]> {
    return this.http.get<MovimientoEjemplar[]>(`${this.endpoint}/${id}/historial`);
  }

  crear(datos: EjemplarRequest): Observable<Ejemplar> {
    return this.http.post<Ejemplar>(this.endpoint, datos);
  }
}
