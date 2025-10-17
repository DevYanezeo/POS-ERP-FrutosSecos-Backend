# deploy-temp

Carpeta de despliegue temporal para la aplicación POS-ERP Frutos Secos.

Propósito
-------
Esta carpeta contiene los artefactos mínimos necesarios para desplegar la aplicación completa (backend, frontend y base de datos) en una máquina remota (por ejemplo una VM en Google Cloud). Está diseñada para copiarla directamente a la VM y ejecutar `docker compose` desde esa ubicación.

Estructura
---------
- `.env` - Variables de entorno usadas por `docker-compose.prod.yml` y por scripts de despliegue. Contiene las variables que la aplicación backend lee (DB_*, JWT_*, SERVER_PORT, etc.).
- `docker-compose.prod.yml` - Compose de producción que define los 3 servicios principales: `db` (Postgres), `backend` (Spring Boot) y `frontend` (Next.js). Monta la carpeta `POS-ERP-FrutosSecos-Backend/p03/database/initdb` para que Postgres pueda ejecutar los scripts de inicialización al crear el volumen por primera vez.
- `docker-compose.yml` - (opcional) Versión local/alternativa del compose.
- `initdb/` - SQL de inicialización que se monta en `/docker-entrypoint-initdb.d` del contenedor Postgres. Los scripts aquí se ejecutan sólo la primera vez que se crea el volumen de datos del contenedor Postgres.
  - `01-schema.sql` - crea tablas, secuencias y esquema inicial.
  - `02-populate.sql` - inserta datos básicos de ejemplo.

Requisitos previos y convenciones
-------------------------------
- Esta carpeta debe estar en una carpeta **padre** que contenga ambos repositorios/paquetes:

  deploy-temp/
  ├─ POS-ERP-FrutosSecos-Backend/  <-- código backend (este repo)
  │  └─ p03/
  └─ POS-ERP-FrutosSecos-Frontend/ <-- código frontend

  El `docker-compose.prod.yml` está escrito para montar rutas relativas (`./POS-ERP-FrutosSecos-Backend/p03/...`). Por eso es importante que `deploy-temp` esté en el mismo nivel que esas carpetas (o ajustar rutas en el compose si lo colocas en otro sitio).

- Tener Docker y Docker Compose instalados en la VM.
- Si usas imágenes privadas (Docker Hub privado o GCR/Artifact Registry), ejecuta `docker login` o configura las credenciales en la VM antes de `docker compose pull`.

Contenido y notas por archivo
---------------------------

.env
  - Variables principales (ejemplo):
    - DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
    - JWT_SECRET, JWT_EXPIRATION
    - SERVER_PORT
  - El backend lee `application.properties` y sustituye `${DB_*}` por estas variables.
  - IMPORTANTE: este `.env` se copia tal cual por el compose vía `env_file:`. Si cambias la configuración de Postgres en `docker-compose.prod.yml` (p. ej. user/password) asegúrate de mantener consistencia entre el `.env` y el bloque `environment:` del servicio `db`.

docker-compose.prod.yml
  - Servicios:
    - db: image postgres:15
      - Monta `./POS-ERP-FrutosSecos-Backend/p03/database/initdb` como `/docker-entrypoint-initdb.d` y un volumen persistente `db-data`.
      - environment: POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD  (estos valores controlarán la creación inicial de la base de datos)
    - backend: imagen del backend (por ejemplo `isirm/p03-backend:latest`) y usa `env_file` apuntando a `POS-ERP-FrutosSecos-Backend/p03/.env`. Además, sobrescribe `SPRING_DATASOURCE_URL` para apuntar a `jdbc:postgresql://db:5432/p03`.
    - frontend: imagen del frontend (por ejemplo `isirm/p03-frontend:latest`).

initdb/
  - Los scripts SQL se ejecutan solo la primera vez que Postgres inicializa el volumen `db-data`. Si necesitas reaplicar los scripts debes eliminar el volumen y recrearlo (advertencia: esto borra datos).

Guía de uso (paso a paso) — despliegue en VM
------------------------------------------------
1. Copiar la carpeta completa en la VM (manteniendo la estructura descrita arriba). Por ejemplo, desde tu máquina local:

   gcloud.cmd compute scp --recurse .\deploy-temp msm:/home/isido/deploy-temp --zone=us-central1-c

2. (En la VM) Verificar contenido y ubicarte donde está el compose:

   cd /home/isido/deploy-temp
   ls -la

3. Ajustar `.env` si es necesario. Recomendado hacer backup antes de cambiar:

   cd POS-ERP-FrutosSecos-Backend/p03
   sudo cp .env .env.bak.$(date +%s)
   sudo nano .env  # o sudo vi .env

4. Si usas imágenes en un repositorio privado: hacer `docker login` en la VM.

5. Levantar los servicios (comando recomendado para producción):

   cd /home/isido/deploy-temp
   sudo docker compose -f docker-compose.prod.yml pull || true
   sudo docker compose -f docker-compose.prod.yml up -d

   - `pull` es opcional si las imágenes ya están en la VM.
   - Si quieres recrear solo el backend después de cambiar `.env`:
     sudo docker compose -f docker-compose.prod.yml up -d --no-deps --force-recreate backend

6. Ver logs y comprobar salud:

   sudo docker compose -f docker-compose.prod.yml logs -f --tail=200 backend
   sudo docker compose -f docker-compose.prod.yml ps
   # comprobar health endpoint desde la VM
   curl -v http://localhost:8080/actuator/health

7. Notas sobre inicialización de la DB (initdb):
   - Los scripts en `initdb/` se ejecutan una sola vez cuando se crea el volumen `db-data` por primera vez.
   - Si cambias los scripts y quieres re-ejecutarlos, debes eliminar el volumen y recrearlo (pérdida de datos):

     sudo docker compose -f docker-compose.prod.yml down
     sudo docker volume rm <nombre_del_volumen>  # identifica con docker volume ls
     sudo docker compose -f docker-compose.prod.yml up -d

Buenas prácticas y advertencias
-----------------------------
- Mantén `.env` fuera de repositorios públicos si contiene secretos (JWT secret, contraseñas). Para producción usa un secreto manejado (Secret Manager) o variables del entorno del host/servicio.
- No confíes en que los scripts `initdb` se re-ejecuten: solo al crear volumen.
- Antes de cambiar credenciales en `docker-compose.prod.yml` (POSTGRES_USER/PASSWORD) piensa si necesitas re-crear el volumen.
- Si quieres exponer la aplicación por nombre en lugar de IP, configura DNS (hosts local para pruebas o A record en un DNS público); puedo darte los pasos.



---

README generado el: 2025-10-14
