import { RolUsuario } from '../services/auth';

export type EstadoPrestamo = 'ACTIVO' | 'VENCIDO' | 'FINALIZADO';
export type EstadoReserva = 'PENDIENTE' | 'APROBADA' | 'RECHAZADA' | 'CANCELADA';
export type EstadoTraslado = 'EN_TRANSITO' | 'RECIBIDO' | 'CANCELADO';

export interface Prestamo {
  id: number;
  ejemplarId: number;
  ejemplarCodigo?: string;
  libroTitulo?: string;
  clienteId: number;
  clienteNombre?: string;
  sedeId: number;
  sedeDevolucionId?: number | null;
  estado: EstadoPrestamo;
  fechaInicio: string;
  fechaLimite: string;
  fechaDevolucion?: string | null;
  vencido: boolean;
  devueltoConRetraso?: boolean;
}

export interface PrestamoCreateRequest {
  ejemplarId: number;
  clienteId: number;
  sedeId: number;
  diasPrestamo?: number;
}

export interface Reserva {
  id: number;
  clienteId: number;
  libroId: number;
  sedeId: number;
  estado: EstadoReserva;
  motivoRechazo?: string | null;
  fechaSolicitud?: string;
}

export interface ReservaCreateRequest {
  clienteId: number;
  libroId: number;
  sedeId: number;
}

export interface Traslado {
  id: number;
  ejemplarId: number;
  sedeOrigenId?: number;
  sedeDestinoId: number;
  estado: EstadoTraslado;
  observaciones?: string | null;
  fechaRecepcion?: string | null;
}

export interface TrasladoCreateRequest {
  ejemplarId: number;
  sedeDestinoId: number;
  observaciones?: string | null;
}

export interface UsuarioAdmin {
  id: number;
  nombre: string;
  email: string;
  rol: RolUsuario;
  sedeId: number | null;
}

export interface UsuarioAdminRequest {
  nombre: string;
  email: string;
  password?: string;
  rol: RolUsuario;
  sedeId: number | null;
}

export interface ReportePrestamos {
  desde: string;
  hasta: string;
  totalPrestamos: number;
  activos?: number;
  vencidos?: number;
  finalizados: number;
  prestamos: Prestamo[];
}

export interface LibroSolicitado {
  libroId: number;
  titulo: string;
  autor?: string;
  totalPrestamos: number;
}
