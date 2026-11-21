import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { email, form, FormField as SignalFormField, FormRoot, ReadonlyFieldState, required } from '@angular/forms/signals';
import { PageMeta } from '../../../../core/models/api.model';
import { Sede } from '../../../../core/models/gestion.model';
import { UsuarioAdmin } from '../../../../core/models/operacion.model';
import { RolUsuario } from '../../../../core/services/auth';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { SedeService } from '../../../../core/services/sede';
import { UsuarioAdminService } from '../../../../core/services/usuario-admin';
import { mensajeDeError } from '../../../../core/utils/errores';
import { Badge } from '../../../../shared/ui/atoms/badge/badge';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { EmptyState } from '../../../../shared/ui/molecules/empty-state/empty-state';
import { FormField } from '../../../../shared/ui/molecules/form-field/form-field';
import { Modal } from '../../../../shared/ui/molecules/modal/modal';
import { Paginator } from '../../../../shared/ui/molecules/paginator/paginator';
import { PageHeader } from '../../../../shared/ui/organisms/page-header/page-header';

@Component({
  selector: 'app-usuarios-admin-page',
  imports: [SignalFormField, FormRoot, PageHeader, Button, Badge, Paginator, EmptyState, Modal, FormField],
  template: `
    <app-page-header titulo="Usuarios Administrativos" subtitulo="Gestión de administradores y bibliotecarios por sede">
      <app-button texto="+ Nuevo usuario" (presionado)="abrirCrear()" />
    </app-page-header>

    @if (usuarios().length === 0) {
      <app-empty-state titulo="No hay usuarios administrativos" />
    } @else {
      <div class="tabla-contenedor">
        <table class="tabla">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Email</th>
              <th>Rol</th>
              <th>Sede ID</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (u of usuarios(); track u.id) {
              <tr>
                <td><strong>{{ u.nombre }}</strong></td>
                <td>{{ u.email }}</td>
                <td><app-badge [texto]="u.rol" variante="info" /></td>
                <td>{{ u.sedeId ?? 'Global' }}</td>
                <td class="tabla__acciones">
                  <app-button texto="Editar" variante="secundario" (presionado)="abrirEditar(u)" />
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>

      <app-paginator
        [(pagina)]="pagina"
        [totalPaginas]="meta().totalPages"
        [totalElementos]="meta().totalElements"
        etiqueta="usuarios"
      />
    }

    <app-modal [titulo]="editando() ? 'Editar usuario' : 'Nuevo usuario administrativo'" [(abierto)]="modalAbierto">
      <form id="usrForm" [formRoot]="formulario" class="panel-form panel-form--modal">
        @if (errorForm()) {
          <p class="mensaje-error" role="alert">{{ errorForm() }}</p>
        }
        <app-form-field etiqueta="Nombre" para="u-nom" [obligatorio]="true" [error]="errorDe(formulario.nombre())">
          <input id="u-nom" class="campo" [formField]="formulario.nombre" />
        </app-form-field>
        <app-form-field etiqueta="Email" para="u-email" [obligatorio]="true" [error]="errorDe(formulario.email())">
          <input id="u-email" type="email" class="campo" [formField]="formulario.email" />
        </app-form-field>
        @if (!editando()) {
          <app-form-field etiqueta="Contraseña" para="u-pass">
            <input id="u-pass" type="password" class="campo" [formField]="formulario.password" />
          </app-form-field>
        }
        <app-form-field etiqueta="Rol" para="u-rol" [obligatorio]="true">
          <select id="u-rol" class="campo" [formField]="formulario.rol">
            @for (rol of roles; track rol) {
              <option [value]="rol">{{ rol }}</option>
            }
          </select>
        </app-form-field>
        <app-form-field etiqueta="Sede (obligatoria salvo Admin General)" para="u-sede">
          <select id="u-sede" class="campo" [formField]="formulario.sedeId">
            <option value="">Ninguna (Global)</option>
            @for (s of sedes(); track s.id) {
              <option [value]="s.id">{{ s.nombre }}</option>
            }
          </select>
        </app-form-field>
      </form>
      <ng-container modalPie>
        <app-button texto="Cancelar" variante="secundario" (presionado)="modalAbierto.set(false)" />
        <app-button tipo="submit" formId="usrForm" texto="Guardar" [cargando]="guardando()" />
      </ng-container>
    </app-modal>
  `,
})
export class UsuariosAdminPage {
  private readonly usuarioService = inject(UsuarioAdminService);
  private readonly sedeService = inject(SedeService);
  private readonly notificaciones = inject(NotificacionesService);

  protected readonly roles: RolUsuario[] = ['ADMINISTRADOR_GENERAL', 'ADMINISTRADOR_SEDE', 'BIBLIOTECARIO'];
  protected readonly sedes = toSignal(this.sedeService.listarTodas(), { initialValue: [] as Sede[] });
  protected readonly usuarios = signal<UsuarioAdmin[]>([]);
  protected readonly pagina = signal(0);
  protected readonly meta = signal<PageMeta>({ totalElements: 0, totalPages: 1, number: 0, size: 10 });

  protected readonly modalAbierto = signal(false);
  protected readonly editando = signal<UsuarioAdmin | null>(null);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);

  protected readonly modelo = signal({
    nombre: '',
    email: '',
    password: '',
    rol: 'BIBLIOTECARIO' as RolUsuario,
    sedeId: '',
  });

  protected readonly formulario = form(
    this.modelo,
    (p) => {
      required(p.nombre, { message: 'El nombre es obligatorio' });
      required(p.email, { message: 'El email es obligatorio' });
      email(p.email, { message: 'Email inválido' });
    },
    { submission: { action: async () => this.guardar() } },
  );

  constructor() {
    effect(() => this.cargar(this.pagina()));
  }

  private cargar(page = this.pagina()): void {
    this.usuarioService.listarPaginado(page, 10).subscribe((res) => {
      this.usuarios.set(res.items);
      this.meta.set(res.page);
    });
  }

  protected abrirCrear(): void {
    this.editando.set(null);
    this.errorForm.set(null);
    this.modelo.set({ nombre: '', email: '', password: '', rol: 'BIBLIOTECARIO', sedeId: '' });
    this.modalAbierto.set(true);
  }

  protected abrirEditar(u: UsuarioAdmin): void {
    this.editando.set(u);
    this.errorForm.set(null);
    this.modelo.set({
      nombre: u.nombre,
      email: u.email,
      password: '',
      rol: u.rol,
      sedeId: u.sedeId ? String(u.sedeId) : '',
    });
    this.modalAbierto.set(true);
  }

  protected errorDe(campo: ReadonlyFieldState<unknown>): string | null {
    return campo.touched() ? (campo.errors()[0]?.message ?? null) : null;
  }

  private guardar(): void {
    this.guardando.set(true);
    const v = this.modelo();
    const payload = {
      nombre: v.nombre.trim(),
      email: v.email.trim(),
      ...(v.password ? { password: v.password } : {}),
      rol: v.rol,
      sedeId: v.sedeId ? Number(v.sedeId) : null,
    };

    const actual = this.editando();
    const req$ = actual
      ? this.usuarioService.actualizar(actual.id, payload)
      : this.usuarioService.crear(payload);

    req$.subscribe({
      next: () => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.notificaciones.mostrar('Usuario administrativo guardado.', 'exito');
        this.cargar();
      },
      error: (e: HttpErrorResponse) => {
        this.guardando.set(false);
        this.errorForm.set(mensajeDeError(e));
      },
    });
  }
}
