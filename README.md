# 🔐 academicService

Microservicio académico de la plataforma **Libro Digital**.

Encargado de la gestión de cursos, estudiantes, matrículas, asignaturas y evaluaciones dentro del sistema. Forma parte de una arquitectura de microservicios desacoplados, donde la autenticación es provista por el `authService`.

---

## 🧠 Arquitectura

Este microservicio sigue una arquitectura por capas:

- **Controller** → Manejo de endpoints REST  
- **Service** → Lógica de negocio  
- **Repository** → Acceso a base de datos  
- **Model** → Entidades JPA (Course, Student, Enrollment, Subject, Evaluation)  
- **Security** → Configuración de seguridad con JWT  
- **Util** → Utilidades para validación y procesamiento de JWT  

---

## ⚙️ Stack tecnológico

- Java 21  
- Spring Boot 4.0.5  
- Spring Web  
- Spring Data JPA  
- Spring Security  
- JWT (JSON Web Tokens) con jjwt 0.11.5  
- Maven  
- PostgreSQL  

---

## 🚀 Instalación y ejecución

1. Clona este repositorio.  

2. Configura la base de datos en:

`src/main/resources/application.properties`

Ejemplo de configuración:

    spring.datasource.url=jdbc:postgresql://localhost:5432/librodigital_academic
    spring.datasource.username=postgres
    spring.datasource.password=tu_password

    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true

    server.port=8082

    jwt.secret=tuClaveSecretaParaJWT_MinimoDebeSerDe256Bits_UsaUnaClaveSegura123

3. Ejecuta la aplicación:

```bash
mvn clean spring-boot:run
```

---

## 🔑 Endpoints principales

> **⚠️ Todos los endpoints requieren autenticación JWT mediante header `Authorization: Bearer {token}`**

### 📘 Cursos

- `POST /courses` — Crear curso  
- `GET /courses` — Listar todos los cursos  
- `GET /courses/{id}` — Obtener curso por ID  
- `PUT /courses/{id}` — Actualizar curso  
- `DELETE /courses/{id}` — Eliminar curso  

---

### 👤 Estudiantes

- `POST /students` — Crear estudiante  
- `GET /students` — Listar todos los estudiantes  
- `GET /students/{id}` — Obtener estudiante por ID  
- `PUT /students/{id}` — Actualizar estudiante  
- `DELETE /students/{id}` — Eliminar estudiante  

---

### 📋 Matrículas

- `POST /enrollments` — Crear matrícula (inscribir estudiante en curso)  
- `GET /enrollments` — Listar todas las matrículas  
- `GET /enrollments/{id}` — Obtener matrícula por ID  
- `PUT /enrollments/{id}` — Actualizar matrícula  
- `DELETE /enrollments/{id}` — Eliminar matrícula  

---

### 📚 Asignaturas

- `POST /subjects` — Crear asignatura  
- `GET /subjects` — Listar todas las asignaturas  
- `GET /subjects/{id}` — Obtener asignatura por ID  
- `PUT /subjects/{id}` — Actualizar asignatura  
- `DELETE /subjects/{id}` — Eliminar asignatura  

---

### 📝 Evaluaciones

- `POST /evaluations` — Crear evaluación  
- `GET /evaluations` — Listar todas las evaluaciones  
- `GET /evaluations/{id}` — Obtener evaluación por ID  
- `PUT /evaluations/{id}` — Actualizar evaluación  
- `DELETE /evaluations/{id}` — Eliminar evaluación  

---

## 📁 Estructura del proyecto

```
academicService/
│
├── controller/
├── service/
├── service/impl/
├── repository/
├── model/
├── security/
└── util/ (preparado para JWT)
```

---

## 🧠 Conceptos aplicados

- Arquitectura por capas  
- Diseño de microservicios  
- Persistencia con JPA  
- Separación de responsabilidades  
- API REST  
- Modelado de dominio académico  

---

## 🔐 Integración con authService

Este microservicio está **completamente integrado** con `authService` mediante autenticación JWT:

- **JwtFilter**: Intercepta todas las peticiones y valida el token JWT  
- **JwtUtil**: Utilidad para extraer información del token (username, claims)  
- **SecurityConfig**: Configuración de Spring Security con autenticación JWT obligatoria  
- **CORS**: Configurado para aceptar peticiones desde el frontend (puertos 5173/5174)  

### Flujo de autenticación:

1. El usuario se autentica en `authService` y obtiene un token JWT  
2. El frontend envía el token en el header: `Authorization: Bearer {token}`  
3. `JwtFilter` intercepta la petición y valida el token  
4. Si es válido, permite el acceso al endpoint solicitado  
5. Si no es válido, retorna error 401 Unauthorized

---

## 👨‍💻 Autor

**Cristian Monsalve**

---

## 📌 Notas

Este microservicio representa el núcleo académico del sistema **Libro Digital**, permitiendo gestionar la estructura educativa completa:

- Cursos  
- Estudiantes  
- Matrículas  
- Asignaturas  
- Evaluaciones  
