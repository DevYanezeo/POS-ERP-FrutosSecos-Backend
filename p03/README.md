# Mil Sabores Manager (MSM) - Backend API

Sistema integral de gestión para el minimarket "Mil Sabores".
Desarrollado con Spring Boot 3 y Java 17.

---

## Tecnologías Utilizadas
* **Lenguaje:** Java 17
* **Framework:** Spring Boot
* **Seguridad:** JWT (JSON Web Tokens)
* **Base de Datos:** PostgreSQL

---

## Documentación de la API
### ------------ Módulo de Usuarios y Roles -------------
Este módulo gestiona la creación, actualización y eliminación de usuarios y sus roles (Administrador y Vendedor) en el sistema Mil Sabores Manager.
### 1. UsuarioController

#### 1. GET /api/usuarios/all
Descripción: Retorna el listado completo de usuarios registrados en el sistema.
##### Respuesta Exitosa (200 OK):
```json
[
  {
    "idUsuario": 1,
    "nombre": "Linda Erika",
    "email": "lindaerika@milsabores.cl",
    "rol": "ADMIN"
  },
  {
    "idUsuario": 2,
    "nombre": "Juan Perez",
    "email": "juanperez@milsabores.cl",
    "rol": "VENDEDOR"
  }
]
```
#### 2. GET /api/usuarios/{id}
Descripción: Obtiene la información detallada de un usuario específico mediante su ID.
##### Respuesta Exitosa (200 OK): Objeto único UsuarioEntity.
##### Errores:
1. 404 Not Found: si el usuario no existe.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 400 Bad Request: si el ID proporcionado es inválido.

#### 3. POST /api/usuarios/crear
Descripción: Crea un nuevo usuario (Administrador o vendedor) en el sistema.
##### Cuerpo de la Petición:
```json
{
  "nombre": "Nuevo Operador",
  "email": "operador@milsabores.cl",
  "password": "passwordSegura123",
  "rol": "VENDEDOR"
}
```
##### Respuesta: Objeto UsuarioEntity creado con su ID asignado.

#### 4. PUT /api/usuarios/{id}
Descripción: Actualiza los datos de un usuario existente (nombre, email o rol).
##### Cuerpo de la Petición:
```json
{
  "nombre": "Nombre Actualizado",
  "email": "nuevo_email@milsabores.cl",
  "rol": "ADMIN"
}
```
##### Respuesta Exitosa (200 OK): Objeto UsuarioEntity actualizado.

#### 5. DELETE /api/usuarios/{id}
Descripción: Elimina un usuario existente mediante su ID.
##### Respuesta Exitosa (204 No Content): Indica que el usuario fue eliminado correctamente.


### ------------ Módulo de Autenticación y Seguridad (Auth) -------------
Este módulo gestiona el acceso al sistema Mil Sabores Manager, implementando seguridad basada en tokens JWT para proteger los datos financieros y de inventario.

#### 1. POST /api/auth/login
Descripción: Autentica a un usuario existente y genera un token de sesión.

##### Cuerpo de la Petición (LoginRequest):
```json
{
  "email": "usuario@ejemplo.com",
  "password": "tu_password_segura"
}
```

##### Respuesta Exitosa (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "idUsuario": 1,
  "email": "usuario@ejemplo.com",
  "nombre": "Nombre Apellido",
  "rol": "ADMIN"
}
```

##### Errores:
1. 400 Bad Request: si las credenciales son inválidas o hay un error en el servidor.
2. 401 Unauthorized: si el usuario no está autorizado.
3. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
4. 404 Not Found: si el usuario no existe.
5. 403 Forbidden: si el usuario no tiene permisos para acceder.

#### 2. POST /api/auth/register
Descripción: Registra un nuevo usuario (Administrador o vendedor) en el sistema.

##### Cuerpo de la Petición (RegisterRequest):
```json
{
  "nombre": "Nuevo Usuario",
  "email": "nuevo@ejemplo.com",
  "password": "password123",
  "rol": "ROLE_VENDEDOR"
}
```
##### Respuesta Exitosa (200 OK): Retorna el mismo objeto LoginResponse con el token autogenerado para inicio de sesión inmediato.

##### Errores:
1. 400 Bad Request: si los datos de registro son inválidos o ya existen.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 409 Conflict: si el email ya está registrado.

### ------------ Módulo de Gestión de Productos e Inventario --------------

Este módulo permite a los administradores y vendedores gestionar el inventario de productos del minimarket.
### 1. CategoriaController
#### 1. GET /api/categorias
Descripción: Retorna el listado completo de categorías existentes.

##### Respuesta Exitosa (200 OK):
```json
[
{
"id": 1,
"nombre": "Frutos Secos",
"descripcion": "Almendras, nueces, castañas"
},
{
"id": 2,
"nombre": "Snacks Salados",
"descripcion": "Papas fritas, maní salado"
}
]
```
##### Errores:
1. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
2. 404 Not Found: si no se encuentran categorías.

#### 2. POST /api/categorias/crear
Descripción: Crea una nueva categoría de productos.
##### Cuerpo de la Petición (CategoriaRequest):
```json
{
  "nombre": "Dulces y Chocolates",
  "descripcion": "Bombones y alfajores artesanales"
}
```
##### Respuesta Exitosa (200 OK): Retorna la categoría creada con su ID asignado.
##### Errores:
1. 400 Bad Request: si los datos de la categoría son inválidos.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 409 Conflict: si la categoría ya existe.
4. 403 Forbidden: si el usuario no tiene permisos para crear categorías.


### 2. ProductoController
#### 1. GET /api/productos-all
Descripción: Retorna el listado completo de productos existentes.

#### 2. GET /api/productos-all-con-categoria
Descripción: Retorna el listado completo de productos con su categoría asociada.
##### Parametros: Uso del ProductoConCategoriaDTO para la respuesta.
##### Ejemplo:
```json
[
  {
    "idProducto": 10,
    "nombre": "Almendras Naturales",
    "precio": 5500,
    "stockTotal": 50,
    "categoriaNombre": "Frutos Secos",
    "imagenUrl": "https://storage.googleapis.com/..."
  }
]
```

##### Errores:
1. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
2. 404 Not Found: si no se encuentran productos.

#### 3. GET /api/productos/buscar?nombre=...
Descripción: Busca productos por nombre (parcial o completo).
##### Ejemplo de uso: /api/productos/buscar?nombre=Almendras-Naturales
##### Respuesta Exitosa (200 OK): Lista de productos que coinciden con el nombre buscado.
##### Errores:
1. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
2. 404 Not Found: si no se encuentran productos que coincidan con el nombre.
3. 400 Bad Request: si el parámetro de búsqueda es inválido.

#### 4. GET /api/productos/stock-bajo/{min}
Descripción: Retorna productos con stock igual o inferior al mínimo especificado (5 actualmente).
##### Respuesta Exitosa (200 OK): Lista de productos con stock bajo.
##### Errores:
1. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
2. 404 Not Found: si no se encuentran productos con stock bajo.
3. 400 Bad Request: si el valor mínimo proporcionado es inválido.

#### 5. POST /api/productos/save
Descripción: Crea un nuevo producto en el inventario.
##### Respuesta Exitosa (201 Created): Retorna el objeto del producto creado.
##### Errores:
1. 400 Bad Request: si los datos del producto son inválidos.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 409 Conflict: si el producto ya existe.

#### 6. PUT /api/productos/{id}/imagen
Descripción: Sube o actualiza la imagen del producto directamente a Google Cloud Storage (GCS).

### 3. LoteController
Permite la trazabilidad física de los productos según su llegada y fecha de expiración.
#### 1. POST /api/lotes/crear
Descripción: Registra un nuevo lote de mercadería.
#### 2. GET /api/lotes/vencimiento?dias=30
Descripción: Consulta lotes que vencerán en los próximos X días.
##### Respuesta Exitosa (200 OK): Lista de lotes próximos a vencer.
##### Errores:
1. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
2. 404 Not Found: si no se encuentran lotes próximos a vencer.
3. 400 Bad Request: si el parámetro de días es inválido.

#### 3. GET /api/lotes/alertas
Descripción: Genera DTOs de alerta para el Dashboard (Próximos a vencer).
##### Ejemplo de alerta de vencimiento:
```json
{
  "loteId": 45,
  "productoNombre": "Mix Salado 500g",
  "fechaVencimiento": "2026-01-05",
  "diasParaVencer": 10
}
```
#### 4. GET /api/lotes/codigo/{codigo}
Descripción: Permite buscar un lote específico mediante el escáner de códigos de barra.

#### 5. PATCH /api/lotes/{id}/cantidad
Descripción: Ajusta la cantidad física de un lote específico.

### 4. MovimientoStockController
Registro de auditoría para entradas y salidas de mercadería que no corresponden a ventas (Mermas, ajustes, devoluciones).
#### 1. POST /api/movimientos-stock/registrar
Descripción: Registra un movimiento y actualiza automáticamente el stock del lote asociado.
##### Cuerpo de la Petición (MovimientoStockRequest):
```json
{
  "loteId": 45,
  "cantidad": 5,
  "tipoMovimiento": "SALIDA",
  "motivo": "Merma por envase dañado"
}
```
##### Respuesta Exitosa (201 Created): Retorna el objeto del movimiento registrado.
##### Errores:
1. 400 Bad Request: si los datos del movimiento son inválidos.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 404 Not Found: si el lote no existe.
4. 409 Conflict: si la salida excede el stock disponible.




### ------------ Módulo de Gestión de Ventas, Clientes y Fiados ----------
Este módulo permite a los administradores y vendedores gestionar las ventas, clientes y fiados del minimarket.
### 1. ClienteFiadoController
#### 1. GET /api/clientes-fiados
Descripción: Retorna el listado completo de clientes con fiados existentes.
##### Respuesta Exitosa (200 OK):
```json
[
  {
    "id": 1,
    "nombre": "Juan Pérez",
    "telefono": "987654321",
    "activo": true
  }
]
```
##### Errores:
1. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
2. 404 Not Found: si no se encuentran clientes con fiados.

#### 2. GET /api/clientes-fiados/activo
Descripción: Retorna el listado de clientes con fiados activos.
##### Respuesta Exitosa (200 OK): Lista filtrada de clientes con activo: true.
##### Errores:
1. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
2. 404 Not Found: si no se encuentran clientes con fiados activos.

#### 3. GET /api/clientes-fiados/{id}
Descripción: Obtiene la ficha de un cliente específico mediante su ID.

##### Respuesta Exitosa (200 OK): Objeto único ClienteFiadoEntity.
##### Errores:
1. 404 Not Found: si el cliente con fiado no existe.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 400 Bad Request: si el ID proporcionado es inválido.

#### 4. GET /api/clientes-fiados/buscar?telefono={num}
Descripción: Busca un cliente con fiado por su número de teléfono.
##### Ejemplo de uso: /api/clientes-fiados/buscar?telefono=912345678

##### Respuesta: Objeto del cliente si existe o uno de los errores.

##### Errores:
1. 404 Not Found: si no se encuentra un cliente con el número proporcionado.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 400 Bad Request: si el número de teléfono proporcionado es inválido.

#### 5. POST /api/clientes-fiados
Descripción: Crea un nuevo cliente con fiado.
##### Cuerpo de la Petición (ClienteFiadoRequest):
```json
{
  "nombre": "María González",
  "telefono": "911223344",
  "activo": true
}
```
##### Respuesta Exitosa (201 Created): Retorna el objeto del cliente creado.

##### Errores:
1. 400 Bad Request: si los datos del cliente son inválidos.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 409 Conflict: si el cliente ya existe.

#### 6. PUT /api/clientes-fiados/{id}
Descripción: Actualiza la información de un cliente con fiado existente.
##### Cuerpo de la Petición (ClienteFiadoRequest):
```json
{
  "nombre": "María González Actualizado",
  "telefono": "999887766",
  "activo": false
}
```
##### Respuesta Exitosa (200 OK): Retorna el objeto del cliente actualizado.

##### Errores:
1. 400 Bad Request: si los datos del cliente son inválidos.
2. 404 Not Found: si el cliente con fiado no existe.
3. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
4. 409 Conflict: si el nuevo teléfono ya está asociado a otro cliente.

#### 7. DELETE /api/clientes-fiados/{id}
Descripción: Elimina un cliente con fiado existente mediante su ID.
##### Respuesta Exitosa (204 No Content): Indica que el cliente fue eliminado correctamente.

### 2. DetalleVentaController
Este controlador gestiona los ítems específicos contenidos en cada transacción de venta.

Nota: Se encarga de la persistencia y consulta de los productos asociados a un ID de venta específico.

### 3. FiadoController
Este controlador gestiona las operaciones relacionadas con los fiados otorgados a los clientes.

#### 1. GET /api/fiados
Descripción: Retorna el listado completo de fiados existentes.

###### Parámetros: pendientesOnly (boolean, default: true).

##### Respuesta (200 OK): Lista de VentaEntity.

#### 2. GET /api/fiados/{id}
Descripción: Obtiene la ficha de un fiado específico mediante su ID.
##### Respuesta Exitosa (200 OK): Objeto único VentaEntity.
##### Errores:
1. 404 Not Found: si el fiado no existe.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 400 Bad Request: si el ID proporcionado es inválido.

#### 3. POST /api/fiados/{id}/marcar
Descripción: Transforma una venta común en un fiado asignándole un cliente.
##### Cuerpo (FiadoUpdateRequest):
```json
{
  "clienteId": 5,
  "fechaVencimiento": "2026-01-20"
}
```

##### Respuesta Exitosa (200 OK): Objeto VentaEntity actualizado.
##### Errores:
1. 404 Not Found: si la venta o el cliente no existen.
2. 400 Bad Request: si los datos proporcionados son inválidos.
3. 500 Internal Server Error: si ocurre un error inesperado en el servidor.

#### 4. PATCH /api/fiados/{id}
Descripción: Actualiza los datos de un fiado existente (monto o fecha).

#### 5. GET /api/fiados/clientes
Descripción: Lista a todos los clientes que poseen deudas activas y su saldo total.

##### Respuesta (200 OK): Lista de ClienteFiadoDTO.
##### Errores:
1. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
2. 404 Not Found: si no se encuentran clientes con deudas activas.

### 4. FeriadoController
Este controlador gestiona las operaciones relacionadas con los días feriados que afectan el funcionamiento del minimarket.
#### 1. GET /api/feriados/rango
Descripción: Consulta feriados entre dos fechas específicas.
##### Parámetros:
- fechaInicio (DD-MM-AAAA)
- fechaFin (DD-MM-AAAA)
##### Respuesta Exitosa (200 OK):
```json
[
  {
    "id": 10,
    "nombre": "Navidad",
    "fecha": "25/12/2025"
  }
]
```

##### Errores:
1. 400 Bad Request: si las fechas proporcionadas son inválidas.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 404 Not Found: si no se encuentran feriados en el rango especificado.

#### 2. POST /api/feriados
Descripción: Crea un nuevo feriado en el sistema.
##### Cuerpo de la Petición (FeriadoEntity):
```json
{
  "nombre": "Año Nuevo",
  "fecha": "01/01/2026"
}
```
##### Respuesta Exitosa (201 Created): Retorna el objeto del feriado creado.
##### Errores:
1. 400 Bad Request: si los datos del feriado son inválidos.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.
3. 409 Conflict: si el feriado ya existe.

### 4. VentaController
Este es el núcleo transaccional del sistema. Gestiona el ciclo de vida de las ventas, el flujo de caja, los pagos de fiados y la reversión de inventario.

#### 1. GET /api/ventas
Descripción: Retorna el listado histórico completo de ventas realizadas.

#### 2. POST /api/ventas/confirmar
Descripción: Procesa y confirma una nueva venta.

##### Cuerpo de Petición (VentaRequest):
```json
{
  "usuarioId": 1,
  "clienteId": 5,
  "metodoPago": "DEBITO",
  "items": [
    { "productoId": 10, "cantidad": 2, "precioUnitario": 5000 },
    { "productoId": 12, "cantidad": 1, "precioUnitario": 3500 }
  ]
}
```
#### 3. DELETE /api/ventas/{ventaId}/detalles/{detalleId}
Descripción: Anula un ítem específico de una venta y ajusta el inventario en consecuencia.
##### Parámetros: idLote (opcional), usuarioId (para auditoría).

#### 4. GET /api/ventas/historial
Descripción: Filtra ventas por rango de fechas (desde/hasta) y por el vendedor que la realizó.

#### 5. GET /api/ventas/historial/feriados
Descripción: Retorna el historial incluyendo si la venta se realizó en un día festivo

##### Respuesta (VentaWithHolidayDTO):
```json
{
  "ventaId": 101,
  "total": 15000,
  "fecha": "25/12/2025",
  "isHoliday": true,
  "holidayName": "Navidad"
}
```
#### 6. GET /api/ventas/fiados
Descripción: Lista las ventas que tienen saldo pendiente de pago.

#### 7. POST /api/ventas/{id}/pagos
Descripción: Registra un abono (parcial o total) a una deuda.
##### Cuerpo de Petición (PagoRequest):
```json
{
  "monto": 5000,
  "metodoPago": "EFECTIVO",
  "comentario": "Abono de fin de mes"
}
```

### ----------- Módulo de Reportes y Estadísticas ------------

Este módulo permite a los administradores generar reportes y estadísticas sobre las ventas, inventario y clientes del minimarket.
### 1. GastoController
Este controlador gestiona las operaciones relacionadas con los gastos operativos del minimarket.
#### 1. GET /api/gastos
Descripción: Retorna el listado completo de gastos registrados.

#### 2. POST /api/gastos
Descripción: Registra un nuevo gasto operativo.
##### Cuerpo de la Petición (GastoEntity):
```json
{
  "descripcion": "Pago Proveedor Frutos Secos",
  "monto": 150000,
  "fecha": "2025-12-13",
  "categoria": "Proveedores"
}
```
##### Respuesta Exitosa (201 Created): Retorna el objeto del gasto creado.
##### Errores:
1. 400 Bad Request: si los datos del gasto son inválidos.
2. 500 Internal Server Error: si ocurre un error inesperado en el servidor.

#### 3. GET /api/gastos/rango
Descripción: Consulta gastos entre dos fechas específicas.
##### Ejemplo: /api/gastos/rango?inicio=2025-12-01&fin=2025-12-31
##### Respuesta Exitosa (200 OK): Lista de gastos en el rango especificado.

### 2. ReporteController
Este controlador consolida los datos de ventas, lotes y gastos para ofrecer una visión analítica del estado financiero del minimarket "Mil Sabores".

#### 1. GET /api/reportes/productos/[semana|mes|dia|anio]
Descripción: Genera un reporte de los productos más vendidos en el período especificado (semana, mes, día, año).
##### Respuesta Exitosa (200 OK): Lista de ProductSalesDTO.
##### Ejemplo de ProductSalesDTO:
```json
{
  "productoNombre": "Maní Japonés",
  "cantidadVendida": 45,
  "totalIngresos": 135000
}
```

#### 2. GET /api/reportes/productos/[semana|mes|dia|anio]/menos
Descripción: Genera un reporte de los productos menos vendidos en el período especificado (semana, mes, día, año).
##### Respuesta Exitosa (200 OK): Lista de ProductSalesDTO.
##### Ejemplo de ProductSalesDTO:
```json
{
  "productoNombre": "Maní Confitado",
  "cantidadVendida": 2,
  "totalIngresos": 6000
}
```

#### 3. GET /api/reportes/productos/margen/[semana|mes|dia|anio]
Descripción: Genera un reporte de los productos con mayor margen de ganancia en el período especificado (semana, mes, día, año).
##### Respuesta Exitosa (200 OK): Lista de ProductMarginDTO.
##### Ejemplo de ProductMarginDTO:
```json
{
  "productoNombre": "Almendras 1kg",
  "ingresosTotales": 12000,
  "costoTotal": 8000,
  "margenGanancia": 4000
}
```
#### 4. GET /api/reportes/productos/perdidas/[semana|mes|dia|anio]
Descripción: Genera un reporte de los productos que expiraron y no se vendieron.
Como funciona: Sumatoria del costo de los productos perdidos por fecha de vencimiento.
##### Respuesta Exitosa (200 OK): Lista de ProductLossDTO.
##### Ejemplo de ProductLossDTO:
```json
{
  "productoNombre": "Mix Premium",
  "montoPerdido": 15500,
  "cantidadLotesVencidos": 2
}
```
#### 5. GET /api/reportes/finanzas/resumen/{periodo}
Parámetro Path: {periodo} puede ser dia, mes, semana o anio.
Descripción: Calcula Ingresos vs. Costos vs. Gastos Operativos.

##### Respuesta Exitosa (200 OK): Objeto FinanceSummaryDTO.
##### Ejemplo de FinanceSummaryDTO:
```json
{
  "totalIngresos": 500000,
  "totalCostosVenta": 200000,
  "totalGastosOperativos": 50000,
  "utilidadNeta": 250000
}
```

#### 6. GET /api/reportes/ventas/semana-actual
Descripción: Retorna una lista de 7 números (ingresos totales) de Lunes a Domingo de la semana en curso.
Uso: Ideal para gráficos de barras en el Dashboard.
##### Respuesta Exitosa (200 OK):
```json
[75000, 82000, 64000, 91000, 100000, 120000, 110000]
```

#### 7. GET /api/reportes/exportar/{periodo}
Descripción: Genera un archivo Excel (.xlsx) con todos los reportes anteriores consolidados.

Parámetros: year, month.
##### Respuesta Exitosa (200 OK): Archivo descargable Excel con los reportes.

