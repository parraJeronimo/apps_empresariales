import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginatedResponse, toPaginatedResponse } from '../models/api.model';
import { EstadoReserva, Reserva, ReservaCreateRequest } from '../models/operacion.model';

@Service()
export class ReservaService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/reservas`;

  listarPaginado(page = 0, size = 10): Observable<PaginatedResponse<Reserva>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http
      .get<Reserva[]>(this.endpoint, { params, observe: 'response' })
      .pipe(map((res) => toPaginatedResponse(res, page, size)));
  }

  porSede(sedeId: number, estado?: EstadoReserva): Observable<Reserva[]> {
    let params = new HttpParams();
    if (estado) params = params.set('estado', estado);
    return this.http.get<Reserva[]>(`${this.endpoint}/sede/${sedeId}`, { params });
  }

  crear(datos: ReservaCreateRequest): Observable<Reserva> {
    return this.http.post<Reserva>(this.endpoint, datos);
  }

  aprobar(id: number): Observable<Reserva> {
    return this.http.patch<Reserva>(`${this.endpoint}/${id}/aprobar`, {});
  }

  rechazar(id: number, motivo: string): Observable<Reserva> {
    return this.http.patch<Reserva>(`${this.endpoint}/${id}/rechazar`, { motivo });
  }

  cancelar(id: number): Observable<Reserva> {
    return this.http.patch<Reserva>(`${this.endpoint}/${id}/cancelar`, {});
  }
}
