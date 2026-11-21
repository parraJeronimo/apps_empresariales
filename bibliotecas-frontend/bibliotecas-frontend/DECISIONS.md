# Registro de Decisiones de Arquitectura (ADR)

| ADR | Decisión | Contexto y Justificación |
| :--- | :--- | :--- |
| **ADR-01** | Angular 22 CSR + Zoneless + `@Service()` | Aplicación de gestión detrás de login JWT; CSR evita problemas de `localStorage` en SSR y simplifica el despliegue estático en Nginx. |
| **ADR-02** | Paginación por Headers HTTP (`X-Total-Count`, `X-Total-Pages`, `X-Page-Number`, `X-Page-Size`) | Permite paginar en el servidor manteniendo el cuerpo JSON como `T[]`, preservando compatibilidad con los 89 casos del Plan de Pruebas. |
| **ADR-03** | Cadena de Interceptores `[registro, carga, auth, errores]` | `registro` mide el tiempo total sin exponer el JWT; `carga` apaga el indicador con `finalize`; `auth` inyecta Bearer y maneja 401; `errores` notifica fallas 0, 403 y 5xx sin tragarse los errores de negocio (400, 404, 409). |
| **ADR-04** | Atomic Design estricto en `shared/ui/` | Los átomos, moléculas y organismos son 100% presentacionales (solo `input()`, `output()`, `model()`). Únicamente las páginas y `MainLayout` inyectan servicios. |
| **ADR-05** | Signal Forms (`@angular/forms/signals`) | Estables en Angular 22; reducen boilerplate de `FormGroup`, sincronizan atributos HTML desde el esquema y trabajan nativamente con `signal` y `linkedSignal`. |
