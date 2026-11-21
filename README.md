# Iron Disk - Backend

API REST de la tienda Iron Disk (vinilos, CDs y cassettes). Es el proyecto de la Evaluación Parcial 3 de
Desarrollo Full Stack II (DSY1104), Duoc UC.

Reemplaza los datos guardados en `localStorage` del frontend por una base de datos MySQL, aplica las reglas de
negocio en el servidor y protege el acceso según el rol de cada usuario.

| Qué | Cómo se cumple |
|---|---|
| Base de datos relacional | MySQL 8.4 con 5 tablas: usuario, categoria, producto, boleta y detalle_boleta |
| Entidades, repositorios y servicios | Capas `model`, `repository` y `service`, con la lógica de negocio en los servicios |
| API REST con CRUD | Controladores en `controller`, con rutas versionadas `/api/v1/...` |
| Documentación | Swagger en `http://localhost:8080/swagger-ui.html` |
| Seguridad | Spring Security con JWT (token de acceso de 15 minutos y de renovación de 7 días) y roles |
| Pruebas | JUnit 5 y Mockito sobre los servicios |

## Tecnologías

- Java 21 y Spring Boot 4.0
- Spring Data JPA con MySQL 8
- Spring Security con JWT (JJWT) y BCrypt
- Swagger con Springdoc

## Requisitos

- Java 21 (JDK)
- Docker Desktop, para la base de datos MySQL
- IntelliJ IDEA o VS Code, opcional (Maven viene incluido en el proyecto con `mvnw`)

## Cómo ejecutarlo

1. Copia `.env.example` como `.env` y cambia los valores. El archivo `.env` no se sube a GitHub.
   - `DB_PASSWORD`: la clave de la base de datos.
   - `JWT_SECRET`: la clave para firmar los tokens. Se genera con `openssl rand -base64 64`.
2. Enciende Docker Desktop y levanta MySQL desde la carpeta del proyecto:
   ```
   docker compose up -d
   ```
3. Ejecuta la API:
   ```
   ./mvnw spring-boot:run
   ```
   En Windows también sirve `mvnw.cmd spring-boot:run`, o ejecutar `IronDiskBackendApplication` desde el IDE.
4. La API queda en `http://localhost:8080` y Swagger en `http://localhost:8080/swagger-ui.html`.

Al primer inicio la API carga los datos de ejemplo (2 categorías, 22 discos y 4 usuarios) solo si las tablas están
vacías. Las tablas las crea JPA solas.

| Acción | Comando |
|---|---|
| Detener MySQL | `docker compose down` |
| Detener MySQL y borrar los datos | `docker compose down -v` |
| Ejecutar las pruebas (no necesitan MySQL) | `./mvnw test` |

La base se llama `irondisk_db`, usa el puerto `3307` del computador y el usuario `root` con la clave del archivo `.env`.

### Cuentas de ejemplo

| Rol | Correo | Contraseña | Puede |
|---|---|---|---|
| Administrador | `ba.jofre@duoc.cl` | `admin123` | Todo |
| Administrador | `re.colipe@duoc.cl` | `admin123` | Todo |
| Vendedor | `diego.perez@duoc.cl` | `vendedor1` | Ver productos y órdenes, sin modificar |
| Cliente | `camila.soto@gmail.com` | `cliente1` | Comprar, ver su perfil y sus compras |

### Probar con Swagger

1. Ejecuta `POST /api/v1/auth/login` con `{"correo":"ba.jofre@duoc.cl","password":"admin123"}`.
2. Copia el `accessToken` de la respuesta.
3. Presiona **Authorize**, pega el token y confirma.
4. Ya puedes probar los endpoints protegidos.

## Estructura del proyecto

```
src/main/java/cl/irondisk/backend/
├── model/        Entidades JPA, una por tabla, y los enumerados Rol y EstadoBoleta
├── repository/   Interfaces JpaRepository con las consultas
├── dto/          Objetos que entran y salen por la API
├── service/      Lógica de negocio: validaciones, reglas y cálculos
├── controller/   Endpoints REST, reciben la petición y llaman al servicio
├── security/     JWT, filtro de autenticación y permisos por rol
├── exception/    Excepciones propias y respuesta de error uniforme
└── config/       Swagger y datos iniciales
```

Una petición recorre siempre el mismo camino:
`Frontend (axios) → JwtAuthFilter → SecurityConfig → Controller → Service → Repository → MySQL`

### Modelo de datos

```
USUARIO 0..1 ──── 0..* BOLETA 1 ──── 1..* DETALLE_BOLETA *..1 ──── PRODUCTO *..1 ──── CATEGORIA
        (opcional: se puede comprar sin cuenta)
```

La boleta guarda una copia de los datos del cliente y el detalle guarda el nombre y el precio del momento de la
compra, para que no cambien si después se edita el producto o el usuario.

## Endpoints

Todas las rutas comienzan con `http://localhost:8080/api/v1`.

| Método y ruta | Quién | Descripción |
|---|---|---|
| `POST /auth/login` | Público | Inicia sesión y devuelve los dos tokens y el usuario |
| `POST /auth/registro` | Público | Crea una cuenta, siempre con rol CLIENTE |
| `POST /auth/refresh` | Público | Entrega tokens nuevos a partir del token de renovación |
| `GET /auth/me` | Con sesión | Datos del usuario dueño del token |
| `GET`, `PUT /perfil` | Con sesión | Ver y editar los datos propios |
| `GET /usuarios`, `GET /usuarios/{id}` | ADMIN | Listar y ver usuarios |
| `POST /usuarios`, `PUT` y `DELETE /usuarios/{id}` | ADMIN | Crear, editar y eliminar usuarios |
| `GET /categorias`, `GET /categorias/{id}` | Público | Listar y ver categorías |
| `POST /categorias`, `PUT` y `DELETE /categorias/{id}` | ADMIN | Administrar categorías |
| `GET /productos?categoriaId=&formato=&q=` | Público | Catálogo con filtros opcionales |
| `GET /productos/ofertas`, `GET /productos/{id}` | Público | Ofertas y detalle de un producto |
| `GET /productos/criticos` | ADMIN | Productos con stock igual o menor a su stock crítico |
| `POST /productos`, `PUT` y `DELETE /productos/{id}` | ADMIN | Administrar productos |
| `POST /boletas` | Público | Registra una compra, el backend calcula el total y descuenta el stock |
| `GET /boletas/mias` | Con sesión | Compras del usuario con sesión |
| `GET /boletas` | ADMIN y VENDEDOR | Lista todas las boletas |
| `GET /boletas/{id}` | Con sesión | Boleta con su detalle, un cliente solo ve las suyas |
| `GET /boletas/usuario/{usuarioId}` | ADMIN | Historial de compras de un usuario |
| `PATCH /boletas/{id}/estado` | ADMIN | Cambia el estado, al anular una boleta el stock vuelve al inventario |
| `GET /reportes/resumen` | ADMIN | Total vendido, boletas, ticket promedio y discos más vendidos, sin contar las anuladas |

La planilla con el detalle de cada endpoint está en `docs/Documentacion-APIs-IronDisk.xlsx` y la colección de Postman con los casos de prueba está en `docs/postman/IronDisk.postman_collection.json`.

### Respuestas de error

Todos los errores tienen la misma forma y el frontend muestra el campo `mensaje`:

```json
{ "status": 409, "mensaje": "Ya existe un producto con el código IM-001", "errores": null, "fecha": "2026-11-20T19:38:35" }
```

| Código | Cuándo |
|---|---|
| 400 | Datos inválidos, `errores` trae el detalle por campo |
| 401 | Sin token, token vencido o credenciales incorrectas |
| 403 | El rol no tiene permiso |
| 404 | El recurso no existe |
| 409 | Se rompe una regla de negocio, por ejemplo un código repetido |

## Seguridad

1. **Login:** `AuthService` compara la contraseña con el hash BCrypt y `JwtService` genera dos tokens. Si el correo o la
   contraseña fallan, el mensaje es siempre el mismo.
2. **Token de acceso (15 minutos):** lleva el correo, el rol y el nombre, y se envía en cada petición con
   `Authorization: Bearer <token>`.
3. **Token de renovación (7 días):** solo sirve en `POST /auth/refresh`. Un token de acceso no sirve para renovar.
4. **Cada petición:** `JwtAuthFilter` revisa la firma y el vencimiento y deja al usuario con su rol.
5. **Permisos:** `SecurityConfig` define en un solo lugar qué rol puede usar cada ruta y responde 401 o 403.

La API no guarda sesiones en el servidor. La clave del JWT y la contraseña de la base se leen del archivo `.env`.

## Reglas de negocio

| Regla | Dónde |
|---|---|
| RUN válido (dígito verificador), sin puntos ni guion, y único | `UsuarioService` y `ValidadorUsuario` |
| Solo correos @duoc.cl, @profesor.duoc.cl y @gmail.com | `UsuarioService` |
| Contraseña de 4 a 10 caracteres guardada con BCrypt; vacía al editar significa no cambiarla | `UsuarioService` |
| El registro público siempre crea un cliente | `UsuarioService` |
| Un administrador no puede eliminarse a sí mismo ni eliminar un usuario con compras | `UsuarioService` |
| Un access token no sirve para renovar la sesión | `AuthService` |
| Código de producto único y categoría existente | `ProductoService` |
| El precio y el total de una compra los calcula el backend, el frontend solo manda el producto y la cantidad | `BoletaService` |
| Si falta stock de un producto no se crea la boleta ni se descuenta nada | `BoletaService` |
| Anular una boleta devuelve el stock y una boleta anulada no se puede modificar | `BoletaService` |
| Un cliente solo puede ver sus propias boletas | `BoletaService` |
| Las boletas anuladas no se cuentan en el resumen de ventas | `ReporteService` |
| Un producto que ya se vendió no se elimina | `ProductoService` |
| El nombre de la categoría es único y no se elimina si tiene productos | `CategoriaService` |

## Frontend

El frontend de la tienda está en otro repositorio: https://github.com/Bast-JFR/iron-disk

## Equipo

Bastián y Renato.
