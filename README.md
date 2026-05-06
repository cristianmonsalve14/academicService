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
- **Security** → Configuración de seguridad (en preparación para JWT)  

---

## ⚙️ Stack tecnológico

- Java 21  
- Spring Boot 4.0.5  
- Spring Web  
- Spring Data JPA  
- Spring Security  
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

3. Ejecuta la aplicación:

```bash
mvn clean spring-boot:run
```

---

## 🔑 Endpoints principales

### 📘 Cursos

- `POST /courses` — Crear curso  
- `GET /courses` — Listar cursos  

---

### 👤 Estudiantes

- `POST /students` — Crear estudiante  
- `GET /students` — Listar estudiantes  

---

### 📋 Matrículas

- `POST /enrollments` — Inscribir estudiante en curso  
- `GET /enrollments` — Listar matrículas  

---

### 📚 Asignaturas

- `POST /subjects` — Crear asignatura  
- `GET /subjects` — Listar asignaturas  

---

### 📝 Evaluaciones

- `POST /evaluations` — Crear evaluación  
- `GET /evaluations` — Listar evaluaciones  

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

## 🔐 Integración (próximamente)

Este microservicio será integrado con `authService` mediante autenticación JWT para proteger los endpoints y validar usuarios autenticados.

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
