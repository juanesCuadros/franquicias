# franquicias-api

API REST reactiva para la gestión de franquicias, sus sucursales y los productos que cada sucursal tiene en inventario. Permite crear franquicias, agregarles sucursales, administrar el stock de productos por sucursal, y consultar cuál es el producto con más stock en cada sucursal de una franquicia.

## Stack técnico

| Componente                  | Tecnología                        |
|------------------------------|------------------------------------|
| Lenguaje                    | Java 17                           |
| Framework                   | Spring Boot 4.1.0                 |
| Capa web                    | Spring WebFlux (reactivo)         |
| Persistencia                | Spring Data MongoDB Reactive      |
| Base de datos               | MongoDB 7.0                       |
| Build                       | Maven (con Maven Wrapper)         |
| Contenedores                | Docker / Docker Compose           |
| Base de datos (producción)  | MongoDB Atlas (tier M0)           |
| Hosting                     | Render (tier gratuito)            |

## Criterios de aceptación cumplidos

- [x] **1. Desarrollado en Spring Boot** — Spring Boot 4.1.0 + WebFlux, ver `pom.xml` y `FranquiciasApiApplication.java`.
- [x] **2. Endpoint para agregar una nueva franquicia** — `POST /api/franquicias`.
- [x] **3. Endpoint para agregar una nueva sucursal a la franquicia** — `POST /api/franquicias/{franquiciaId}/sucursales`.
- [x] **4. Endpoint para agregar un nuevo producto a la sucursal** — `POST /api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos`.
- [x] **5. Endpoint para eliminar un producto de una sucursal** — `DELETE /api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}`.
- [x] **6. Endpoint para modificar el stock de un producto** — `PATCH /api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock`.
- [x] **7. Endpoint que muestre el producto con más stock por sucursal para una franquicia puntual** — `GET /api/franquicias/{franquiciaId}/productos/mas-stock`, retorna un listado de productos indicando a qué sucursal pertenece cada uno (`ProductoPorSucursalResponse`).
- [x] **8. Persistencia de datos con sistema en la nube (Redis/MySQL/Mongo/DynamoDB)** — se usó **MongoDB**, vía `spring-boot-starter-data-mongodb-reactive`.

### Puntos extra

- [x] Aplicación dockerizada, con `docker-compose.yml` levantando API + MongoDB.
- [x] Programación reactiva / funcional de punta a punta (WebFlux, `Mono`/`Flux`, streams en lugar de bucles imperativos).
- [x] Endpoint para actualizar el nombre de una franquicia — `PATCH /api/franquicias/{franquiciaId}`.
- [x] Endpoint para actualizar el nombre de una sucursal — `PATCH /api/franquicias/{franquiciaId}/sucursales/{sucursalId}`.
- [x] Endpoint para actualizar el nombre de un producto — `PATCH /api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}`.
- [x] IaC con Terraform — cluster de MongoDB Atlas (M0) y regla de acceso de red definidos como código en `infra/`. Validado con `terraform plan` (sin `apply`, para no duplicar el cluster productivo ya existente); ver `infra/README.md` para el detalle de cómo aplicarlo desde cero.
- [x] Despliegue en la nube — API en Render conectada a MongoDB Atlas. URL pública: https://franquicias-cj97.onrender.com

## Arquitectura

El proyecto sigue **arquitectura hexagonal** (puertos y adaptadores): el dominio de negocio vive aislado en el centro, sin depender de frameworks ni de infraestructura, y se comunica con el mundo exterior únicamente a través de interfaces (puertos). Spring, MongoDB y REST son detalles de infraestructura reemplazables, no el núcleo de la aplicación.

```
com/mvc/franquicias/
├── FranquiciasApiApplication.java
├── application/
│   └── usecase/                     (9 servicios @Service, uno por caso de uso)
├── domain/
│   ├── exception/                   (excepciones de negocio)
│   ├── model/                       (Franquicia, Sucursal, Producto, ProductoPorSucursal)
│   └── port/
│       ├── in/                      (interfaces de casos de uso)
│       └── out/                     (FranquiciaRepositoryPort)
└── infrastructure/
    └── adapter/
        ├── in/
        │   └── rest/                (FranquiciaController, GlobalExceptionHandler, DTOs)
        └── out/
            └── persistence/         (documentos Mongo, mapper, repositorio, adaptador)
```

- **`domain/model/`** — Records de Java inmutables (`Franquicia`, `Sucursal`, `Producto`, `ProductoPorSucursal`), sin ninguna anotación ni dependencia de Spring, Spring Data o MongoDB. Son el núcleo puro del negocio.
- **`domain/port/`** — Contratos: `port/in/` define los casos de uso disponibles (uno por operación de negocio, usando `Mono`/`Flux` de Reactor como parte del contrato reactivo del dominio); `port/out/` define lo que el dominio necesita de la infraestructura (`FranquiciaRepositoryPort`), sin saber que detrás hay MongoDB.
- **`application/usecase/`** — Implementa cada interfaz de `port/in/` con una clase `@Service`, orquestando el dominio (búsqueda, validación, reconstrucción de agregados) y delegando la persistencia al puerto de salida. No conoce nada de MongoDB ni de REST.
- **`infrastructure/adapter/in/rest/`** — El controller HTTP: recibe DTOs, valida (`@Valid`), invoca el caso de uso correspondiente (inyectado por su interfaz, no por la implementación concreta) y mapea la respuesta a DTO. Incluye el manejo global de errores.
- **`infrastructure/adapter/out/persistence/`** — Implementa `FranquiciaRepositoryPort` contra MongoDB reactivo: documentos de Spring Data, mapper documento↔dominio, y el repositorio reactivo.

**¿Por qué MongoDB?** El modelo de negocio es naturalmente jerárquico (una franquicia agrupa sucursales, que agrupan productos); modelarlo como un único documento agregado evita joins y refleja el dominio tal cual es, sin necesitar un esquema relacional normalizado.

**¿Por qué WebFlux/reactivo?** Es no bloqueante de punta a punta y tiene integración nativa con el driver reactivo de MongoDB, además de cumplir el plus de programación reactiva/funcional pedido en el enunciado.

**¿Por qué hexagonal?** Mantiene el dominio aislado de los detalles de infraestructura, permite testear las reglas de negocio sin mocks pesados de Spring/Mongo, y facilita cambiar de base de datos o de capa web sin tocar el núcleo del negocio.

## Cómo desplegar localmente

### Opción A: Con Docker (recomendado)

**Prerrequisitos**: Docker Desktop instalado y corriendo.

```
docker compose up --build
```

Esto levanta un contenedor de MongoDB 7.0 y la API (esperando a que Mongo esté healthy antes de arrancar). La API queda disponible en **http://localhost:8080**.

### Opción B: Desarrollo local sin Docker

**Prerrequisitos**: Java 17, y MongoDB corriendo en `localhost:27017` (puede ser con `docker run` de un contenedor de Mongo suelto, o una instalación local).

```
.\mvnw.cmd spring-boot:run   # Windows
./mvnw spring-boot:run       # Mac/Linux
```

## Despliegue en la nube (demo pública)

**URL base**: https://franquicias-cj97.onrender.com

> **Nota**: el servicio corre en el tier gratuito de Render, que "duerme" tras ~15 minutos de inactividad. La primera petición después de un período de inactividad puede tardar 30-90 segundos en responder mientras el servicio arranca — esto es esperado, no un error.

**Nota técnica**: la base de datos usada en este despliegue es MongoDB Atlas (tier M0 gratuito), separada de la instancia local de Docker usada en desarrollo.

Para probar esta URL con la colección de Postman, cambiá la variable de colección `baseUrl` de `http://localhost:8080` a la URL de Render indicada arriba.

## Cómo probar la API

La colección de Postman está en [`docs/postman/franquicias-api.postman_collection.json`](docs/postman/franquicias-api.postman_collection.json).

> **Importante**: importá el archivo con **Import > File** dentro de Postman. **No abras la carpeta `docs/postman/` como "workspace" o carpeta local** — eso hace que la app genere su propia metadata de sincronización dentro de esa carpeta, lo cual puede romper las referencias de la colección.

La colección está organizada como un flujo de demo, en orden: crear franquicia → agregar sucursal → agregar productos → consultar el producto con más stock por sucursal → actualizar/eliminar. Las variables de colección (`franquiciaId`, `sucursalId`, `productoId`, `productoId2`) se completan automáticamente mediante los test scripts de cada request de creación, así que basta con correr las carpetas en orden sin editar nada a mano.

## Endpoints disponibles

| Método | Ruta                                                                                   | Descripción                                                        |
|--------|------------------------------------------------------------------------------------------|---------------------------------------------------------------------|
| POST   | `/api/franquicias`                                                                        | Crea una nueva franquicia.                                          |
| POST   | `/api/franquicias/{franquiciaId}/sucursales`                                              | Agrega una sucursal a una franquicia existente.                     |
| POST   | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos`                       | Agrega un producto a una sucursal.                                  |
| DELETE | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}`          | Elimina un producto de una sucursal.                                 |
| PATCH  | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock`    | Actualiza el stock de un producto.                                   |
| GET    | `/api/franquicias/{franquiciaId}/productos/mas-stock`                                     | Devuelve el producto con más stock de cada sucursal de la franquicia.|
| PATCH  | `/api/franquicias/{franquiciaId}`                                                         | Actualiza el nombre de una franquicia.                               |
| PATCH  | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}`                                 | Actualiza el nombre de una sucursal.                                 |
| PATCH  | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}`          | Actualiza el nombre de un producto.                                  |

## Decisiones técnicas y trade-offs

- **Dominio modelado con records inmutables (estilo funcional)**: los métodos de "modificar" (`actualizarNombre`, `actualizarStock`, `agregarProducto`, etc.) devuelven una nueva instancia en vez de mutar el estado existente.
- **Toda la franquicia se persiste como un único documento de Mongo (agregado)**, con sucursales y productos embebidos — no como colecciones separadas relacionadas por id.
- **Criterio de desempate en "producto con más stock por sucursal"**: cuando dos productos de la misma sucursal tienen el mismo stock, se conserva el primero en el orden de la lista de productos de la sucursal.
- **Eliminar un producto con id inexistente devuelve 404** en vez de comportarse como una operación idempotente silenciosa — decisión intencional, documentada en el código (`EliminarProductoService`), para no esconder errores del cliente (ids mal formados o ya eliminados).
- **Los mappers** (dominio ↔ documento Mongo, dominio ↔ DTO REST) están implementados como clases con métodos **estáticos**, no como beans de Spring, para mantenerlos simples y evitar problemas de inyección en slice tests como `@DataMongoTest`.

## Nota sobre metodología

El desarrollo se apoyó en Claude Code como asistente de código, bajo dirección y revisión propias en cada paso. Las decisiones de arquitectura, la validación de cada etapa (incluyendo la corrección de incompatibilidades específicas de Spring Boot 4.1) y la verificación manual de todos los endpoints, así como la estructura final del repositorio, fueron definidas y revisadas por el autor en cada commit.

## Cómo ejecutar los tests

```
.\mvnw.cmd test
```

La suite incluye tests unitarios de dominio (records), de aplicación (casos de uso, con Mockito + `StepVerifier`), del controller (`@WebFluxTest` + `WebTestClient`), y un test de integración contra MongoDB real usando Testcontainers — este último **requiere Docker Desktop corriendo**.
