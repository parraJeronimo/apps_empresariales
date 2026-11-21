import { HttpClient, HttpContext, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { NotificacionesService } from '../services/notificaciones';
import { SIN_NOTIFICACION_ERROR } from './contexto';
import { erroresInterceptor } from './errores-interceptor';

describe('erroresInterceptor', () => {
  let http: HttpClient;
  let api: HttpTestingController;
  let notificaciones: NotificacionesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([erroresInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    api = TestBed.inject(HttpTestingController);
    notificaciones = TestBed.inject(NotificacionesService);
  });

  afterEach(() => api.verify());

  it('notifica errores 500', () => {
    http.get('/api/sedes').subscribe({ error: () => {} });
    api.expectOne('/api/sedes').flush(null, { status: 500, statusText: 'Server Error' });
    expect(notificaciones.notificaciones().length).toBe(1);
    expect(notificaciones.notificaciones()[0].texto).toContain('500');
  });

  it('no notifica errores 400 de negocio y propaga el error', () => {
    let status = 0;
    http.post('/api/sedes', {}).subscribe({ error: (e) => (status = e.status) });
    api.expectOne('/api/sedes').flush({ message: 'Inválido' }, { status: 400, statusText: 'Bad Request' });
    expect(notificaciones.notificaciones().length).toBe(0);
    expect(status).toBe(400);
  });

  it('respeta SIN_NOTIFICACION_ERROR', () => {
    const context = new HttpContext().set(SIN_NOTIFICACION_ERROR, true);
    http.get('/api/categorias', { context }).subscribe({ error: () => {} });
    api.expectOne('/api/categorias').flush(null, { status: 503, statusText: 'Unavailable' });
    expect(notificaciones.notificaciones().length).toBe(0);
  });
});
