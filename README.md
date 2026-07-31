# 🌱 AgroSmart — Plataforma de Comercialización Agrícola

## Examen Final Práctico — Programación Avanzada

**Universidad de las Fuerzas Armadas ESPE**

Aplicación desarrollada con Spring Boot integrando:

- Spring WebFlux para programación reactiva.
- Spring Data JPA/Hibernate para persistencia.
- PostgreSQL como base de datos.
- LangChain4j para generación de publicidad mediante IA.
- Reactor (`Mono` y `Flux`) para manejo de flujos reactivos.
- JUnit 5 + Reactor Test para pruebas unitarias.

---

# 1. Semilla personal

## Datos de identidad

- Nombre: Elsa Janneth Pilataxi Malquin
- NRC: 30405
- Nonce del examen: AGS-2026

## Cálculo de semilla

| Parámetro | Valor |
|---|---|
| Últimos 2 dígitos de cédula | 14 |
| Tabla generada | `tbl_productos_base_14` |
| Puerto perfil prod | `8114` |
| Último dígito de cédula | 4 |
| Categoría asignada | Banano |

La semilla personal fue utilizada para definir el nombre de la tabla de productos y la configuración del perfil productivo.

# 2. Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring WebFlux
- Spring Data JPA
- Hibernate
- PostgreSQL
- LangChain4j
- Project Reactor
- JUnit 5
- Mockito
- Reactor Test

---

# 3. Configuración y ejecución

## Requisitos

- Java 21
- Maven
- PostgreSQL
- Variable de entorno para la API Key de IA

---

## Base de datos

La aplicación utiliza PostgreSQL.

La creación y actualización de tablas se realiza mediante Hibernate usando:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Configuración utilizada:

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Hibernate genera o actualiza la tabla:

```
tbl_productos_base_14
```

## Ejecutar la aplicación

Compilar:

```bash
./mvnw clean install
```

Ejecutar:

```bash
./mvnw spring-boot:run
```

La aplicación inicia con el perfil:

```
prod
```

y utiliza el puerto:

```
8114
```

---

# 5. Endpoints disponibles

## 5.1 Obtener productos comercializables

Este endpoint devuelve los productos que cumplen las reglas de comercialización aplicadas mediante programación funcional.

### Request

```bash
curl http://localhost:8114/api/productos
```

### Respuesta real obtenida

```json
[
 {
  "id":1,
  "nombre":"BANANO CAVENDISH",
  "categoria":"Banano",
  "precioUsd":5.50,
  "correosNotificacion":[
    "ventas@agrosmart.com"
  ]
 },
 {
  "id":2,
  "nombre":"BANANO ORGANICO",
  "categoria":"Banano",
  "precioUsd":8.75,
  "correosNotificacion":[
    "admin@agrosmart.com"
  ]
 }
]
```

Resultado HTTP:

```
StatusCode: 200 OK
Content-Type: application/json
```

---

## 5.2 Buscar producto por ID

Permite consultar un producto específico mediante su identificador.

### Request

```bash
curl http://localhost:8114/api/productos/2
```

### Respuesta real obtenida

```json
{
 "id":2,
 "nombre":"Banano Organico",
 "categoria":"Banano",
 "precioUsd":8.75,
 "correosNotificacion":[
    "admin@agrosmart.com"
 ]
}
```

Resultado HTTP:

```
StatusCode: 200 OK
Content-Type: application/json
```

---

## 5.3 Generar publicidad mediante IA

Este endpoint utiliza LangChain4j para solicitar una descripción publicitaria basada en el producto y la audiencia.

### Request

```bash
curl "http://localhost:8114/api/agrosmart/publicidad?producto=Banano%20Cavendish&audiencia=importadores%20europeos"
```

### Respuesta real obtenida

```text
Publicidad no disponible en este momento (ResourceAccessException)
```

Resultado HTTP:

```
StatusCode: 200 OK
Content-Type: text/plain
```

La respuesta demuestra el manejo reactivo de errores, ya que cuando el proveedor de IA no responde correctamente, la aplicación utiliza un mensaje alternativo mediante recuperación de errores sin interrumpir el flujo.

---

# 6. Manejo reactivo y boundedElastic

El proyecto utiliza Spring WebFlux, pero la persistencia con JPA es bloqueante.

JPA utiliza llamadas tradicionales como:

```java
repository.findAll()
repository.findById()
```

Estas operaciones no deben ejecutarse directamente en el event loop de Netty porque podrían bloquear las peticiones.

Para solucionar esto se utilizó:

```java
Schedulers.boundedElastic()
```

El flujo implementado es:

```
Repositorio JPA bloqueante
          |
          v
Mono.fromCallable()
          |
          v
Schedulers.boundedElastic()
          |
          v
Flux reactivo
```

De esta manera las operaciones bloqueantes se ejecutan en un pool separado de hilos.

---

# 7. Operadores reactivos utilizados

## Mono.fromCallable()

Se utilizó para envolver llamadas bloqueantes de JPA.

Permite que la operación no se ejecute hasta que exista una suscripción al flujo.

---

## subscribeOn(Schedulers.boundedElastic())

Permite ejecutar la operación bloqueante fuera del event loop.

Evita bloquear los hilos principales de WebFlux.

---

## flatMapMany()

Convierte una lista obtenida desde JPA en un flujo `Flux`.

Permite trabajar cada elemento individualmente.

---

## map()

Se utilizó para transformar objetos:

- De Entity a modelo de dominio.
- Aplicar transformación a mayúsculas.

---

## filter()

Permite descartar productos que no cumplen las reglas comerciales.

Ejemplo:

- Precio mayor a cero.
- Correos de notificación existentes.

---

## doOnNext()

Se utilizó para realizar trazabilidad mediante logs sin modificar los datos del flujo.

---

## defaultIfEmpty()

Permite entregar un producto genérico cuando ningún producto cumple las condiciones del filtro.

---

## switchIfEmpty()

Se utilizó en la búsqueda por ID.

Cuando no existe un producto, genera:

```
ProductoNoEncontradoException
```

sin romper el flujo reactivo.

---

# 8. Modelo inmutable

El dominio `Producto` fue implementado como un modelo inmutable.

Características:

- Campos privados finales.
- Sin setters.
- Constructor obligatorio.
- Copias defensivas de listas.

Ejemplo:

```java
List.copyOf()
```

Esto evita que una lista externa pueda modificar el estado interno del objeto.

---

# 9. Integración con IA

La aplicación utiliza LangChain4j mediante un servicio:

```java
@AiService
```

El modelo recibe:

- Producto.
- Público objetivo.

y genera una descripción publicitaria.

Además se implementó manejo de errores:

```java
onErrorResume()
```

para devolver un mensaje alternativo cuando el proveedor de IA no está disponible.

---

# 10. Pruebas unitarias

Las pruebas fueron realizadas con:

- JUnit 5.
- Mockito.
- Reactor Test.

Ejecutar:

```bash
./mvnw test
```

Resultado esperado:

```
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
```

Las pruebas cubren:

- Modelo inmutable.
- Copias defensivas.
- Reglas funcionales.
- Flujo reactivo.
- Manejo de errores.
- Integración con servicio de IA.

---

# 11. Evidencias

Las evidencias del desarrollo se encuentran en:

```
docs/evidencias/
```

Incluyen:

- 001. Inicio de aplicación con perfil prod
- 002. Configuración PostgreSQL
- 003. Datos de semilla 1
- 004. Datos de semilla 2
- 005. Ejecución de endpoints 1
- 006. Ejecución de endpoints 2
- 008. Ejecución de endpoints 3
- 009. Ejecución de endpoints 4
- 010. Pruebas unitarias exitosas
- 011. Historial de commits
---

# 12. Decisiones principales

Durante el desarrollo se priorizó:

- Mantener separación entre Entity y dominio.
- Evitar bloquear el event loop.
- Utilizar programación funcional con Predicate y Consumer.
- Mantener modelos inmutables.
- Probar los flujos reactivos sin depender de PostgreSQL.

---

# Autor

Elsa Janneth Pilataxi Malquin

Universidad de las Fuerzas Armadas ESPE