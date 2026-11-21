import { HttpClient, HttpContext, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CargaService } from '../services/carga';
import { cargaInterceptor } from './carga-interceptor';
import { SIN_INDICADOR_CARGA } from './contexto';

describe('cargaInterceptor', () => {
  let http: HttpClient;
  let api: HttpTestingController;
  let carga: CargaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([cargaInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    api = TestBed.inject(HttpTestingController);
    carga = TestBed.inject(CargaService);
  });

  afterEach(() => api.verify());

  it('mantiene la barra activa mientras existan peticiones en curso', () => {
    http.get('/api/a').subscribe();
    http.get('/api/b').subscribe({ error: () => {} });
    expect(carga.activa()).toBe(true);

    api.expectOne('/api/a').flush({});
    expect(carga.activa()).toBe(true);

    api.expectOne('/api/b').flush(null, { status: 500, statusText: 'Error' });
    expect(carga.activa()).toBe(false);
  });

  it('ignora peticiones con SIN_INDICADOR_CARGA', () => {
    const context = new HttpContext().set(SIN_INDICADOR_CARGA, true);
    http.get('/api/categorias', { context }).subscribe();
    expect(carga.activa()).toBe(false);
    api.expectOne('/api/categorias').flush([]);
  });
});
