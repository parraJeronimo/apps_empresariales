# Sistema de Gestion de Multiples Bibliotecas

API REST (Spring Boot 3.5 / Java 21) para administrar una red de bibliotecas con varias sedes: catalogo de libros y
ejemplares, clientes, prestamos, devoluciones en cualquier sede, reservas, traslados entre sedes, usuarios
administrativos y reportes.

Este proyecto implementa **solo los requerimientos funcionales (RF-01 a RF-25)** del documento
`Requerimientos_Sistema_Gestion_Multiples_Bibliotecas.docx`. Los requerimientos no funcionales (seguridad, auditoria,
rendimiento, etc.) y los puntos "en analisis" quedan pendientes (ver el final de este documento).

## Estructura de carpetas

```
src/main/java/com/taller/bibliotecas
├── BibliotecasApplication.java
├── config/                 OpenApiConfig (Swagger)
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
| `usuarios-admin` | RF-23 |
| `reportes` (+ `/libros-mas-solicitados`) | RF-24 y ranking de libros mas solicitados |

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
5. Los roles de usuario existen como datos, pero todavia no restringen el acceso a los endpoints.

## Pendiente (fuera del alcance actual)

- Requerimientos no funcionales: autenticacion y control de acceso por rol (RNF), contrasenas cifradas, auditoria,
  respaldos, etc.
- Decisiones de la seccion 3 del documento de requerimientos.
