import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { LibroSolicitado, ReportePrestamos } from '../../../../core/models/operacion.model';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { ReporteService } from '../../../../core/services/reporte';
import { mensajeDeError } from '../../../../core/utils/errores';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { PageHeader } from '../../../../shared/ui/organisms/page-header/page-header';

@Component({
  selector: 'app-reportes-page',
  imports: [PageHeader, Button],
  template: `
    <app-page-header titulo="Reportes" subtitulo="Indicadores de circulación, inventario y movimientos entre sedes" />

    <div class="barra-filtros">
      <label>Desde <input type="date" class="campo" [value]="desde()" (change)="desde.set($any($event.target).value)" /></label>
      <label>Hasta <input type="date" class="campo" [value]="hasta()" (change)="hasta.set($any($event.target).value)" /></label>
      <app-button texto="Generar reportes" (presionado)="cargarReportes()" />
    </div>

    @if (prestamos(); as rp) {
      <div class="stats-grid">
        <div class="stat-box"><span>Total préstamos</span><strong>{{ rp.totalPrestamos }}</strong></div>
        <div class="stat-box"><span>Finalizados</span><strong>{{ rp.finalizados }}</strong></div>
        <div class="stat-box"><span>Activos</span><strong>{{ rp.activos ?? 0 }}</strong></div>
        <div class="stat-box"><span>Vencidos</span><strong style="color:var(--color-error)">{{ rp.vencidos ?? 0 }}</strong></div>
      </div>
    }

    <section style="margin-top: var(--space-6)">
      <h2>Libros más solicitados</h2>
      <div class="tabla-contenedor" style="margin-top: var(--space-3)">
        <table class="tabla">
          <thead>
            <tr>
              <th>#</th>
              <th>Título</th>
              <th>Total préstamos</th>
            </tr>
          </thead>
          <tbody>
            @for (item of ranking(); track item.libroId; let i = $index) {
              <tr>
                <td><strong>#{{ i + 1 }}</strong></td>
                <td>{{ item.titulo }}</td>
                <td>{{ item.totalPrestamos }}</td>
              </tr>
            } @empty {
              <tr><td colspan="3">Sin datos de préstamos en el período.</td></tr>
            }
          </tbody>
        </table>
      </div>
    </section>
  `,
  styles: `
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
      gap: var(--space-4);
    }
    .stat-box {
      background: var(--color-superficie);
      border: 1px solid var(--color-borde);
      border-radius: var(--radio-lg);
      padding: var(--space-4);
      display: flex;
      flex-direction: column;
      gap: var(--space-1);
      span { font-size: 13px; color: var(--color-texto-suave); }
      strong { font-size: 28px; font-family: var(--font-display); }
    }
  `,
})
export class ReportesPage implements OnInit {
  private readonly reporteService = inject(ReporteService);
  private readonly notificaciones = inject(NotificacionesService);

  protected readonly desde = signal(`${new Date().getFullYear()}-01-01`);
  protected readonly hasta = signal(`${new Date().getFullYear()}-12-31`);
  protected readonly prestamos = signal<ReportePrestamos | null>(null);
  protected readonly ranking = signal<LibroSolicitado[]>([]);

  ngOnInit(): void {
    this.cargarReportes();
  }

  protected cargarReportes(): void {
    this.reporteService.prestamos(this.desde(), this.hasta()).subscribe({
      next: (data) => this.prestamos.set(data),
      error: (e: HttpErrorResponse) => this.notificaciones.mostrar(mensajeDeError(e), 'error'),
    });

    this.reporteService.librosMasSolicitados(10).subscribe({
      next: (list) => this.ranking.set(list),
    });
  }
}
