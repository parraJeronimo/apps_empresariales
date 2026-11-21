import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LibroSolicitado, ReportePrestamos } from '../models/operacion.model';

@Service()
export class ReporteService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/reportes`;

  prestamos(desde: string, hasta: string): Observable<ReportePrestamos> {
    const params = new HttpParams().set('desde', desde).set('hasta', hasta);
    return this.http.get<ReportePrestamos>(`${this.endpoint}/prestamos`, { params });
  }

  devoluciones(desde: string, hasta: string): Observable<Record<string, unknown>> {
    const params = new HttpParams().set('desde', desde).set('hasta', hasta);
    return this.http.get<Record<string, unknown>>(`${this.endpoint}/devoluciones`, { params });
  }

  libros(): Observable<Record<string, unknown>> {
    return this.http.get<Record<string, unknown>>(`${this.endpoint}/libros`);
  }

  clientes(): Observable<Record<string, unknown>> {
    return this.http.get<Record<string, unknown>>(`${this.endpoint}/clientes`);
  }

  movimientosSedes(desde: string, hasta: string): Observable<Record<string, unknown>> {
    const params = new HttpParams().set('desde', desde).set('hasta', hasta);
    return this.http.get<Record<string, unknown>>(`${this.endpoint}/movimientos-sedes`, { params });
  }

  librosMasSolicitados(limite = 10): Observable<LibroSolicitado[]> {
    const params = new HttpParams().set('limite', limite);
    return this.http.get<LibroSolicitado[]>(`${this.endpoint}/libros-mas-solicitados`, { params });
  }
}
