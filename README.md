# 📋 Backend - recetario

APP creada para mi uso personal, en donde puedo:

- Crear, modificar, leer y eliminar recetas.
- Buscar recetas aplicando filtros.
- Buscar recetas según ingredientes.

---
## 💻 Tecnologías

- Java 17
- Spring Boot
- MongoDB
- Cloudinary
- Swagger

---
## 🚀 Instalación

1. Cloná este repositorio https://github.com/brenGiam/recetario-backend
2. Asegurate de tener Java 17+ y Maven instalados.
3. Configurá tu archivo application.properties o application.yml con los datos de:
- Conexión a base de datos MongoDB
- Credenciales de Cloudinary
4. Ejecutá la aplicación con Maven:
./mvnw spring-boot:run

---
## Endpoints de Recetas

### 📝 Crear Receta
**POST** `/recipes`  
**Content-Type:** multipart/form-data

Descripción: Crea una nueva receta, opcionalmente con imagen.

**Parámetros (form-data):**

| Nombre        | Tipo       | Descripción                  | Requerido |
|---------------|------------|------------------------------|-----------|
| title         | String     | Título de la receta          | Sí        |
| category      | String     | Categoría de la receta       | Sí        |
| ingredients   | String[]   | Lista de ingredientes        | Sí        |
| instructions  | String     | Instrucciones de preparación | Sí        |
| fit           | Boolean    | Indica si es receta fit      | Sí        |
| image         | File       | Imagen de la receta          | Opcional  |

**Respuestas:**
- `201 Created`: Receta creada correctamente. Devuelve una RecipeResponseDTO
- `400 Forbidden`: Datos inválidos.
- `500 Internal Server Error`: Error inesperado.

### 🔍 Listar recetas con o sin filtros
**GET** `/recipes/filter`

Descripción: Devuelve todas las recetas que hay registradas en la base de datos y se puede filtrar por diferentes criterios.

**Parámetros opcionales:**
- `category`: `CENA`, `MERIENDA`, etc.
- `fit`: boolean
- `page`, `size`: parámetros de paginación estándar de Spring.

**Respuestas:**
- `200 OK`: Devuelve una lista de recetas, RecipeFilteredResponseDTO. Si no hay recetas que coincidan, devuelve Page.empty().
- `500 Internal Server Error`: Error inesperado.

### 🔄 Actualizar Receta
**PATCH** `/recipes`
**Content-Type:** multipart/form-data

Descripción: Actualiza la información de una receta existente.

**Parámetros (form-data):**

| Nombre        | Tipo       | Descripción                  | Requerido |
|---------------|------------|------------------------------|-----------|
| title         | String     | Título de la receta          | Sí        |
| category      | String     | Categoría de la receta       | Sí        |
| ingredients   | String[]   | Lista de ingredientes        | Sí        |
| instructions  | String     | Instrucciones de preparación | Sí        |
| fit           | Boolean    | Indica si es receta fit      | Sí        |
| image         | File       | Imagen de la receta          | Opcional  |

**Respuestas:**
- `200 OK`: Receta actualizada con éxito.
- `400 Bad Request`: Datos inválidos.
- `404 Not Found`: Receta no encontrada.
- `500 Internal Server Error`: Error inesperado.

### 🔍 Obtener receta
**GET** `/{idReceta}`

Descripción: Obtiene una receta seleccionada.

**Respuestas:**
- `200 Ok`: Receta obtenida correctamente. Se devuelve un RecipeResponseDTO.
- `404 Not Found`: Receta no encontrada.
- `500 Internal Server Error`: Error inesperado.

### 🗑️ Eliminar receta
**DELETE** `/{idReceta}`

Descripción: Elimina una receta por su ID.

**Respuestas:**
- `200 No Content:` Receta eliminada con éxito.
- `404 Not Found:` Receta no encontrada.
- `500 Internal Server Error`: Error inesperado.

### 🔍 Listar recetas por ingredientes
**GET** `/recipes/ingredients`

Descripción: Devuelve todas las recetas que hay registradas en la base de datos con los ingredientes dados.

**Parámetro:**
- `ingredients`: `Queso`, `Harina`, etc.
- `page`, `size`: parámetros de paginación estándar de Spring.

**Respuestas:**
- `200 OK`: Devuelve una lista de recetas, RecipeFilteredResponseDTO. Si no hay recetas que coincidan, devuelve Page.empty().
- `500 Internal Server Error`: Error inesperado.

### 📄 Swagger UI
- Accedé a la documentación interactiva: `http://localhost:8080/swagger-ui`

