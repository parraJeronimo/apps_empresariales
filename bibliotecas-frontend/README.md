# Bibliotecas Global — Frontend (Angular 22)

Aplicación SPA en **Angular 22** (Standalone, Zoneless, CSR) organizada por **Features** y **Atomic Design** para el Sistema de Gestión de Múltiples Bibliotecas (`bibliotecas-api`).

## Requisitos
- Node.js 22 o superior
- Angular CLI 22 (`npm install -g @angular/cli@22`)
- Backend Spring Boot ejecutándose en `http://localhost:8080/bibliotecas/api/v1`

## Instalación y ejecución
```bash
npm install
npm start
# Abrir http://localhost:4200
```

Si prefieres usar el proxy de desarrollo de Angular:
```bash
npm run start:proxy
```

## Scripts disponibles
- `npm start`: Servidor de desarrollo con `environment.development.ts`.
- `npm run start:proxy`: Servidor de desarrollo usando `proxy.conf.json`.
- `npm run build`: Build de producción en `dist/` usando `environment.ts`.
- `npm test`: Ejecuta las pruebas unitarias con Vitest (`ng test --watch=false`).

## Arquitectura del proyecto
- `src/app/core/`: Modelos TypeScript, utilidades, servicios `@Service()`, guards funcionales (`authGuard`, `guestGuard`, `roleGuard`) e interceptores HTTP (`registro`, `carga`, `auth`, `errores`).
- `src/app/shared/ui/`: Componentes de presentación puros organizados con Atomic Design (`atoms`, `molecules`, `organisms`, `templates`).
- `src/app/features/`: Módulos de negocio cargados con lazy loading (`auth`, `sedes`, `categorias`, `libros`, `ejemplares`, `clientes`, `prestamos`, `reservas`, `traslados`, `usuarios-admin`, `reportes`, `errores`).

## Paginación por Headers HTTP
Los endpoints `GET` paginados del backend conservan el arreglo JSON en el cuerpo y envían los metadatos en los headers:
- `X-Total-Count`
- `X-Total-Pages`
- `X-Page-Number`
- `X-Page-Size`

La utilidad `toPaginatedResponse()` en `src/app/core/models/api.model.ts` extrae estos headers automáticamente para alimentar a la molécula `Paginator`.
