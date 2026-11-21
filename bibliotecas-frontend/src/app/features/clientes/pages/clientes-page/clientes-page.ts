import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, signal } from '@angular/core';
import { email, form, FormField as SignalFormField, FormRoot, ReadonlyFieldState, required } from '@angular/forms/signals';
import { PageMeta } from '../../../../core/models/api.model';
import { Cliente } from '../../../../core/models/gestion.model';
import { ClienteService } from '../../../../core/services/cliente';
import { NotificacionesService } from '../../../../core/services/notificaciones';
import { mensajeDeError } from '../../../../core/utils/errores';
import { Button } from '../../../../shared/ui/atoms/button/button';
import { EmptyState } from '../../../../shared/ui/molecules/empty-state/empty-state';
import { FormField } from '../../../../shared/ui/molecules/form-field/form-field';
import { Modal } from '../../../../shared/ui/molecules/modal/modal';
import { Paginator } from '../../../../shared/ui/molecules/paginator/paginator';
import { PageHeader } from '../../../../shared/ui/organisms/page-header/page-header';

@Component({
  selector: 'app-clientes-page',
  imports: [SignalFormField, FormRoot, PageHeader, Button, Paginator, EmptyState, Modal, FormField],
  template: `
    <app-page-header titulo="Clientes" subtitulo="Lectores registrados en la red de bibliotecas">
      <app-button texto="+ Nuevo cliente" (presionado)="abrirCrear()" />
    </app-page-header>

    <div class="barra-filtros">
      <input
        type="search"
        class="campo"
        style="max-width: 260px"
        placeholder="Buscar por documento..."
        [value]="docBusqueda()"
        (input)="docBusqueda.set($any($event.target).value)"
      />
      <app-button texto="Buscar" variante="secundario" (presionado)="buscarPorDocumento()" />
      @if (docBusqueda()) {
        <app-button texto="Limpiar" variante="secundario" (presionado)="limpiarBusqueda()" />
      }
    </div>

    @if (clientes().length === 0) {
      <app-empty-state titulo="No hay clientes para mostrar" />
    } @else {
      <div class="tabla-contenedor">
        <table class="tabla">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Documento</th>
              <th>Email</th>
              <th>Teléfono</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (c of clientes(); track c.id) {
              <tr>
                <td><strong>{{ c.nombre }}</strong></td>
                <td>{{ c.documento }}</td>
                <td>{{ c.email }}</td>
                <td>{{ c.telefono || '—' }}</td>
                <td class="tabla__acciones">
                  <app-button texto="Editar" variante="secundario" (presionado)="abrirEditar(c)" />
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
        etiqueta="clientes"
      />
    }

    <app-modal [titulo]="clienteEditando() ? 'Editar cliente' : 'Nuevo cliente'" [(abierto)]="modalAbierto">
      <form id="clForm" [formRoot]="formulario" class="panel-form panel-form--modal">
        @if (errorForm()) {
          <p class="mensaje-error" role="alert">{{ errorForm() }}</p>
        }
        <app-form-field etiqueta="Nombre completo" para="cl-nom" [obligatorio]="true" [error]="errorDe(formulario.nombre())">
          <input id="cl-nom" class="campo" [formField]="formulario.nombre" />
        </app-form-field>
        <app-form-field etiqueta="Documento" para="cl-doc" [obligatorio]="true" [error]="errorDe(formulario.documento())">
          <input id="cl-doc" class="campo" [formField]="formulario.documento" />
        </app-form-field>
        <app-form-field etiqueta="Correo electrónico" para="cl-email" [obligatorio]="true" [error]="errorDe(formulario.email())">
          <input id="cl-email" type="email" class="campo" [formField]="formulario.email" />
        </app-form-field>
        <app-form-field etiqueta="Teléfono" para="cl-tel">
          <input id="cl-tel" class="campo" [formField]="formulario.telefono" />
        </app-form-field>
        <app-form-field etiqueta="Dirección" para="cl-dir">
          <input id="cl-dir" class="campo" [formField]="formulario.direccion" />
        </app-form-field>
      </form>
      <ng-container modalPie>
        <app-button texto="Cancelar" variante="secundario" (presionado)="modalAbierto.set(false)" />
        <app-button tipo="submit" formId="clForm" texto="Guardar" [cargando]="guardando()" />
      </ng-container>
    </app-modal>
  `,
})
export class ClientesPage {
  private readonly clienteService = inject(ClienteService);
  private readonly notificaciones = inject(NotificacionesService);

  protected readonly clientes = signal<Cliente[]>([]);
  protected readonly pagina = signal(0);
  protected readonly meta = signal<PageMeta>({ totalElements: 0, totalPages: 1, number: 0, size: 10 });
  protected readonly docBusqueda = signal('');

  protected readonly modalAbierto = signal(false);
  protected readonly clienteEditando = signal<Cliente | null>(null);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);

  protected readonly modelo = signal({ nombre: '', documento: '', email: '', telefono: '', direccion: '' });
  protected readonly formulario = form(
    this.modelo,
    (p) => {
      required(p.nombre, { message: 'El nombre es obligatorio' });
      required(p.documento, { message: 'El documento es obligatorio' });
      required(p.email, { message: 'El email es obligatorio' });
      email(p.email, { message: 'Formato de email inválido' });
    },
    { submission: { action: async () => this.guardar() } },
  );

  constructor() {
    effect(() => this.cargarClientes(this.pagina()));
  }

  protected cargarClientes(page = this.pagina()): void {
    this.clienteService.listarPaginado(page, 10).subscribe((res) => {
      this.clientes.set(res.items);
      this.meta.set(res.page);
    });
  }

  protected buscarPorDocumento(): void {
    const doc = this.docBusqueda().trim();
    if (!doc) return;
    this.clienteService.porDocumento(doc).subscribe({
      next: (cliente) => {
        this.clientes.set([cliente]);
        this.meta.set({ totalElements: 1, totalPages: 1, number: 0, size: 10 });
      },
      error: (e: HttpErrorResponse) => {
        this.clientes.set([]);
        this.notificaciones.mostrar(mensajeDeError(e), 'advertencia');
      },
    });
  }

  protected limpiarBusqueda(): void {
    this.docBusqueda.set('');
    this.cargarClientes(0);
  }

  protected abrirCrear(): void {
    this.clienteEditando.set(null);
    this.errorForm.set(null);
    this.modelo.set({ nombre: '', documento: '', email: '', telefono: '', direccion: '' });
    this.modalAbierto.set(true);
  }

  protected abrirEditar(c: Cliente): void {
    this.clienteEditando.set(c);
    this.errorForm.set(null);
    this.modelo.set({
      nombre: c.nombre,
      documento: c.documento,
      email: c.email,
      telefono: c.telefono ?? '',
      direccion: c.direccion ?? '',
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
      documento: v.documento.trim(),
      email: v.email.trim(),
      telefono: v.telefono.trim() || null,
      direccion: v.direccion.trim() || null,
    };

    const editando = this.clienteEditando();
    const req$ = editando
      ? this.clienteService.actualizar(editando.id, payload)
      : this.clienteService.crear(payload);

    req$.subscribe({
      next: () => {
        this.guardando.set(false);
        this.modalAbierto.set(false);
        this.notificaciones.mostrar('Cliente guardado correctamente.', 'exito');
        this.cargarClientes();
      },
      error: (e: HttpErrorResponse) => {
        this.guardando.set(false);
        this.errorForm.set(mensajeDeError(e));
      },
    });
  }
}
