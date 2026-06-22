# 📚 academicService

Microservicio académico de la plataforma **Libro Digital**.

Encargado de la gestión de cursos, estudiantes, asignaturas, matrículas y evaluaciones dentro del sistema. Forma parte de una arquitectura de microservicios desacoplados, donde la autenticación es provista por el `authService`.

---

## 🧠 Arquitectura

Este microservicio sigue una arquitectura por capas:

- Controller → Manejo de endpoints REST  
- Service → Lógica de negocio  
- Repository → Acceso a base de datos  
- Model → Entidades JPA  
- Security → Configuración de autenticación JWT  

---

## ⚙️ Stack Tecnológico

- Java 21  
- Spring Boot 4.1.0  
- Spring Web  
- Spring Data JPA  
- Spring Security  
- JSON Web Tokens (JWT)  
- Maven  
- PostgreSQL  

---

## 🚀 Instalación y Ejecución

### 1. Configuración de Base de Datos

Editar el archivo:

src/main/resources/application.properties

Ejemplo:

spring.datasource.url=jdbc:postgresql://localhost:5432/librodigital_academic  
spring.datasource.username=postgres  
spring.datasource.password=tu_password  

spring.jpa.hibernate.ddl-auto=update  
spring.jpa.show-sql=true  

server.port=8092  

jwt.secret=tuClaveSecretaParaJWT_MinimoDebeSerDe256Bits  

---

### 2. Ejecutar aplicación

mvn clean spring-boot:run

El servicio estará disponible en:

http://localhost:8092  

---

## 🔐 Seguridad

Este microservicio está protegido con JWT.

✔ Todas las rutas requieren autenticación  
✔ Token enviado en header Authorization  

Formato:

Authorization: Bearer {token}

---

## 🔑 Endpoints principales

### 📘 Cursos
- POST /courses  
- GET /courses  
- GET /courses/{id}  
- PUT /courses/{id}  
- DELETE /courses/{id}  

---

### 👤 Estudiantes
- POST /students  
- GET /students  
- GET /students/{id}  
- PUT /students/{id}  
- DELETE /students/{id}  

---

### 📚 Asignaturas
- POST /subjects  
- GET /subjects  
- GET /subjects/{id}  
- PUT /subjects/{id}  
- DELETE /subjects/{id}  

---

### 📋 Matrículas
- POST /enrollments  
- GET /enrollments  
- GET /enrollments/{id}  
- PUT /enrollments/{id}  
- DELETE /enrollments/{id}  

---

### 📝 Evaluaciones
- POST /evaluations  
- GET /evaluations  
- GET /evaluations/{id}  
- PUT /evaluations/{id}  
- DELETE /evaluations/{id}  

---

## 🧾 Modelo de Datos (Resumen)

- Course → cursos  
- Student → estudiantes  
- Subject → asignaturas  
- Enrollment → matrículas  
- Evaluation → evaluaciones (incluye nota 1.0 - 7.0)

---

## 🏗️ Estructura del Proyecto

academicService/
├── controller/
├── service/
├── service/impl/
├── repository/
├── model/
├── security/

---

## 🔗 Integración con authService

El sistema utiliza autenticación distribuida mediante JWT:

1. Usuario se autentica en authService  
2. Recibe token JWT  
3. El frontend envía el token en cada request  
4. JwtFilter valida el token  
5. Si es válido → acceso permitido  
6. Si no → error 401  

---

## ✅ Estado del Proyecto

✔ CRUD completo en todos los módulos  
✔ Seguridad JWT implementada  
✔ Integración con frontend  
✔ Persistencia en PostgreSQL  
✔ Arquitectura en capas  

---

## 👨‍💻 Autor

Cristian Monsalve  
Hector Olivares
---

## 📌 Observaciones

Este microservicio constituye el núcleo académico del sistema, implementando las funcionalidades principales de gestión educativa.

Se aplicaron buenas prácticas como separación de responsabilidades, arquitectura por capas y uso de DTO para comunicación con el frontend.
``