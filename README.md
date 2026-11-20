# Iron Disk - Backend

API REST de la tienda Iron Disk (vinilos, CDs y cassettes). Es el proyecto de la Evaluación Parcial 3 de
Desarrollo Full Stack II (DSY1104), Duoc UC.

Reemplaza los datos guardados en `localStorage` del frontend por una base de datos MySQL, aplica las reglas de
negocio en el servidor y protege el acceso según el rol de cada usuario.

## Tecnologías

- Java 21 y Spring Boot 4
- Spring Data JPA con MySQL 8
- Spring Security con JWT y roles (Administrador, Vendedor y Cliente)
- Swagger para documentar la API

## Requisitos

- Java 21 (JDK)
- Docker Desktop, para la base de datos MySQL
- IntelliJ IDEA o VS Code, opcional (Maven viene incluido en el proyecto con `mvnw`)

## Cómo ejecutarlo

1. Copia `.env.example` como `.env` y cambia la clave de la base de datos. El archivo `.env` no se sube a GitHub.
2. Enciende Docker Desktop y levanta MySQL desde la carpeta del proyecto:
   ```
   docker compose up -d
   ```
3. Ejecuta la API:
   ```
   ./mvnw spring-boot:run
   ```
   En Windows también sirve `mvnw.cmd spring-boot:run`, o ejecutar `IronDiskBackendApplication` desde el IDE.
4. La API queda en `http://localhost:8080`.

| Acción | Comando |
|---|---|
| Detener MySQL | `docker compose down` |
| Detener MySQL y borrar los datos | `docker compose down -v` |

La base se llama `irondisk_db`, usa el puerto `3307` del computador y el usuario `root` con la clave del archivo `.env`.
Las tablas las crea JPA solas al iniciar la API.

## Frontend

El frontend de la tienda está en otro repositorio: https://github.com/Bast-JFR/iron-disk

## Equipo

Bastián y Renato.
