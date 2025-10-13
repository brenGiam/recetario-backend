# 📋 Backend - recetario

APP creada para mi uso personal, en donde puedo:

- Crear, modificar, leer y eliminar recetas.
- Buscar recetas aplicando filtros.
- Buscar recetas según ingredientes.

---
## 💻 Tecnologías

- Java
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- MySQL
- Cloudinary

---
## 🚀 Instalación

1. Cloná este repositorio https://github.com/brenGiam/pawFinder
2. Asegurate de tener Java 17+ y Maven instalados.
3. Configurá tu archivo application.properties o application.yml con los datos de:
- Conexión a base de datos MySQL
- Credenciales de Cloudinary
- Secret key de JWT
4. Ejecutá la aplicación con Maven:
./mvnw spring-boot:run

---
## 🧪 Autenticación

Autenticación mediante JSON Web Tokens (JWT). Se debe enviar el token en el header:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature

---
## 🧍‍♂️ Endpoints de Usuario

### 📝 Registrar usuario
**POST** `/usuarios`

Descripción: Crea un nuevo usuario.

**Body (JSON):**
```json
{
  "nombre": "Raúl",
  "apellido": "Mendez",
  "mail": "raul@mail.com",
  "password": "123456",
  "provincia": "Buenos Aires",
  "ciudad": "Punta Alta",
  "tel": "29325010101"
}
```

**Respuestas:**
- `201 Created`: Usuario creado.
- `400 Bad Request`: Validación incorrecta.

### 🔐 Iniciar sesión
**POST** `/auth/login`

Descripción: Autentica al usuario y devuelve un token JWT.

**Body (JSON):**
```json
{
  "mail": "maria@gmail.com",
  "password": "maria1234"
}
```

**Respuestas:**
- `200 OK`: Devuelve el token JWT.
- `401 Unauthorized`: Credenciales inválidas.

### 🔄 Actualizar usuario (🔒 Protegido)
**PATCH** `/usuarios`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Actualiza la información de un usuario existente.

**Headers:**
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
> Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Body (JSON):**
```json
{
  "nombre": "nuevoNombre",
  "apellido": "nuevoApellido",
  "mail": "nuevoEmail@ejemplo.com",
  "password": "nuevaContraseña123",
  "provincia": "nuevaProvincia"
}
```

**Respuestas:**
- `200 OK`: Usuario actualizado con éxito.
- `400 Bad Request`: Datos inválidos.
- `403 Forbidden`: Token no válido o ausente.
- `404 Not Found`: Usuario no encontrado.
- `500 Internal Server Error`: Error inesperado.

### 🗑️ Eliminar usuario (🔒 Protegido)
**DELETE** `/usuarios/mi-cuenta`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Elimina un usuario por su ID.

**Headers:**
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
> Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Respuestas:**
- `204 No Content`: Usuario eliminado con éxito.
- `403 Forbidden`: Token no válido o ausente.
- `404 Not Found`: Usuario no encontrado.
- `500 Internal Server Error`: Error inesperado.

### 🔍 Obtener perfil actual (🔒 Protegido)
**GET** `/usuarios/perfil`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Obtiene el perfil del usuario actual.

**Headers:**
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
> Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Respuestas:**
- `200 Ok`: Perfil obtenido correctamente. Se devuelve un UsuarioVistaDTO
- `403 Forbidden`: Token no válido o ausente.
- `404 Not Found`: Usuario no encontrado.
- `500 Internal Server Error`: Error inesperado.

---
## 🐾 Endpoints de Mascotas

### 📝 Crear mascota (🔒 Protegido)
**POST** `/mascotas`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Crea una nueva mascota para un usuario.

**Headers:**
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
> Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Body (JSON):**
```json
{
    "nombre": "Chuky",
    "especie": "PERRO",
    "estado": "ENCONTRADO",
    "conCollar": false,
    "raza": "Sin raza",
    "colores": [
        "Blanco"
    ],
    "caracteristicas": "",
    "provincia": "Buenos Aires",
    "ciudad": "Bahia Blanca",
    "barrio": ""
}
```
**Respuestas:**
- `201 Created`: Mascota creada correctamente. Devuelve una MascotaConMatchesRespuestaDTO
- `403 Forbidden`: Token no válido o ausente.
- `500 Internal Server Error`: Error inesperado.

### 🔍 Listar mascotas con o sin filtros
**GET** `/mascotas`

Descripción: Devuelve todas las mascotas que hay registradas en la base de datos y se puede filtrar por diferentes criterios.

**Parámetros opcionales:**
- `especie`: `PERRO`, `GATO`, etc.
- `estado`: `PERDIDO`, `ENCONTRADO`
- `provincia`: string
- `ciudad`: string
- `page`, `size`, `sort`: parámetros de paginación estándar de Spring.

**Respuestas:**
- `200 OK`: Devuelve una lista de mascotas, MascotaListarDTO. Si no hay mascotas que coincidan, devuelve Page.empty().
- `400 Bad request`: Error en los parámetros.
- `500 Internal Server Error`: Error inesperado.

### 🔄 Actualizar mascota (🔒 Protegido)
**PATCH** `/mascotas`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Actualiza la información de una mascota existente.

**Headers:** 
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
>Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Body (JSON):**
```json
{
    "id": "222c7aca-4e58-422d-928e-a1a22fa263a7",
    "especie": "PERRO",
    "estado": "ENCONTRADO",
    "nombre": "",
    "conCollar": false,
    "raza": "Sin raza",
    "colores": [
        "Blanco"
    ],
    "caracteristicas": "",
    "provincia": "Buenos Aires",
    "ciudad": "Bahia Blanca",
    "barrio": ""
}
```
**Respuestas:**
- `200 OK`: Mascota actualizada con éxito.
- `400 Bad Request`: Datos inválidos.
- `403 Forbidden`: Token no válido o ausente.
- `401 Unauthorized`: Si el usuario intenta modificar una mascota que no le pertenece.
- `500 Internal Server Error`: Error inesperado.

### 🔍 Obtener mascota
**GET** `/{idMascota}`

Descripción: Obtiene el perfil de una mascota seleccionada.

**Respuestas:**
- `200 Ok`: Perfil obtenido correctamente. Se devuelve un MascotaVistaDTO
- `400 Bad request`: La mascota no existe o formato UUID inválido.
- `500 Internal Server Error`: Error inesperado.

### 🗑️ Eliminar mascota (🔒 Protegido)
**DELETE** `/{idMascota}`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Elimina una mascota por su ID.

**Headers:** 
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
>Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Respuestas:**
- `204 No Content:` Mascota eliminada con éxito.
- `400 Bad Request:` Mascota no encontrada o UUID inválido.
- `401 Unauthorized`: Si el usuario intenta eliminar una mascota que no le pertenece.
- `403 Forbidden:` Token no válido o ausente.
- `500 Internal Server Error`: Error inesperado.

### 🔍 Obtener mis mascotas (🔒 Protegido)
**GET** `/mascotas/mis-mascotas`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Obtiene las mascotas pertenecientes a un usuario.

**Headers:**
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
> Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Respuestas:**
- `200 Ok`: Mascotas obtenidas correctamente. Se devuelve un MascotaListarDTO
- `400 Bad Request`: El estado es obligatorio, mascota no encontrada o UUID inválido.
- `401 Unauthorized`: Token no válido o ausente.
- `403 Forbidden`: No tienes permisos para realizar esta acción sobre esta mascota.
- `500 Internal Server Error`: Error inesperado.

### 🔄 Cambiar estado de mi mascota (🔒 Protegido)
**PATCH** `/mascotas/{idMascota}/estado`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Cambia el estado de una mascota (por ejemplo de PERDIDO a ENCONTRADO).

**Headers:**
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
> Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Body (JSON):**
```json
{
    "estado": "ENCONTRADO"
}
```
**Respuestas:**
- `200 Ok`: Estado de mascota actualizado correctamente. Se devuelve un MascotaVistaDTO.
- `401 Unauthorized`: Token no válido o ausente.
- `403 Forbidden`: Token no válido o ausente.
- `500 Internal Server Error`: Error inesperado.

## 🧩  Endpoints de Matches

### 🔍 Obtener mis matches (🔒 Protegido)
**GET** `/matches/mis-matches`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Obtiene los matches de un usuario especifico.

**Headers:** 
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
>Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Respuestas:**
- `200 Ok`: Matches obtenido correctamente. Se devuelve una lista de matches.
- `403 Forbidden`: Token no válido o ausente.
- `404 Not Found`: Usuario no encontrado.
- `500 Internal Server Error`: Error inesperado.

### 🔄 Actualizar estado match (🔒 Protegido)
**PUT** `/matches/{matchId}/estado`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Actualiza el estado de un match.

**Headers:** 
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
>Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Body (JSON):**
```json
{
  "estado": "NUEVO ESTADO",
}
```
**Respuestas:**
- `200 OK`: Tarea actualizada con éxito.
- `400 Bad Request`: Datos inválidos.
- `403 Forbidden`: Token no válido o ausente.
- `404 Not Found`: Tarea no encontrada.
- `500 Internal Server Error`: Error inesperado.

### 🔍 Obtener mis matches PENDIENTES (🔒 Protegido)
**GET** `/matches/pendientes`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Devuelve todos los matches del usuario cuyo estado sea PENDIENTE.

**Headers:** 
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
>Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Respuestas:**
- `200 OK`: Lista de matches.
- `403 Forbidden`: Token faltante o inválido.
- `500 Internal Server Error`: Error inesperado.

### 🔍 Obtener match (🔒 Protegido)
**GET** `/{matchId}`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Obtiene un match por su ID.

**Headers:** 
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
>Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Respuestas:**
- `200 OK:` Devuelve el match solicitado.
- `403 Forbidden:` Token no válido o ausente.
- `500 Internal Server Error`: Error inesperado.

### 🗑️ Eliminar match (🔒 Protegido)
**DELETE** `/matches/{matchId}`
🔒 Endpoints protegidos: requieren incluir un token JWT válido en el header de autorización.

Descripción: Elimina un match por su ID.

**Headers:**
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload.fakeSignature
> Este token es solo un ejemplo. Debe ser reemplazado por uno real obtenido al iniciar sesión.

**Respuestas:**
- `204 No Content`: Match eliminado con éxito.
- `403 Forbidden`: Token no válido o ausente.
- `404 Not Found`: Match no encontrado.
- `500 Internal Server Error`: Error inesperado.

