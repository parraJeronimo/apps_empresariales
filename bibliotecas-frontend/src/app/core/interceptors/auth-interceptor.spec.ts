import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { vi } from 'vitest';
import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth';
import { authInterceptor } from './auth-interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let api: HttpTestingController;
  let auth: AuthService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    api = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => {
    api.verify();
    localStorage.clear();
  });

  it('adjunta Authorization Bearer cuando hay sesión y es nuestra API', () => {
    auth.establecerSesion({
      token: 'jwt-test',
      nombre: 'Laura',
      email: 'laura@biblioteca.com',
      rol: 'ADMINISTRADOR_GENERAL',
      sedeId: null,
    });

    http.get(`${environment.apiUrl}/sedes`).subscribe();
    const req = api.expectOne(`${environment.apiUrl}/sedes`);
    expect(req.request.headers.get('Authorization')).toBe('Bearer jwt-test');
    req.flush([]);
  });

  it('cierra sesión y navega a /auth/login ante un 401 con token activo', () => {
    auth.establecerSesion({
      token: 'jwt-vencido',
      nombre: 'Laura',
      email: 'laura@biblioteca.com',
      rol: 'ADMINISTRADOR_GENERAL',
      sedeId: null,
    });
    const navSpy = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    let recibioError = false;
    http.get(`${environment.apiUrl}/sedes`).subscribe({ error: () => (recibioError = true) });
    api.expectOne(`${environment.apiUrl}/sedes`).flush(
      { message: 'Token inválido' },
      { status: 401, statusText: 'Unauthorized' },
    );

    expect(auth.autenticado()).toBe(false);
    expect(navSpy).toHaveBeenCalledWith(['/auth/login'], { queryParams: { redirect: '/' } });
    expect(recibioError).toBe(true);
  });
});
