# Reporte de Brechas y Alcance del Backend

## Brechas resueltas antes de Fase 3
1. **Seguridad JWT y CORS**: Implementado en `SecurityConfig`, `AuthController`, `JwtService` y `JwtAuthenticationFilter` con exposición de headers `Location` y `X-*`.
2. **Paginación opcional**: Implementada mediante parámetros `page`/`size` y metadatos en headers HTTP sin romper los 89 casos del Plan de Pruebas.

## Funcionalidades fuera de alcance en v1
Las siguientes pantallas aparecen en el Mapa de Navegación / User Flow inicial pero no tienen endpoints implementados en las 19 historias de usuario (HU-01 a HU-19) del backend:
- **Multas y pagos**: Sin controlador ni DTOs en `bibliotecas-api`.
- **Copias de seguridad / restauración**: Sin endpoints administrativos.
- **Renovación de préstamos**: Sin endpoint dedicado.
- **Registro público y recuperación de contraseña**: El acceso actual se realiza mediante `POST /api/v1/auth/login` con usuarios administrativos.

*Recomendación:* Mantener estas funcionalidades para una fase v2 una vez se definan los endpoints y reglas de negocio en Spring Boot.
