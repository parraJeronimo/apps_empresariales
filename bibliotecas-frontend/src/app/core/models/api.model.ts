import { HttpResponse } from '@angular/common/http';

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  detalles: string[] | null;
}

export interface PageMeta {
  totalElements: number;
  totalPages: number;
  number: number; // base 0
  size: number;
}

export interface PaginatedResponse<T> {
  items: T[];
  page: PageMeta;
}

export function toPaginatedResponse<T>(
  response: HttpResponse<T[]>,
  fallbackPage = 0,
  fallbackSize = 10,
): PaginatedResponse<T> {
  const items = response.body ?? [];
  const headers = response.headers;
  const totalElements = Number(headers.get('X-Total-Count') ?? items.length);
  const size = Number(headers.get('X-Page-Size') ?? fallbackSize);
  const totalPages = Number(
    headers.get('X-Total-Pages') ?? Math.max(1, Math.ceil(totalElements / Math.max(1, size))),
  );
  const number = Number(headers.get('X-Page-Number') ?? fallbackPage);

  return {
    items,
    page: { totalElements, totalPages, number, size },
  };
}
