# Microservicio de Pólizas — Prueba Técnica Bolivar

Microservicio REST desarrollado con **Spring Boot 4 / Java 17** para la gestión de pólizas de seguros (Vida, Salud y Vehículo) y sus beneficiarios / vehículos asegurados.

---

## Tabla de contenido

1. [Solución planteada](#1-solución-planteada)
2. [Modelo de datos](#2-modelo-de-datos)
3. [Arquitectura del microservicio](#3-arquitectura-del-microservicio)
4. [Cómo correr localmente](#4-cómo-correr-localmente)
5. [Endpoints principales (API)](#5-endpoints-principales-api)
6. [Arquitectura AWS propuesta](#6-arquitectura-aws-propuesta)

---

## 1. Solución planteada

### Contexto y objetivo

El sistema permite a una aseguradora gestionar de manera centralizada:

- **Clientes** con su tipo de documento de identidad.
- **Pólizas base** (VIDA, SALUD, VEHICULO) con fechas de vigencia y estado.
- **Especializaciones** de cada tipo de póliza con sus propias reglas de negocio.
- **Beneficiarios** de pólizas de salud y vida.
- **Vehículos asegurados** dentro de una póliza de vehículo.

### Decisiones de diseño

| Decisión | Justificación |
|---|---|
| **Spring Boot 4 + Java 17** | LTS estable, ecosistema maduro, inyección de dependencias y gestión de transacciones out-of-the-box. |
| **Arquitectura por capas** (API → Dominio → Acceso a datos) | Separación clara de responsabilidades, fácil de testear y extender. |
| **Patrón de parámetros personalizados** (`Parametros*`) | Desacopla los contratos de la API de las entidades JPA; permite validación centralizada antes de llegar al repositorio. |
| **JPA Specification** para consultas dinámicas | Evita proliferación de métodos en los repositorios; los filtros se construyen solo con los campos que llegan distintos de `null`. |
| **MapStruct** para mapeo entidad ↔ DTO | Mapeo en tiempo de compilación, sin reflexión en runtime, código legible. |
| **Lombok** para reducir boilerplate | Getters/Setters/Constructores generados automáticamente. |
| **SpringDoc OpenAPI 3 (Swagger UI)** | Documentación interactiva de la API accesible en `/swagger-ui.html`. |
| **SQL Server** como motor de base de datos | Requisito del entorno corporativo; el driver MSSQL JDBC se incluye como dependencia de runtime. |
| **Validaciones de negocio explícitas** | Cada servicio valida sus propios parámetros y lanza `ValidationException` con mensajes claros; el `GlobalExceptionHandler` las captura y devuelve `ProblemDetails` (RFC 7807). |

### Reglas de negocio clave

- Una póliza **VIDA** por cliente no puede solaparse en fechas con otra póliza de vida activa del mismo cliente.
- Una póliza de **SALUD** con cobertura `SOLO_CLIENTE` **no permite** beneficiarios.
- Una póliza de **SALUD** con cobertura `CLIENTE_PADRES` admite máximo **1 padre y 1 madre**.
- Una póliza de **SALUD** con cobertura `CLIENTE_CONYUGE_HIJOS` admite máximo **1 cónyuge** e hijos ilimitados.
- Una póliza de **VIDA** admite máximo **2 beneficiarios**; el número de documento debe ser único por póliza.
- Una **placa de vehículo** debe ser única dentro de la misma póliza de vehículo.

---

## 2. Modelo de datos

### Diagrama entidad-relación (lógico)

```
┌─────────────────┐       ┌────────────────┐
│  TiposDocumento │       │  TiposPoliza   │
│─────────────────│       │────────────────│
│ id (PK)         │       │ id (PK)        │
│ codigo          │       │ codigo         │
│ descripcion     │       │ descripcion    │
│ activo          │       └───────┬────────┘
└────────┬────────┘               │ 1
         │ 1                      │
         │                        │ N
┌────────▼────────┐       ┌───────▼────────┐
│    Clientes     │       │    Polizas     │
│─────────────────│  1:N  │────────────────│
│ id (PK)         ├───────► clienteId      │
│ tipoDocumentoId │       │ id (PK)        │
│ numeroDocumento │       │ tipoPolizaId   │
│ nombres         │       │ numeroPoliza   │
│ apellidos       │       │ fechaInicio    │
│ email           │       │ fechaFin       │
│ telefono        │       │ estado         │
│ fechaNacimiento │       │ fechaCreacion  │
│ fechaCreacion   │       └────────┬───────┘
│ fechaActualizac.│                │
└─────────────────┘     ┌──────────┼───────────┐
                        │          │           │
                   1:1  │     1:1  │     1:N   │
                        ▼          ▼           ▼
              ┌──────────────┐ ┌──────────┐ ┌───────────────────┐
              │ PolizasVida  │ │PolizasSal│ │PolizasVehiculo    │
              │──────────────│ │ud────────│ │───────────────────│
              │ id (PK)      │ │id (PK)   │ │id (PK)            │
              │ polizaId(FK) │ │polizaId  │ │polizaId (FK)      │
              │ montoAseg.   │ │tipoCober.│ └─────────┬─────────┘
              └──────┬───────┘ └────┬─────┘           │ 1:N
                     │ 1:N          │ 1:N             ▼
                     ▼              ▼                ┌───────────────┐
            ┌────────────────┐  ┌──────────────────┐ │   Vehiculos   │
            │BeneficiariosV. │  │BeneficiariosSalud│ │───────────────│
            │────────────────│  │──────────────────│ │id (PK)        │
            │ id (PK)        │  │id (PK)           │ │polizaVehId(FK)│
            │ polizaVidaId   │  │polizaSaludId     │ │placa          │
            │ tipoParentesco │  │tipoParentesco    │ │marca          │
            │ nombreCompleto │  │nombreCompleto    │ │modelo         │
            │ numeroDocumento│  │numeroDocumento   │ │anio           │
            └────────────────┘  │montoAdicional    │ │valorAsegurado │
                                └──────────────────┘ └───────────────┘
```

### Catálogos (tablas de referencia)

| Tabla | Descripción | Registros semilla |
|---|---|---|
| `TiposDocumento` | CC, CE, PAS, NIT | 4 |
| `TiposPoliza` | VIDA, VEHICULO, SALUD | 3 |
| `TiposCoberturaSalud` | SOLO_CLIENTE, CLIENTE_PADRES, CLIENTE_CONYUGE_HIJOS | 3 |
| `TiposParentesco` | PADRE, MADRE, CONYUGE, HIJO | 4 |

> Los datos de los catálogos se insertan automáticamente al arrancar la aplicación mediante `data.sql` con guardas `WHERE NOT EXISTS`.

---

## 3. Arquitectura del microservicio

```
┌────────────────────────────────────────────────────────────┐
│                  HTTP Client / Swagger UI                  │
└─────────────────────────┬──────────────────────────────────┘
                          │ REST (JSON)
┌─────────────────────────▼──────────────────────────────────┐
│                    Capa API (Controllers)                  │
│  ClienteController  PolizasBaseController                  │
│  PolizasSaludController  PolizasVehiculoController         │
│  PolizasVidaController                                     │
│                                                            │
│  GlobalExceptionHandler ──► ProblemDetails (RFC 7807)      │
└─────────────────────────┬──────────────────────────────────┘
                          │ Interfaces de Servicio
┌─────────────────────────▼──────────────────────────────────┐
│                   Capa Dominio                             │
│                                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │ClienteService│  │PolizaBaseSvc │  │PolizaSaludService│  │
│  └──────────────┘  └──────────────┘  └──────────────────┘  │
│  ┌──────────────────────┐  ┌──────────────────────────────┐│
│  │PolizaVehiculoService │  │   PolizaVidaService          ││
│  └──────────────────────┘  └──────────────────────────────┘│
│                                                            │
│  IClienteMapper ──► MapStruct  IPolizaMapper               │
│  Enums (TipoPoliza, TipoParentesco, TipoCoberturaSalud)    │
│  Entidades JPA   DTOs   Parámetros personalizados          │
└─────────────────────────┬──────────────────────────────────┘
                          │ JPA Repositories (Specification)
┌─────────────────────────▼──────────────────────────────────┐
│              Capa Acceso a Datos (Repositories)            │
│                                                            │
│  ClienteRepository      PolizaRepository                   │
│  PolizasSaludRepository PolizasVehiculoRepository          │
│  PolizasVidaRepository  VehiculoRepository                 │
│  BeneficiariosSaludRepository  BeneficiariosVidaRepository │
│  TiposDocumentoRepository  TiposPolizaRepository           │
│  TiposCoberturaSaludRepository TiposParentescoRepository   │
└─────────────────────────┬──────────────────────────────────┘
                          │ JDBC (MSSQL Driver)
┌─────────────────────────▼──────────────────────────────────┐
│                  SQL Server (AseguradoraDB)                 │
└────────────────────────────────────────────────────────────┘
```

### Stack tecnológico

| Componente | Tecnología / Versión |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 4.0.3 |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | Microsoft SQL Server |
| Mapeo objetos | MapStruct 1.5.5 |
| Boilerplate | Lombok 1.18.26 |
| Documentación API | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven (Maven Wrapper incluido) |

---

## 4. Cómo correr localmente

### Prerrequisitos

| Herramienta | Versión mínima |
|---|---|
| JDK | 17 |
| Maven | 3.8+ *(o usar el wrapper `mvnw` incluido)* |
| SQL Server | 2019+ *(o SQL Server Express / Docker)* |

## Pruebas unitarias y de integración incluidas. Para correrlas:
No es necesario base de datos
- Controlador 100%
- Servicio 90%

.\mvnw.cmd test -Dtest=PolizaSaludServiceTest,PolizaVidaServiceTest,ClienteServiceTest,ClienteControllerTest,PolizasSaludControllerTest,PolizasVidaControllerTest,PolizasVehiculoControllerTest -Dspring.datasource.url=jdbc:h2:mem:testdb -Dspring.jpa.hibernate.ddl-auto=none -Dspring.sql.init.mode=never

Reporte JaCoCo → abrir en: target/site/jacoco/index.html

### Paso 1 — Levantar SQL Server con Docker *(opcional)*

Si no tienes SQL Server instalado localmente, puedes levantar una instancia con Docker:

```bash
docker run -e "ACCEPT_EULA=Y" \
           -e "SA_PASSWORD=Bogota2023." \
           -p 1433:1433 \
           --name sqlserver \
           -d mcr.microsoft.com/mssql/server:2022-latest
```

Luego crea la base de datos:

```sql
CREATE DATABASE AseguradoraDB;
```

### Paso 2 — Configurar la conexión

Edita `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://<HOST>:1433;databaseName=AseguradoraDB;encrypt=true;trustServerCertificate=true
    username: sa
    password: <TU_PASSWORD>
```

> Reemplaza `<HOST>` con `localhost` si usas Docker localmente, o con el nombre/IP de tu instancia.

### Paso 3 — Compilar y ejecutar

```bash
# Usando el Maven Wrapper (no requiere Maven instalado globalmente)
./mvnw spring-boot:run          # Linux / macOS
.\mvnw.cmd spring-boot:run      # Windows PowerShell
```

O bien, genera el JAR y ejecútalo:

```bash
./mvnw clean package -DskipTests
java -jar target/microservicio-polizas-0.0.1-SNAPSHOT.jar
```

### Paso 4 — Verificar que levantó correctamente

- **API base:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### Variables de entorno (alternativa a editar el YAML)

Para no exponer credenciales en el archivo de configuración puedes sobreescribirlas con variables de entorno:

```bash
export SPRING_DATASOURCE_URL="jdbc:sqlserver://localhost:1433;databaseName=AseguradoraDB;encrypt=true;trustServerCertificate=true"
export SPRING_DATASOURCE_USERNAME="sa"
export SPRING_DATASOURCE_PASSWORD="Bogota2023."
./mvnw spring-boot:run
```

---

## 5. Endpoints principales (API)

> Documentación interactiva completa disponible en Swagger UI al levantar la aplicación.

### Clientes — `/api/clientes`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/consultar-clientes` | Lista clientes con filtros opcionales (id, nombre, documento, email…) |
| POST | `/crear-cliente` | Crea un nuevo cliente |
| PUT | `/actualizar-cliente/{id}` | Actualiza datos de un cliente existente |
| DELETE | `/eliminar-cliente/{id}` | Elimina un cliente |

### Pólizas Base — `/api`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/consultar-polizas-base` | Lista pólizas base con filtros |
| POST | `/crear-poliza-base` | Crea una póliza base (VIDA / SALUD / VEHICULO) |
| GET | `/consultar-polizas-cliente` | Retorna pólizas de un cliente con todas sus especializaciones |

### Pólizas de Salud — `/api/polizas/salud`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/crear-poliza-salud` | Crea especialización de salud sobre una póliza base |
| POST | `/crear-beneficiario-salud` | Agrega beneficiario (respeta reglas de cobertura) |
| GET | `/consultar-polizas-salud` | Lista pólizas de salud con filtros |
| GET | `/consultar-beneficiarios-salud/{polizaSaludId}` | Lista beneficiarios de una póliza de salud |

### Pólizas de Vida — `/api/polizas/vida`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/crear-poliza-vida` | Crea especialización de vida con monto asegurado |
| POST | `/crear-beneficiario-vida` | Agrega beneficiario (máximo 2 por póliza) |
| GET | `/consultar-polizas-vida` | Lista pólizas de vida con filtros |
| GET | `/consultar-beneficiarios-vida/{polizaVidaId}` | Lista beneficiarios de una póliza de vida |

### Pólizas de Vehículo — `/api/polizas/vehiculo`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/crear-poliza-vehiculo` | Crea especialización de vehículo |
| POST | `/crear-vehiculo` | Agrega un vehículo asegurado (placa única por póliza) |
| GET | `/consultar-polizas-vehiculo` | Lista pólizas de vehículo con filtros |
| GET | `/consultar-vehiculos/{polizaVehiculoId}` | Lista vehículos de una póliza de vehículo |

### Manejo de errores

Todos los errores siguen el estándar **Problem Details (RFC 7807)**:

```json
{
  "status": 400,
  "type": "Validation",
  "title": "Validation Error",
  "detail": "polizaId es requerido y debe ser mayor a 0",
  "timestamp": "2026-03-01T12:00:00Z",
  "errors": []
}
```

---

## 6. Arquitectura AWS propuesta

### Visión general

El microservicio se puede desplegar en AWS siguiendo un patrón **serverless-friendly** o **containerizado**, dependiendo del volumen de tráfico esperado. Se propone una arquitectura basada en **contenedores (ECS Fargate)** por su equilibrio entre control operacional y escalabilidad automática.

```
┌──────────────────────────────────────────────────────────────────────────┐
│                              AWS Cloud                                   │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │                         VPC (us-east-1)                           │  │
│  │                                                                    │  │
│  │  ┌─────────────────────┐      ┌─────────────────────────────────┐ │  │
│  │  │   Public Subnets    │      │       Private Subnets           │ │  │
│  │  │                     │      │                                 │ │  │
│  │  │  ┌───────────────┐  │      │  ┌────────────────────────┐    │ │  │
│  │  │  │  API Gateway  │  │      │  │    ECS Fargate Cluster  │    │ │  │
│  │  │  │  (REST API)   │  │      │  │                         │    │ │  │
│  │  │  └──────┬────────┘  │      │  │  ┌─────────────────┐   │    │ │  │
│  │  │         │           │      │  │  │  Task: polizas  │   │    │ │  │
│  │  │  ┌──────▼────────┐  │      │  │  │  (Spring Boot)  │   │    │ │  │
│  │  │  │  Application  │  │      │  │  │  port: 8080     │   │    │ │  │
│  │  │  │  Load Balancer│──┼──────┼──►  └────────┬────────┘   │    │ │  │
│  │  │  │  (ALB)        │  │      │  │           │            │    │ │  │
│  │  │  └───────────────┘  │      │  └───────────┼────────────┘    │ │  │
│  │  │                     │      │              │                  │ │  │
│  │  └─────────────────────┘      │  ┌───────────▼────────────┐    │ │  │
│  │                               │  │  Amazon RDS            │    │ │  │
│  │                               │  │  (SQL Server SE)       │    │ │  │
│  │                               │  │  Multi-AZ Standby      │    │ │  │
│  │                               │  └────────────────────────┘    │ │  │
│  │                               │                                 │ │  │
│  │                               │  ┌────────────────────────┐    │ │  │
│  │                               │  │  AWS Secrets Manager   │    │ │  │
│  │                               │  │  (credenciales DB)     │    │ │  │
│  │                               │  └────────────────────────┘    │ │  │
│  │                               └─────────────────────────────────┘ │  │
│  └────────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐  │
│  │  Amazon ECR  │  │  CloudWatch  │  │     WAF      │  │  Route 53   │  │
│  │ (imágenes    │  │  (logs +     │  │ (seguridad   │  │  (DNS)      │  │
│  │  Docker)     │  │  métricas)   │  │  API)        │  │             │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └─────────────┘  │
└──────────────────────────────────────────────────────────────────────────┘
```

### Descripción de cada componente

| Servicio AWS | Rol | Justificación |
|---|---|---|
| **Amazon ECS Fargate** | Ejecuta los contenedores del microservicio | Sin gestión de servidores; escala automáticamente con políticas de CPU/memoria. |
| **Amazon ECR** | Registro privado de imágenes Docker | Integración nativa con ECS y CodePipeline; escaneo de vulnerabilidades incorporado. |
| **Application Load Balancer (ALB)** | Balanceo de carga HTTP/HTTPS | Soporta path-based routing, health checks y terminación SSL. |
| **API Gateway** *(opcional)* | Punto de entrada unificado | Útil si se expone la API a terceros: throttling, API keys, autenticación Cognito. |
| **Amazon RDS (SQL Server SE)** | Base de datos gestionada Multi-AZ | Backups automáticos, failover automático, parches de SO gestionados por AWS. |
| **AWS Secrets Manager** | Almacena credenciales de BD y otros secretos | Las tasks de ECS leen los secretos en runtime; nunca se guardan en variables de entorno en texto plano ni en el código. |
| **AWS WAF** | Firewall de aplicaciones web | Protege el ALB / API Gateway de inyecciones SQL, XSS y bots. |
| **Amazon CloudWatch** | Logs centralizados y métricas | Los contenedores envían logs via `awslogs` driver; se configuran alarmas por tasa de errores 5xx. |
| **Route 53** | DNS | Registra el dominio y enruta tráfico al ALB con health checks activos. |
| **AWS Certificate Manager (ACM)** | Certificados SSL/TLS gratuitos | Se adjunta al ALB para HTTPS; renovación automática. |

*Generado el 2026-03-01*

