export type EstadoEjemplar = 'DISPONIBLE' | 'PRESTADO' | 'EN_TRASLADO';
export type TipoMovimiento = 'REGISTRO' | 'PRESTAMO' | 'DEVOLUCION' | 'TRASLADO' | string;

export interface Sede {
  id: number;
  nombre: string;
  direccion: string;
  ciudad?: string | null;
  telefono?: string | null;
  activa: boolean;
}

export interface SedeRequest {
  nombre: string;
  direccion: string;
  ciudad?: string | null;
  telefono?: string | null;
}

export interface Categoria {
  id: number;
  nombre: string;
  descripcion?: string | null;
  categoriaPadreId: number | null;
  categoriaPadreNombre?: string | null;
}

export interface CategoriaRequest {
  nombre: string;
  descripcion?: string | null;
  categoriaPadreId?: number | null;
}

export interface Libro {
  id: number;
  titulo: string;
  autor: string;
  isbn: string;
  editorial?: string | null;
  anioPublicacion?: number | null;
  descripcion?: string | null;
  categoriaId?: number | null;
  categoriaNombre?: string | null;
}

export interface LibroRequest {
  titulo: string;
  autor: string;
  isbn: string;
  editorial?: string | null;
  anioPublicacion?: number | null;
  descripcion?: string | null;
  categoriaId?: number | null;
}

export interface Ejemplar {
  id: number;
  codigo: string;
  estado: EstadoEjemplar;
  libroId: number;
  libroTitulo?: string;
  sedePropietariaId: number;
  sedeActualId: number;
  sedeActualNombre?: string;
}

export interface EjemplarRequest {
  codigo?: string | null;
  libroId: number;
  sedeId: number;
}

export interface MovimientoEjemplar {
  id: number;
  tipo: TipoMovimiento;
  fecha: string;
  sedeOrigenNombre?: string | null;
  sedeDestinoNombre?: string | null;
  observaciones?: string | null;
}

export interface Cliente {
  id: number;
  nombre: string;
  documento: string;
  email: string;
  telefono?: string | null;
  direccion?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface ClienteRequest {
  nombre: string;
  documento: string;
  email: string;
  telefono?: string | null;
  direccion?: string | null;
}
