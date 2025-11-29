# 🏛️ DigiPin - System Architecture

## Table of Contents
- [System Overview](#system-overview)
- [Architecture Diagram](#architecture-diagram)
- [Component Architecture](#component-architecture)
- [Data Flow](#data-flow)
- [Security Architecture](#security-architecture)
- [Database Design](#database-design)
- [API Architecture](#api-architecture)
- [Design Patterns](#design-patterns)

---

## System Overview

DigiPin is a **3-tier enterprise application** built using **Spring Boot** and **MVC architecture pattern**, designed to provide secure digital address management with consent-based access control.

### Core Principles
- **Separation of Concerns**: Clear separation between presentation, business logic, and data layers
- **Security First**: Multi-layered security with JWT, BCrypt, and consent management
- **Scalability**: Stateless design enabling horizontal scaling
- **Maintainability**: Clean code principles with modular design

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                           CLIENT LAYER                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │   Web App    │  │  Mobile App  │  │  Third-Party (AIU)       │  │
│  │  (Frontend)  │  │   (React)    │  │  Integration Service     │  │
│  └──────┬───────┘  └──────┬───────┘  └────────────┬─────────────┘  │
└─────────┼──────────────────┼───────────────────────┼────────────────┘
          │                  │                       │
          └──────────────────┴───────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      SECURITY LAYER                                 │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │          JWT Authentication Filter                         │    │
│  │  • Token Validation  • User Authentication                 │    │
│  │  • Role Verification • CORS Configuration                  │    │
│  └───────────────────────────┬────────────────────────────────┘    │
└────────────────────────────────┼───────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER (Controllers)                │
│  ┌──────────────────────┐        ┌─────────────────────────────┐   │
│  │  AuthController      │        │ DigitalAddressController    │   │
│  │  ──────────────      │        │ ────────────────────────    │   │
│  │  • register()        │        │ • createDigitalAddress()    │   │
│  │  • login()           │        │ • updateDigitalAddress()    │   │
│  │  • verifyAadhaar()   │        │ • getDigitalAddress()       │   │
│  │  • getUserProfile()  │        │ • resolveWithConsent()      │   │
│  └──────────┬───────────┘        └──────────┬──────────────────┘   │
└─────────────┼──────────────────────────────┼────────────────────────┘
              │                              │
              └──────────────┬───────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    BUSINESS LOGIC LAYER (Services)                  │
│  ┌────────────────┐  ┌──────────────────┐  ┌──────────────────┐    │
│  │  AuthService   │  │ DigitalAddress   │  │ ConsentService   │    │
│  │  ────────────  │  │     Service      │  │ ──────────────   │    │
│  │  • getUserFrom │  │ ────────────────  │  │ • createConsent()│    │
│  │    Token()     │  │ • createDigital  │  │ • verifyUpiPin() │    │
│  │  • verifyAadh  │  │   Address()      │  │ • getConsentBy   │    │
│  │    aar()       │  │ • updateDigital  │  │   Token()        │    │
│  │                │  │   AddressByUser  │  │ • updateConsent()│    │
│  │                │  │   Id()           │  │ • getActive      │    │
│  │                │  │ • getDigital     │  │   Consent()      │    │
│  │                │  │   AddressByDigi  │  │                  │    │
│  │                │  │   pin()          │  │                  │    │
│  └────────┬───────┘  └─────────┬────────┘  └────────┬─────────┘    │
│           │                    │                     │              │
│  ┌────────┴──────────────────┬─┴─────────────────────┴─────────┐   │
│  │      CustomUserDetailsService                                │   │
│  │      • loadUserByUsername() • loadUserById()                 │   │
│  └──────────────────────────────────────────────────────────────┘   │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                  DATA ACCESS LAYER (Repositories)                   │
│  ┌──────────────────┐  ┌──────────────────┐  ┌─────────────────┐   │
│  │ UserRepository   │  │ DigitalAddress   │  │ ConsentRepo     │   │
│  │ ────────────     │  │    Repository    │  │ ───────────     │   │
│  │ • findByEmail    │  │ ────────────────  │  │ • findByDigital │   │
│  │   Id()           │  │ • findByDigital  │  │   AddressIdAnd  │   │
│  │ • findByPhone    │  │   Address()      │  │   IsActiveTrue()│   │
│  │   Number()       │  │ • findByUserId() │  │ • findByConsent │   │
│  │ • findByAadhaar  │  │                  │  │   TokenAndIs    │   │
│  │   Number()       │  │                  │  │   ActiveTrue()  │   │
│  └────────┬─────────┘  └────────┬─────────┘  └────────┬────────┘   │
│           │                     │                      │            │
│  ┌────────┴─────────────────────┴──────────────────────┴─────────┐  │
│  │              Spring Data JPA / Hibernate ORM                   │  │
│  └────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       DATABASE LAYER                                │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │                     MySQL Database                         │    │
│  │  ┌─────────┐  ┌─────────────┐  ┌─────────┐  ┌──────────┐  │    │
│  │  │  User   │  │  Digital    │  │ Consent │  │ Aadhaar  │  │    │
│  │  │  Table  │  │  Address    │  │  Table  │  │ MockData │  │    │
│  │  │         │  │  Table      │  │         │  │  Table   │  │    │
│  │  └─────────┘  └─────────────┘  └─────────┘  └──────────┘  │    │
│  └────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘

         ┌─────────────────────────────────────────┐
         │         CROSS-CUTTING CONCERNS          │
         │  • Exception Handling                   │
         │  • Logging & Monitoring                 │
         │  • Validation (Jakarta)                 │
         │  • Transaction Management               │
         └─────────────────────────────────────────┘
```

---

## Component Architecture

### 1. Configuration Layer (`config/`)

**SecurityConfig.java**
- Configures Spring Security
- Defines authentication provider
- Sets up JWT filter chain
- CORS configuration

**JwtAuthenticationFilter.java**
- Intercepts HTTP requests
- Validates JWT tokens
- Sets authentication context

### 2. Presentation Layer (`controller/`)

**Responsibilities:**
- Handle HTTP requests/responses
- Input validation using Jakarta Bean Validation
- Map DTOs to domain models
- Return appropriate HTTP status codes

**Key Controllers:**
- `AuthController`: User registration, login, Aadhaar verification
- `DigitalAddressController`: CRUD operations for digital addresses, consent resolution

### 3. Business Logic Layer (`service/`)

**Responsibilities:**
- Core business logic implementation
- Transaction management
- Business rules enforcement
- Inter-service communication

**Key Services:**
- `AuthService`: Authentication & authorization logic
- `DigitalAddressService`: Digital address management
- `ConsentService`: Consent creation, verification, and lifecycle management
- `CustomUserDetailsService`: User loading for Spring Security

### 4. Data Access Layer (`repository/`)

**Responsibilities:**
- Database CRUD operations
- Custom query methods
- Entity mapping

**Pattern**: Spring Data JPA Repository Pattern

### 5. Domain Model (`models/`)

**Entity Relationships:**
```
User (1) ─────────< (N) DigitalAddress
                          │
                          │ (1)
                          │
                          ▼
                        (1) Consent
```

### 6. Data Transfer Objects (`dto/`)

**Purpose:**
- Decouple API contracts from domain models
- Input validation
- Response formatting
- Security (hide sensitive fields)

---

## Data Flow

### 1. User Registration & Login Flow

```
┌──────────┐    1. POST /register    ┌────────────────┐
│  Client  │ ─────────────────────▶  │ AuthController │
└──────────┘                          └───────┬────────┘
                                              │ 2. Validate
                                              ▼
                                      ┌───────────────┐
                                      │  AuthService  │
                                      └───────┬───────┘
                                              │ 3. Hash password
                                              ▼
                                      ┌───────────────┐
                                      │ UserRepository│
                                      └───────┬───────┘
                                              │ 4. Save user
                                              ▼
                                      ┌───────────────┐
                                      │   Database    │
                                      └───────────────┘

Login Flow:
Client ──▶ AuthController ──▶ AuthService ──▶ UserRepository
   ▲                                  │
   │        7. Return JWT Token       │
   └──────────────────────────────────┤
                                      │ 5. Verify credentials
                                      │ 6. Generate JWT
                                      ▼
                                   JwtUtil
```

### 2. Digital Address Creation with Consent Flow

```
┌──────────┐    1. POST /create + JWT    ┌─────────────────────┐
│  Client  │ ──────────────────────────▶  │DigitalAddressCtrl   │
└──────────┘                               └──────────┬──────────┘
                                                      │
                                    2. Extract user from JWT
                                                      │
                                                      ▼
                                           ┌──────────────────┐
                                           │  AuthService     │
                                           │  getUserFromToken│
                                           └──────────┬───────┘
                                                      │
                               3. Create digital address
                                                      │
                                                      ▼
                                           ┌──────────────────┐
                                           │DigitalAddress    │
                                           │    Service       │
                                           └──────┬───────────┘
                                                  │
                         ┌────────────────────────┼────────────────────────┐
                         │                        │                        │
                    4. Save DA              5. Create consent        6. Generate DigiPin
                         │                        │                        │
                         ▼                        ▼                        ▼
                  ┌─────────────┐        ┌───────────────┐       ┌────────────┐
                  │ DA Repo     │        │ConsentService │       │ Algorithm  │
                  └─────────────┘        └───────┬───────┘       └────────────┘
                                                 │
                                         7. Hash UPI PIN
                                         8. Store consent
                                                 │
                                                 ▼
                                         ┌───────────────┐
                                         │ Consent Repo  │
                                         └───────────────┘
```

### 3. AIU Address Resolution Flow

```
┌──────────┐    1. POST /resolve-with-consent    ┌─────────────────────┐
│   AIU    │ ─────────────────────────────────▶   │DigitalAddressCtrl   │
└──────────┘    {digitalAddress, consentToken}    └──────────┬──────────┘
                                                              │
                                            2. Verify consent token
                                                              │
                                                              ▼
                                                   ┌──────────────────┐
                                                   │ ConsentService   │
                                                   │getConsentByToken │
                                                   └──────────┬───────┘
                                                              │
                                            3. Check expiry & active status
                                                              │
                                                              ▼
                                                   ┌──────────────────┐
                                                   │ DigitalAddress   │
                                                   │    Service       │
                                                   └──────────┬───────┘
                                                              │
                                            4. Fetch address details
                                                              │
                                                              ▼
                                                   ┌──────────────────┐
                                                   │  Return address, │
                                                   │  coordinates, &  │
                                                   │  consent info    │
                                                   └──────────────────┘
```

---

## Security Architecture

### Multi-Layer Security Model

```
┌─────────────────────────────────────────────────────────────┐
│                    Layer 1: Transport Security              │
│                    • HTTPS/TLS Encryption                   │
│                    • CORS Policy Enforcement                │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 Layer 2: Authentication Layer               │
│                    • JWT Token Validation                   │
│                    • Token Expiry Check                     │
│                    • User Identity Verification             │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 Layer 3: Authorization Layer                │
│                    • Role-Based Access Control              │
│                    • Resource Ownership Verification        │
│                    • Endpoint Protection                    │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 Layer 4: Business Logic Security            │
│                    • UPI PIN Verification                   │
│                    • Consent Token Validation               │
│                    • Aadhaar Verification                   │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 Layer 5: Data Protection Layer              │
│                    • BCrypt Password Hashing                │
│                    • UPI PIN Hashing (BCrypt)               │
│                    • Aadhaar Number Masking                 │
│                    • SQL Injection Prevention (JPA)         │
└─────────────────────────────────────────────────────────────┘
```

### Security Features

**1. JWT Authentication**
```
Token Structure:
Header: { "alg": "HS256", "typ": "JWT" }
Payload: { "sub": "userId", "iat": timestamp, "exp": timestamp }
Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

**2. Password & PIN Security**
- BCrypt with 10 rounds of hashing
- Salt automatically generated per password
- Rainbow table attack prevention

**3. Consent Management**
- Token-based access control
- Time-based expiry for temporary consents
- Automatic deactivation of expired consents

---

## Database Design

### Entity-Relationship Diagram

```
┌─────────────────────────────┐
│          User               │
│─────────────────────────────│
│ PK id                       │
│    userName (UNIQUE)        │
│    phoneNumber (UNIQUE)     │
│    emailId (UNIQUE)         │
│    password                 │
│    aadhaarNumber (MASKED)   │
│    isAadhaarVerified        │
└──────────┬──────────────────┘
           │ 1
           │
           │ N
┌──────────▼──────────────────┐
│    DigitalAddress           │
│─────────────────────────────│
│ PK id                       │
│ FK userId                   │
│    digitalAddress (UNIQUE)  │
│    generatedDigipin         │
│    suffix                   │
│    latitude                 │
│    longitude                │
│    address                  │
│ FK activeConsentId          │
│    hasActiveConsent         │
│    createdAt                │
└──────────┬──────────────────┘
           │ 1
           │
           │ 1
┌──────────▼──────────────────┐
│        Consent              │
│─────────────────────────────│
│ PK id                       │
│ FK userId                   │
│ FK digitalAddressId         │
│    upiPinHash               │
│    consentToken             │
│    consentType (ENUM)       │
│    createdAt                │
│    expiresAt (NULLABLE)     │
│    isActive                 │
└─────────────────────────────┘

┌─────────────────────────────┐
│     AadhaarMockData         │
│─────────────────────────────│
│ PK id                       │
│    aadhaarNumber (UNIQUE)   │
│    dateOfBirth              │
│    fullName                 │
└─────────────────────────────┘
```

### Database Indexes

```sql
-- Performance Optimization Indexes
CREATE INDEX idx_user_email ON User(emailId);
CREATE INDEX idx_user_phone ON User(phoneNumber);
CREATE INDEX idx_user_aadhaar ON User(aadhaarNumber);

CREATE INDEX idx_da_user ON DigitalAddress(userId);
CREATE INDEX idx_da_address ON DigitalAddress(digitalAddress);

CREATE INDEX idx_consent_da_active ON Consent(digitalAddressId, isActive);
CREATE INDEX idx_consent_token_active ON Consent(consentToken, isActive);
CREATE INDEX idx_consent_user_active ON Consent(userId, isActive);
```

---

## API Architecture

### RESTful Design Principles

**1. Resource-Based URLs**
```
/api/auth/register           → User registration
/api/auth/login              → Authentication
/api/digital-address/create  → Create resource
/api/digital-address/{id}    → Get resource
```

**2. HTTP Methods**
- `POST` - Create new resources
- `GET` - Retrieve resources
- `PUT` - Update existing resources
- `DELETE` - Remove resources (if implemented)

**3. Status Codes**
```
200 OK                  → Successful GET request
201 Created             → Successful POST (resource created)
400 Bad Request         → Validation error
401 Unauthorized        → Authentication failure
403 Forbidden           → Authorization failure
404 Not Found           → Resource doesn't exist
500 Internal Error      → Server error
```

### API Versioning Strategy
Currently using URL versioning: `/api/v1/...` (implicit v1)

---

## Design Patterns

### 1. MVC (Model-View-Controller)
```
Controller (View) ──▶ Service (Controller) ──▶ Repository (Model)
      ▲                                                │
      │                                                │
      └────────────────────────────────────────────────┘
```

### 2. Repository Pattern
- Abstracts data access logic
- Provides clean separation between business logic and data access
- Spring Data JPA implementation

### 3. DTO Pattern
- Decouples API contracts from domain models
- Provides input validation layer
- Controls data exposure

### 4. Dependency Injection
```java
@Autowired
private UserRepository userRepository;
```
- Spring manages bean lifecycle
- Promotes loose coupling
- Facilitates testing

### 5. Service Layer Pattern
```
Controller ──▶ Service ──▶ Repository
               (Business Logic)
```

### 6. Filter Chain Pattern
```
Request ──▶ JwtFilter ──▶ SecurityFilter ──▶ Controller
```

### 7. Builder Pattern (Lombok)
```java
@Data
public class User {
    // Automatically generates getters, setters, equals, hashCode
}
```

---

## Scalability Considerations

### Horizontal Scaling
- **Stateless Design**: JWT tokens eliminate server-side session storage
- **Database Connection Pooling**: HikariCP for efficient connection management
- **Load Balancer Ready**: No session affinity required

### Caching Strategy (Future Enhancement)
```
Redis Cache Layer
    ├── User Profile Cache
    ├── Digital Address Cache
    └── Consent Token Cache
```

### Performance Optimization
- **Database Indexing**: Strategic indexes on frequently queried columns
- **Lazy Loading**: JPA lazy fetch for related entities
- **DTO Projections**: Return only required fields in API responses

---

## Deployment Architecture

### Development Environment
```
Developer Machine
    ├── IDE (IntelliJ/Eclipse)
    ├── Local MySQL Database
    └── Maven for build
```

### Production Environment (Recommended)
```
┌─────────────────────────────────────────────┐
│              Load Balancer                  │
└──────┬──────────────────────┬───────────────┘
       │                      │
       ▼                      ▼
┌──────────────┐      ┌──────────────┐
│  App Server  │      │  App Server  │
│  Instance 1  │      │  Instance 2  │
└──────┬───────┘      └──────┬───────┘
       │                     │
       └──────────┬──────────┘
                  │
                  ▼
         ┌────────────────┐
         │ MySQL Database │
         │   (Primary)    │
         └────────────────┘
```

---

## Technology Justification

| Technology | Justification |
|------------|---------------|
| **Spring Boot** | Rapid development, production-ready, extensive ecosystem |
| **Spring Security** | Industry-standard security framework, JWT support |
| **Spring Data JPA** | Reduces boilerplate, type-safe queries, database abstraction |
| **MySQL** | ACID compliance, mature ecosystem, good for relational data |
| **JWT** | Stateless authentication, scalable, mobile-friendly |
| **BCrypt** | Industry-standard, adaptive hashing, brute-force resistant |
| **Lombok** | Reduces boilerplate, improves code readability |
| **Jakarta Validation** | Declarative validation, clean code |

---

## Future Enhancements

### Phase 2 Features
1. **Redis Caching Layer** - Improve response times
2. **Event-Driven Architecture** - Kafka/RabbitMQ for async processing
3. **Microservices Migration** - Split into Auth, Address, and Consent services
4. **GraphQL API** - Flexible data querying
5. **WebSocket Support** - Real-time location updates
6. **OAuth 2.0 Integration** - Third-party authentication
7. **Rate Limiting** - API throttling using Bucket4j
8. **Audit Logging** - Complete activity tracking
9. **Analytics Dashboard** - Usage metrics and insights
10. **Geofencing** - Location-based notifications

---

<div align="center">

**DigiPin Architecture v1.0**

Built with ❤️ for Hackathon 2025

</div>
