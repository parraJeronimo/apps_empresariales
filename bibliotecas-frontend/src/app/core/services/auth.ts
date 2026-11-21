import { HttpClient } from '@angular/common/http';
import { computed, inject, Service, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export type RolUsuario = 'ADMINISTRADOR_GENERAL' | 'ADMINISTRADOR_SEDE' | 'BIBLIOTECARIO';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  nombre: string;
  email: string;
  rol: RolUsuario;
  sedeId: number | null;
}

const STORAGE_KEY = 'bibliotecas_sesion';

function leerSesion(): AuthResponse | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as AuthResponse) : null;
  } catch {
    return null;
  }
}

@Service()
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = `${environment.apiUrl}/auth`;

  private readonly sesionActual = signal<AuthResponse | null>(leerSesion());

  readonly usuario = this.sesionActual.asReadonly();
  readonly autenticado = computed(() => this.sesionActual() !== null);
  readonly token = computed(() => this.sesionActual()?.token ?? null);
  readonly rol = computed(() => this.sesionActual()?.rol ?? null);
  readonly sedeId = computed(() => this.sesionActual()?.sedeId ?? null);

  login(credenciales: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.endpoint}/login`, credenciales).pipe(
      tap((respuesta) => this.establecerSesion(respuesta)),
    );
  }

  establecerSesion(sesion: AuthResponse): void {
    this.sesionActual.set(sesion);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(sesion));
  }

  cerrarSesion(): void {
    this.sesionActual.set(null);
    localStorage.removeItem(STORAGE_KEY);
  }

  tieneRol(rolesPermitidos: RolUsuario[]): boolean {
    const rolActual = this.rol();
    return rolActual !== null && rolesPermitidos.includes(rolActual);
  }
}
