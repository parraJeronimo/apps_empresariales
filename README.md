# Sistema de Gestion de Multiples Bibliotecas

API REST (Spring Boot 3.5 / Java 21) para administrar una red de bibliotecas con varias sedes: catalogo de libros y
ejemplares, clientes, prestamos, devoluciones en cualquier sede, reservas, traslados entre sedes, usuarios
administrativos y reportes.

Este proyecto implementa los requerimientos funcionales (RF-01 a RF-25) y la **seguridad con Spring Security + JWT**
(RNF-01 a RNF-03) del documento `Requerimientos_Sistema_Gestion_Multiples_Bibliotecas.docx`. Los demas requerimientos
no funcionales (auditoria, respaldos, etc.) y los puntos "en analisis" quedan pendientes (ver el final de este documento).

## Estructura de carpetas

```
src/main/java/com/taller/bibliotecas
├── BibliotecasApplication.java
├── config/                 OpenApiConfig (Swagger + esquema Bearer), DataSeeder (administrador general inicial)
├── security/               Capa de seguridad: SecurityConfig, JwtService, JwtAuthenticationFilter,
│                           CustomUserDetailsService, JwtAuthenticationEntryPoint (401), JwtAccessDeniedHandler (403)
├── presentationLayer/
│   ├── controller/         Controladores REST (uno por modulo)
│   └── exception/          GlobalExceptionHandler, RecursoNoEncontradoException, SolicitudInvalidaException
├── businessLayer/
│   ├── dto/                DTOs Create / Update / respuesta y DTOs de reportes
│   └── service/
│       ├── *Service.java   Contratos de negocio
│       └── impl/           Reglas de negocio (@Service @Transactional)
└── persistenceLayer/
    ├── entity/             Entidades JPA y enums de estado
    ├── repository/         Spring Data JPA
    ├── mapper/             MapStruct (Entity <-> DTO)
    └── dao/                Unico punto que usa repositorios y mappers

src/test/java/com/taller/bibliotecas/businessLayer
└── *ServiceTest.java       Pruebas unitarias (JUnit 5 + Mockito) de cada servicio
```

## Modulos y requerimientos

| Modulo (ruta base `/api/v1/...`) | RF cubiertos |
|---|---|
| `sedes` | RF-01, RF-02 |
| `categorias` | RF-05 |
| `libros` (+ `/catalogo`, `/buscar`) | RF-03, RF-05, RF-06, RF-07 |
| `ejemplares` (+ `/{id}/historial`) | RF-04, RF-08, RF-20 |
| `clientes` | RF-09, RF-10 |
| `prestamos` (+ `/{id}/devolucion`, `/cliente/{id}`, `/cliente/{id}/historial`) | RF-11 a RF-17, RF-25 |
| `reservas` (+ `/sede/{id}`, `/{id}/aprobar`, `/rechazar`, `/cancelar`) | RF-21, RF-22 y gestion de solicitudes por sede |
| `traslados` (+ `/{id}/recepcion`, `/{id}/cancelar`) | RF-18, RF-19 |
| `auth` (`/login`, `/me`) | RNF-01, RNF-02 |
| `usuarios-admin` | RF-23 |
| `reportes` (+ `/libros-mas-solicitados`) | RF-24 y ranking de libros mas solicitados |

## Seguridad (Spring Security + JWT)

La API es **stateless**: el usuario inicia sesion, recibe un token JWT (HS256) y lo envia en cada peticion con el header
`Authorization: Bearer <token>`. Las contrasenas se guardan con BCrypt y nunca se devuelven en las respuestas.

```bash
# 1. Iniciar sesion (el DataSeeder crea admin@biblioteca.com / Admin12345 al arrancar)
curl -s -X POST http://localhost:8090/bibliotecas/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@biblioteca.com","password":"Admin12345"}'

# 2. Usar el token
curl http://localhost:8090/bibliotecas/api/v1/sedes -H "Authorization: Bearer <token>"
```

En Swagger UI: ejecutar `POST /api/v1/auth/login`, copiar `token`, pulsar **Authorize** y pegarlo (sin la palabra `Bearer`).

| Ruta | Quien puede |
|---|---|
| `POST /auth/login`, Swagger, `/error`, consola H2 (perfil test) | Publico |
| `/usuarios-admin/**`, `POST/PUT/PATCH/DELETE /sedes/**` | `ADMINISTRADOR_GENERAL` |
| `/reportes/**`, `DELETE /categorias/**`, `DELETE /libros/**` | `ADMINISTRADOR_GENERAL`, `ADMINISTRADOR_SEDE` |
| Resto de `/api/v1/**` (clientes, libros, ejemplares, prestamos, reservas, traslados...) | Cualquier usuario autenticado |

Respuestas de seguridad (mismo JSON `timestamp/status/error/message/detalles` del resto de la API):
`401` sin token, token invalido/expirado o credenciales incorrectas; `403` rol sin permiso o cuenta desactivada.
Un usuario desactivado pierde el acceso de inmediato aunque su token siga vigente (el filtro consulta la BD en cada peticion).

**Usuarios y contrasenas.** `POST /usuarios-admin` exige `password` (8 a 72 caracteres) y `PUT /usuarios-admin/{id}` permite
restablecerla enviando `password`. No hay registro publico: solo el administrador general crea usuarios. Los usuarios que
existian antes de agregar la seguridad no tienen contrasena y no pueden iniciar sesion hasta que se les asigne una.

**Variables de entorno** (se leen del sistema o de un archivo `.env` en la raiz, que no se sube a Git; plantilla en `.env.example`):

| Variable | Uso | Por defecto |
|---|---|---|
| `JWT_SECRET` | Clave HS256 en Base64 (minimo 32 bytes: `openssl rand -base64 32`) | clave de desarrollo; **obligatoria en `prod`** |
| `JWT_EXPIRATION` | Vigencia del token en ms | 3600000 (1 h); 900000 en `prod` |
| `CORS_ALLOWED_ORIGINS` | Frontends permitidos, separados por coma | `localhost:4200`, `5173` y `3000`; obligatoria en `prod` |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | Administrador general inicial | `admin@biblioteca.com` / `Admin12345`; en `prod` si no hay `ADMIN_PASSWORD` no se crea |
| `DB_USERNAME` / `DB_PASSWORD` | MySQL en `prod` | `root` / `changeme` |

## Paginacion opcional (headers)

Los `GET` de listado base (`/sedes`, `/categorias`, `/libros`, `/ejemplares`, `/clientes`, `/prestamos`, `/reservas`,
`/traslados`, `/usuarios-admin`) **siguen devolviendo un arreglo JSON** y solo paginan cuando se envia `page` y/o `size`.
Asi ningun caso existente del plan de pruebas cambia.

| Peticion | Resultado |
|---|---|
| `GET /sedes` | Arreglo completo, igual que siempre, **sin** headers de paginacion |
| `GET /sedes?page=0&size=10` | Arreglo con solo esa pagina + headers |
| `GET /sedes?size=10` | `page` vale 0 |
| `GET /sedes?page=2` | `size` vale 20 |

- `page` empieza en 0; `size` va de 1 a 100. Valores fuera de rango o no numericos responden `400` con el JSON estandar de errores.
- Las paginas se ordenan por `id` ascendente (estable: no repiten ni saltan elementos). Una pagina fuera de rango devuelve `[]` con `200`.
- La consulta se limita en la base de datos (`LIMIT/OFFSET`); no se carga la tabla completa en memoria.

Headers de la respuesta paginada (expuestos por CORS para que el navegador los pueda leer):

| Header | Contenido |
|---|---|
| `X-Total-Count` | Total de elementos del listado |
| `X-Total-Pages` | Total de paginas con el `size` pedido |
| `X-Page` | Pagina devuelta (desde 0) |
| `X-Page-Size` | Tamano de pagina aplicado |

Los listados auxiliares (`/sedes/activas`, `/categorias/raiz`, `/libros/catalogo`, `/libros/buscar`, `/prestamos/vencidos`,
`/*/sede/{id}`, `/*/cliente/{id}`) no cambian y devuelven siempre el arreglo completo.

## Reglas de negocio implementadas

- **Ejemplares**: nacen `DISPONIBLE` en la sede donde se registran (`sedePropietaria` = `sedeActual`). Si no se envia
  codigo, el sistema genera uno unico (`ISBN-001`, `ISBN-002`...). Estados: `DISPONIBLE`, `PRESTADO`, `EN_TRASLADO`.
- **Prestamo**: el ejemplar debe estar `DISPONIBLE` y en la sede donde se presta. Fecha de inicio = hoy; fecha limite =
  14 dias por defecto (configurable por prestamo entre 1 y 30 dias).
- **Vencido**: un prestamo `ACTIVO` cuya fecha limite ya paso (se calcula al consultar). En el filtro por cliente,
  `ACTIVOS` son los vigentes (no vencidos), `VENCIDOS` los que ya superaron la fecha y `FINALIZADOS` los devueltos.
- **Devolucion**: se puede hacer en cualquier sede activa. Guarda la sede de devolucion, calcula si hubo retraso y los
  dias, y el ejemplar queda `DISPONIBLE` **en la sede donde se devolvio** (no regresa automaticamente a su sede de
  origen; ver supuestos).
- **Reservas / solicitudes**: solo se puede reservar un libro que no tenga ejemplares `DISPONIBLE` en la sede
  solicitada. Estados: `PENDIENTE` -> `APROBADA` / `RECHAZADA` (con **motivo obligatorio**) / `CANCELADA` (por el
  cliente). Los administradores de una sede consultan sus solicitudes en `GET /reservas/sede/{sedeId}?estado=PENDIENTE`.
- **Traslados**: solo ejemplares `DISPONIBLE`; quedan `EN_TRASLADO` hasta que la sede destino confirma la recepcion, y en
  ese momento la ubicacion del ejemplar se actualiza sola (RF-19). Un traslado en transito puede cancelarse.
- **Historial del ejemplar (RF-20)**: cada registro, prestamo, devolucion y traslado deja un movimiento.
- **Usuarios administrativos**: roles `ADMINISTRADOR_GENERAL`, `ADMINISTRADOR_SEDE`, `BIBLIOTECARIO`. Todos, salvo el
  administrador general, deben pertenecer a una sede.
- **Sedes**: una sede no se puede desactivar si tiene ejemplares prestados o en traslado.

## Como ejecutar

Requisitos: JDK 21. (Los perfiles `dev` y `prod` usan MySQL; el perfil `test` usa H2 en memoria.)

```bash
# Con H2 en memoria, sin instalar nada (puerto 8090)
./gradlew bootRun --args='--spring.profiles.active=test'

# Con MySQL local (perfil dev por defecto, puerto 8080, base bibliotecas_db)
./gradlew bootRun
```

- Swagger UI: `http://localhost:8090/bibliotecas/swagger-ui.html` (puerto 8080 en dev)
- Postman: importar `postman/Bibliotecas.postman_collection.json` y ejecutarla en orden con el Collection Runner
  (usa el perfil `test`; guarda los IDs en variables de coleccion).

## Pruebas unitarias y cobertura

```bash
./gradlew test
```

Las pruebas de seguridad estan en `src/test/java/.../security` (`JwtServiceTest`, `CustomUserDetailsServiceTest` y
`SecurityIntegrationTest`, esta ultima levanta la aplicacion completa con H2 y prueba 401/403/200 de punta a punta).
Cada servicio tiene su clase de pruebas en `src/test/java/.../businessLayer` (Mockito sobre los DAO y servicios
colaboradores). Al terminar `test` se genera el reporte de Jacoco (`build/reports/jacoco/test/html/index.html`) y se
ejecuta `jacocoTestCoverageVerification`, que exige:

- cobertura minima general de 2 % (igual que el proyecto base), y
- **85 % de lineas cubiertas por cada clase de `businessLayer.service.impl`**.

Se puede subir o bajar el umbral en el bloque `jacocoTestCoverageVerification` de `build.gradle`.

## Supuestos tomados (a confirmar con el cliente)

Estos puntos estan en la seccion 3 del documento ("en analisis"); se implemento la opcion mas simple y facil de
cambiar:

1. Plazo de prestamo unico de 14 dias (maximo 30), igual para todos los clientes.
2. Un ejemplar devuelto en otra sede **se queda en esa sede**; para regresarlo a su sede de origen se usa un traslado.
3. Una reserva se hace sobre un **libro** y una **sede de recogida**; no reserva un ejemplar especifico.
4. No hay multas ni notificaciones; solo se informa si hubo retraso y cuantos dias.
5. Los permisos por rol de la seccion "Seguridad" son una propuesta (los niveles exactos estan "en analisis"); se ajustan
   en `security/SecurityConfig.java`. Los roles no limitan por sede: un `ADMINISTRADOR_SEDE` puede operar sobre cualquier sede.
6. Solo el personal administrativo inicia sesion. Los clientes no tienen cuenta (no hay login de clientes ni recuperacion de clave).

## Pendiente (fuera del alcance actual)

- Requerimientos no funcionales restantes: auditoria de operaciones, respaldos, etc.
- Login de clientes, registro publico y recuperacion de contrasena por correo (requiere definir el medio de notificacion).
- Los modulos de multas, renovacion de prestamos y copias de seguridad que aparecen en el mapa de navegacion pero no en los requerimientos funcionales.
- Decisiones de la seccion 3 del documento de requerimientos.
