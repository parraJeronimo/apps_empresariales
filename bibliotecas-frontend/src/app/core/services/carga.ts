import { computed, Service, signal } from '@angular/core';

@Service()
export class CargaService {
  private readonly enCurso = signal(0);
  readonly activa = computed(() => this.enCurso() > 0);

  iniciar(): void {
    this.enCurso.update((n) => n + 1);
  }

  terminar(): void {
    this.enCurso.update((n) => Math.max(0, n - 1));
  }
}
