# 🎯 DigiPin - Digital Address with Consent Management System

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![ImmuDB](https://img.shields.io/badge/ImmuDB-Latest-green.svg)](https://immudb.io/)

> A secure and innovative digital address resolution system with PIN-based consent management, AAVA verification, real-time confidence scoring, and tamper-proof audit trails powered by ImmuDB.

## 🌟 Overview

DigiPin revolutionizes how digital addresses are created, managed, and shared in India. It combines location-based digital addressing with a secure consent mechanism, allowing users to share their addresses with authorized parties using a simple 6-digit DA PIN. The system integrates multiple verification layers and provides cryptographically verifiable audit trails for complete transparency and trust.

### 🎯 Key Features

#### Core Functionality
- 🗺️ **Location-Based Digital Addresses** - Generate unique digital addresses from GPS coordinates (username@suffix format)
- 📍 **DigiPin Generation** - Proprietary grid-based algorithm (4x4) for India-specific location codes (XXX-XXX-XXXX format)
- 🔐 **DA PIN Authentication** - Secure 6-digit PIN for address access control (BCrypt hashed)
- 🔑 **Consent Management** - Granular control over who can access your address with token-based sharing
- ⏰ **Flexible Consent Types** 
  - **PERMANENT**: Never expires, ideal for trusted long-term relationships
  - **TEMPORARY**: Auto-expires after specified duration (1-365 days)

#### Verification & Trust
- 🛡️ **Aadhaar KYC Verification** - Mock Aadhaar integration with 50+ test records for identity verification
- 🏆 **AAVA Physical Verification** - Government agent on-ground verification for highest trust level
- 📊 **Real-Time Confidence Scoring** - Dynamic trust score (0-100) based on:
  - Successful/failed deliveries
  - AAVA verification status
  - User ratings and feedback
  - Service fulfillment history
- ✅ **Verification Types**: SELF-verified (50 score) or AAVA-verified (80+ score)

#### Security & Audit
- 🔒 **JWT Authentication** - Stateless token-based authentication with 343-hour expiration
- 🔐 **BCrypt Encryption** - Industry-standard password and PIN hashing (strength: 10)
- 🛡️ **Spring Security** - Comprehensive security filter chain with role-based access control
- 🔐 **ImmuDB Integration** - Tamper-proof, cryptographically verifiable audit trail
  - All address operations logged
  - Immutable audit history
  - Cryptographic verification of data integrity
  - Transaction IDs for complete traceability

#### API & Integration
- 🚀 **RESTful APIs** - Clean, well-documented endpoints with 5 specialized controllers
- 🌐 **CORS Support** - Configurable cross-origin resource sharing
- 📊 **Comprehensive Audit APIs** - Query audit history, verify integrity, view statistics
- 🔧 **AIU Integration** - Authorized Integrator Unit APIs for address resolution

#### Developer Experience
- 📖 **Complete Documentation** - Detailed README, API docs, and architecture diagrams
- 🐳 **Docker Support** - Docker Compose for MySQL and ImmuDB setup
- 🧪 **Mock Data** - 50 pre-loaded Aadhaar records for testing
- 🛠️ **Maven Build** - Standard Maven project structure with all dependencies managed

## 🏗️ Architecture

> 📘 **Detailed Architecture**: For comprehensive architecture diagrams, data flows, and technical deep-dive, see [ARCHITECTURE.md](ARCHITECTURE.md)

### High-Level System Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                      CLIENT LAYER                               │
│   Web Apps | Mobile Apps | AIU Systems | AAVA Portal           │
└───────────────────────────┬────────────────────────────────────┘
                            │
                    ┌───────▼────────┐
                    │  API Gateway   │
                    │  (Port 8080)   │
                    └───────┬────────┘
                            │
        ┌───────────────────┴───────────────────┐
        │   JWT Authentication & Security        │
        └───────────────────┬───────────────────┘
                            │
    ┌───────────────────────▼────────────────────────┐
    │         DIGIPIN SPRING BOOT APP                │
    │                                                 │
    │  ┌─────────────────────────────────────────┐  │
    │  │  Controllers (REST APIs)                │  │
    │  │  • Auth • Digital Address • AAVA        │  │
    │  │  • AIU  • Audit                         │  │
    │  └──────────────────┬──────────────────────┘  │
    │                     │                          │
    │  ┌──────────────────▼──────────────────────┐  │
    │  │  Service Layer (Business Logic)        │  │
    │  │  • Auth • DigitalAddress • Consent     │  │
    │  │  • ImmuDB • UserDetails                │  │
    │  └──────────────────┬──────────────────────┘  │
    │                     │                          │
    │  ┌──────────────────▼──────────────────────┐  │
    │  │  Repository Layer (JPA)                 │  │
    │  │  • User • DigitalAddress • Consent      │  │
    │  │  • Aadhaar • UserAddress                │  │
    │  └──────────────────┬──────────────────────┘  │
    └───────────────────────┼──────────────────────┘
                            │
            ┌───────────────┴───────────────┐
            │                               │
       ┌────▼────┐                     ┌────▼─────┐
       │  MySQL  │                     │  ImmuDB  │
       │Database │                     │ Database │
       │         │                     │          │
       │• Users  │                     │• Audit   │
       │• Address│                     │  Logs    │
       │• Consent│                     │• Crypto  │
       │• Aadhaar│                     │  Verify  │
       └─────────┘                     └──────────┘
```

### Technology Stack

**Backend Framework:**
- **Spring Boot 3.5.8** - Modern Java framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence & ORM
- **Hibernate** - JPA implementation

**Databases:**
- **MySQL 8.0** - Primary transactional database
  - User management
  - Digital addresses
  - Consent records
  - Aadhaar mock data
- **ImmuDB** - Tamper-proof audit trail
  - Immutable event logs
  - Cryptographic verification
  - Complete audit history

**Security:**
- **JWT (JJWT 0.12.3)** - Token-based authentication
- **BCrypt** - Password and PIN hashing
- **Spring Security** - Security filter chain
- **Rate Limiting (Bucket4j 8.7.0)** - API throttling

**Additional Libraries:**
- **Lombok** - Code generation and boilerplate reduction
- **Jakarta Validation** - Bean validation
- **ZXing 3.5.3** - QR code generation
- **Jackson** - JSON processing
- **MySQL Connector J** - MySQL database driver

### Project Structure (MVC Pattern)

```
digipin/
├── src/main/java/com/hackathon/resolutionconsent/digipin/
│   ├── DigipinApplication.java              # Spring Boot entry point
│   │
│   ├── config/                               # Configuration classes
│   │   ├── SecurityConfig.java               # Security configuration & CORS
│   │   ├── JwtAuthenticationFilter.java      # JWT validation filter
│   │   └── ImmuDBConfig.java                 # ImmuDB connection config
│   │
│   ├── controller/                           # REST Controllers (API Layer)
│   │   ├── AuthController.java               # Authentication APIs
│   │   │   ├── POST /api/auth/register       → User registration
│   │   │   ├── POST /api/auth/login          → Login & JWT generation
│   │   │   ├── POST /api/auth/verify-aadhaar → Aadhaar verification
│   │   │   └── GET  /api/auth/profile        → User profile
│   │   │
│   │   ├── DigitalAddressController.java     # Digital address APIs
│   │   │   ├── POST   /api/digital-address/create          → Create address
│   │   │   ├── GET    /api/digital-address/{address}       → Get address
│   │   │   ├── GET    /api/digital-address                 → List addresses
│   │   │   ├── GET    /api/digital-address/digipin         → Get DigiPin
│   │   │   ├── PUT    /api/digital-address/update          → Update address
│   │   │   ├── DELETE /api/digital-address/delete          → Delete address
│   │   │   └── POST   /api/digital-address/flag-for-aava   → Flag for AAVA
│   │   │
│   │   ├── AAVAController.java               # AAVA verification APIs
│   │   │   ├── POST /api/aava/aava-verify              → Submit verification
│   │   │   └── GET  /api/aava/aava-status/{address}    → Verification status
│   │   │
│   │   ├── AIUController.java                # AIU (Integrator) APIs
│   │   │   ├── POST /api/aiu/store                    → Store user address
│   │   │   ├── POST /api/aiu/resolve-with-consent     → Resolve address
│   │   │   └── POST /api/aiu/feedback                 → Submit feedback
│   │   │
│   │   └── AuditController.java              # Audit & verification APIs
│   │       ├── GET  /api/audit/audit-history/{address}   → Audit logs
│   │       ├── POST /api/audit/verify-audit              → Verify integrity
│   │       ├── GET  /api/audit/audit-stats               → Statistics
│   │       └── GET  /api/audit/confidence-score/{id}     → Trust score
│   │
│   ├── service/                              # Business Logic Layer
│   │   ├── AuthService.java                  # Authentication & Aadhaar
│   │   ├── DigitalAddressService.java        # Address management
│   │   ├── ConsentService.java               # Consent & PIN verification
│   │   ├── ImmuDBService.java                # Audit logging & verification
│   │   └── CustomUserDetailsService.java     # Spring Security integration
│   │
│   ├── repository/                           # Data Access Layer (JPA)
│   │   ├── UserRepository.java               # User CRUD operations
│   │   ├── DigitalAddressRepository.java     # Address CRUD operations
│   │   ├── ConsentRepository.java            # Consent CRUD operations
│   │   ├── AadhaarMockDataRepository.java    # Aadhaar mock data
│   │   └── UserAddressRepository.java        # AIU address storage
│   │
│   ├── models/                               # Entity Models (JPA Entities)
│   │   ├── User.java                         # User entity
│   │   ├── DigitalAddress.java               # Digital address entity
│   │   ├── Consent.java                      # Consent entity
│   │   ├── AadhaarMockData.java              # Aadhaar mock entity
│   │   └── UserAddress.java                  # AIU user address entity
│   │
│   ├── dto/                                  # Data Transfer Objects
│   │   ├── RegisterRequest.java              # Registration request
│   │   ├── LoginRequest.java                 # Login request
│   │   ├── AadhaarVerificationRequest.java   # Aadhaar verification
│   │   ├── CreateDigitalAddressRequest.java  # Create address request
│   │   ├── UpdateDigitalAddressRequest.java  # Update address request
│   │   ├── AavaVerificationRequest.java      # AAVA verification request
│   │   ├── ResolveAddressWithConsentRequest.java  # AIU resolve request
│   │   ├── ServiceFulfillmentFeedbackRequest.java # Feedback request
│   │   ├── UserAddressRequest.java           # AIU store request
│   │   ├── UserProfileDto.java               # User profile response
│   │   └── ResolveAddressResponse.java       # Address resolution response
│   │
│   └── util/                                 # Utility Classes
│       ├── JwtUtil.java                      # JWT token generation & validation
│       └── UserDetailsImplement.java         # UserDetails implementation
│
├── src/main/resources/
│   ├── application.properties                # Application configuration
│   ├── static/                               # Static resources (if any)
│   └── templates/                            # Templates (if any)
│
├── src/test/java/                            # Test classes
│   └── com/hackathon/resolutionconsent/digipin/
│       └── DigipinApplicationTests.java
│
├── aadhaar_mock_data.sql                     # Mock Aadhaar data (50 records)
├── docker-compose.yml                        # Docker services (MySQL + ImmuDB)
├── pom.xml                                   # Maven dependencies
├── mvnw & mvnw.cmd                           # Maven wrapper scripts
├── LICENSE                                   # Apache 2.0 License
├── README.md                                 # This file
└── ARCHITECTURE.md                           # Detailed architecture docs
```

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+
- Docker (for ImmuDB)
- Git

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Tribhuvan-Web/digipin-backend.git
cd digipin-backend
```

2. **Start ImmuDB and MySQL using Docker**
```bash
# Windows PowerShell
.\setup-immudb.ps1

# OR manually with Docker Compose
docker-compose up -d
```

3. **Configure Database**

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/digipin_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# ImmuDB configuration (default values)
immudb.host=localhost
immudb.port=3322
immudb.database=digipin_audit
```

4. **Create Database**
```sql
CREATE DATABASE digipin_db;
```

5. **Build the project**
```bash
mvn clean install
```

6. **Run the application**
```bash
mvn spring-boot:run
```

The server will start at `http://localhost:8080`

## 📡 API Endpoints

### 🔐 Authentication APIs (`/api/auth`)

#### 1. Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "userName": "Tribhuvan_nath",
  "phoneNumber": "9876543210",
  "emailId": "tribhuvan@example.com",
  "password": "securePassword123"
}

Response (201 CREATED):
"Registered successfully"
```

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "emailOrPhone": "tribhuvan@example.com",  // Email or Phone
  "password": "securePassword123"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 3. Verify Aadhaar
```http
POST /api/auth/verify-aadhaar
Authorization: Bearer <token>
Content-Type: application/json

{
  "aadhaarNumber": "234567890123",
  "dateOfBirth": "1990-05-15"
}

Response (200 OK):
"Aadhaar verified successfully"
```

#### 4. Get User Profile
```http
GET /api/auth/profile
Authorization: Bearer <token>

Response (200 OK):
{
  "userName": "Tribhuvan_nath",
  "phoneNumber": "9876543210",
  "emailId": "tribhuvan@example.com",
  "isAadhaarVerified": true
}
```

---

### 🏠 Digital Address APIs (`/api/digital-address`)

#### 1. Create Digital Address
```http
POST /api/digital-address/create
Authorization: Bearer <token>
Content-Type: application/json

{
  "suffix": "home",
  "latitude": 28.6139,
  "longitude": 77.2090,
  "address": "Connaught Place, New Delhi, India",
  "daPin": "123456",
  "consentType": "PERMANENT",        // or "TEMPORARY"
  "consentDurationDays": 30          // Required for TEMPORARY
}

Response (201 CREATED):
"Digital address created successfully"

Note: Creates digital address in format: username@suffix
      E.g., Tribhuvan_nath@home
      Automatically generates DigiPin from coordinates
```

#### 2. Get Digital Address
```http
GET /api/digital-address/{digitalAddress}
Authorization: Bearer <token>

Example: GET /api/digital-address/Tribhuvan_nath@home

Response (200 OK):
{
  "id": 1,
  "userId": 1,
  "digitalAddress": "Tribhuvan_nath@home",
  "generatedDigipin": "FC9-823-7654",
  "latitude": 28.6139,
  "longitude": 77.2090,
  "address": "Connaught Place, New Delhi, India",
  "confidenceScore": 50.0,
  "verificationType": "SELF",
  "isAadhaarVerified": false,
  "hasActiveConsent": true,
  "createdAt": "2025-12-02T10:30:00"
}
```

#### 3. List All Addresses
```http
GET /api/digital-address
Authorization: Bearer <token>

Response (200 OK):
[
  {
    "id": 1,
    "digitalAddress": "Tribhuvan_nath@home",
    "generatedDigipin": "FC9-823-7654",
    ...
  },
  {
    "id": 2,
    "digitalAddress": "Tribhuvan_nath@office",
    "generatedDigipin": "FC9-824-1234",
    ...
  }
]
```

#### 4. Get DigiPin for Coordinates
```http
GET /api/digital-address/digipin?lat=28.6139&lon=77.2090

Response (200 OK):
"FC9-823-7654"
```

#### 5. Update Digital Address
```http
PUT /api/digital-address/update
Authorization: Bearer <token>
Content-Type: application/json

{
  "latitude": 28.7041,
  "longitude": 77.1025,
  "address": "Updated Address, Delhi",
  "daPin": "123456",               // Required for verification
  "consentType": "TEMPORARY",
  "consentDurationDays": 60
}

Response (200 OK):
"Digital address updated successfully"
```

#### 6. Delete Digital Address
```http
DELETE /api/digital-address/delete?digitalAddress=Tribhuvan_nath@home
Authorization: Bearer <token>

Response (200 OK):
"Digital address deleted successfully"
```

#### 7. Flag for AAVA Verification
```http
POST /api/digital-address/flag-for-aava
Authorization: Bearer <token>
Content-Type: application/json

{
  "digitalAddress": "Tribhuvan_nath@home",
  "reason": "Required for government welfare scheme"
}

Response (200 OK):
{
  "message": "Digital address flagged for AAVA verification",
  "digitalAddress": "Tribhuvan_nath@home",
  "requiresAavaVerification": true,
  "note": "An AAVA agent will be assigned for physical verification"
}
```

---

### 🛡️ AAVA Verification APIs (`/api/aava`)

#### 1. Submit AAVA Verification
```http
POST /api/aava/aava-verify
Content-Type: application/json

{
  "digitalAddress": "Tribhuvan_nath@home",
  "agentId": "AAVA001",
  "verificationStatus": "VERIFIED",     // or "REJECTED", "PENDING"
  "locationConfirmed": true,
  "verifiedLatitude": 28.6139,
  "verifiedLongitude": 77.2090,
  "verificationNotes": "Location verified. Resident confirmed."
}

Response (200 OK):
{
  "message": "AAVA verification processed successfully",
  "digitalAddress": "Tribhuvan_nath@home",
  "verificationStatus": "VERIFIED",
  "isAavaVerified": true,
  "verificationType": "AAVA",
  "oldConfidenceScore": 50.0,
  "newConfidenceScore": 80.0,
  "agentId": "AAVA001",
  "verifiedAt": "2025-12-02T14:30:00",
  "tamperProofLogged": true
}
```

#### 2. Get AAVA Status
```http
GET /api/aava/aava-status/{digitalAddress}

Example: GET /api/aava/aava-status/Tribhuvan_nath@home

Response (200 OK):
{
  "digitalAddress": "Tribhuvan_nath@home",
  "isAavaVerified": true,
  "verificationType": "AAVA",
  "requiresAavaVerification": false,
  "agentId": "AAVA001",
  "verifiedAt": "2025-12-02T14:30:00",
  "verificationNotes": "Location verified. Resident confirmed.",
  "approvedUseCases": {
    "governmentWelfare": true,
    "propertyRecords": true,
    "legalNotices": true,
    "emergencyServices": true,
    "eCommerce": true,
    "foodDelivery": true
  }
}
```

---

### 🔗 AIU (Integrator) APIs (`/api/aiu`)

#### 1. Store User Address
```http
POST /api/aiu/store
Content-Type: application/json

{
  "name": "John Doe",
  "phoneNumber": "9876543210",
  "digitalAddress": "Tribhuvan_nath@home",
  "daPin": "123456"
}

Response (201 CREATED):
{
  "id": 1,
  "name": "John Doe",
  "phoneNumber": "9876543210",
  "digitalAddress": "Tribhuvan_nath@home",
  "createdAt": "2025-12-02T10:30:00"
}
```

#### 2. Resolve Address with Consent
```http
POST /api/aiu/resolve-with-consent
Content-Type: application/json

{
  "digitalAddress": "Tribhuvan_nath@home",
  "daPin": "123456"
}

Response (200 OK):
{
  "digitalAddress": "Tribhuvan_nath@home",
  "generatedDigipin": "FC9-823-7654",
  "latitude": 28.6139,
  "longitude": 77.2090,
  "address": "Connaught Place, New Delhi, India",
  "confidenceScore": 80.0,
  "pincode": "110001",
  "verificationType": "AAVA"
}

Note: All resolutions are logged to ImmuDB for audit trail
```

#### 3. Submit Service Fulfillment Feedback
```http
POST /api/aiu/feedback
Content-Type: application/json

{
  "digitalAddress": "Tribhuvan_nath@home",
  "aiuIdentifier": "Flipkart_Delhi",
  "fulfillmentStatus": "SUCCESSFUL",   // or "FAILED", "PARTIALLY_SUCCESSFUL"
  "comments": "Package delivered successfully"
}

Response (200 OK):
{
  "message": "Service fulfillment feedback submitted successfully",
  "digitalAddress": "Tribhuvan_nath@home",
  "oldConfidenceScore": 80.0,
  "newConfidenceScore": 82.0,
  "fulfillmentStatus": "SUCCESSFUL",
  "aiuIdentifier": "Flipkart_Delhi",
  "totalFulfillments": 5,
  "isTrusted": true
}
```

---

### 📊 Audit & Verification APIs (`/api/audit`)

#### 1. Get Audit History
```http
GET /api/audit/audit-history/{digitalAddress}
Authorization: Bearer <token>

Example: GET /api/audit/audit-history/Tribhuvan_nath@home

Response (200 OK):
{
  "digitalAddress": "Tribhuvan_nath@home",
  "auditHistory": [
    {
      "eventType": "ADDRESS_CREATED",
      "timestamp": "2025-12-02T10:30:00",
      "userId": 1,
      "latitude": 28.6139,
      "longitude": 77.2090,
      "_txId": 42,
      "_verified": true
    },
    {
      "eventType": "AAVA_VERIFICATION",
      "timestamp": "2025-12-02T14:30:00",
      "agentId": "AAVA001",
      "verificationStatus": "VERIFIED",
      "oldScore": 50.0,
      "newScore": 80.0,
      "_txId": 58,
      "_verified": true
    },
    {
      "eventType": "ADDRESS_RESOLVED",
      "timestamp": "2025-12-02T15:00:00",
      "consentToken": "ABC123",
      "resolvedBy": "AIU",
      "_txId": 62,
      "_verified": true
    }
  ],
  "totalEvents": 3,
  "tamperProof": true,
  "cryptographicallyVerified": true
}
```

#### 2. Verify Audit Entry
```http
POST /api/audit/verify-audit
Authorization: Bearer <token>
Content-Type: application/json

{
  "auditKey": "address:create:Tribhuvan_nath@home:1733137800000"
}

Response (200 OK):
{
  "verified": true,
  "auditEntry": {
    "eventType": "ADDRESS_CREATED",
    "digitalAddress": "Tribhuvan_nath@home",
    "_txId": 42,
    "_verified": true
  },
  "message": "Data integrity verified - No tampering detected"
}

Error Response (if tampered):
{
  "verified": false,
  "tampered": true,
  "message": "WARNING: Data tampering detected!"
}
```

#### 3. Get Audit Statistics
```http
GET /api/audit/audit-stats
Authorization: Bearer <token>

Response (200 OK):
{
  "totalEvents": 150,
  "totalAddresses": 25,
  "totalResolutions": 75,
  "totalAavaVerifications": 10,
  "totalConfidenceUpdates": 40,
  "allVerified": true,
  "databaseHealth": "HEALTHY"
}
```

#### 4. Get Confidence Score
```http
GET /api/audit/confidence-score/{digitalAddress}

Example: GET /api/audit/confidence-score/Tribhuvan_nath@home

Response (200 OK):
{
  "digitalAddress": "Tribhuvan_nath@home",
  "confidenceScore": 82.0,
  "totalRatings": 10,
  "averageRating": 4.5,
  "totalFulfillments": 5,
  "threshold": 70.0,
  "isTrusted": true
}
```

---

### 📌 API Response Codes

| Code | Status | Description |
|------|--------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid request parameters |
| 401 | Unauthorized | Authentication required or invalid token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Data conflict (e.g., duplicate entry) |
| 500 | Internal Server Error | Server-side error |

---

### 🔑 Authentication

All protected endpoints require JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

Token expires after **343 hours** (~14 days). Obtain token via `/api/auth/login`.

## 🔐 Tamper-Proof Audit Trail (ImmuDB)

Every critical operation is automatically logged to **ImmuDB**, a cryptographically verifiable database that ensures complete data integrity and transparency.

### What Gets Logged?

| Event Type | Description | Key Data |
|------------|-------------|----------|
| **ADDRESS_CREATED** | New digital address created | User ID, coordinates, DigiPin, timestamp |
| **ADDRESS_RESOLVED** | AIU resolves address | Consent token, AIU identifier, timestamp |
| **AAVA_VERIFICATION** | Physical verification by AAVA | Agent ID, verification status, old/new scores |
| **CONFIDENCE_UPDATE** | Confidence score modified | Old score, new score, fulfillment status |

### ImmuDB Features

- ✅ **Immutable**: Data cannot be modified or deleted once written
- ✅ **Cryptographically Verified**: Each entry has a transaction ID and cryptographic signature
- ✅ **Complete History**: Full audit trail accessible via APIs
- ✅ **Tamper Detection**: Immediate detection of any manipulation attempts
- ✅ **Transaction IDs**: Every entry gets a unique ImmuDB transaction ID
- ✅ **Verification API**: Verify integrity of any audit entry

### Architecture

```
Application Event → ImmuDBService → ImmuDB (gRPC)
                                       ↓
                            Cryptographic Storage
                                       ↓
                          ┌────────────────────────┐
                          │  Key-Value Store       │
                          │  Event Type: Data      │
                          │  _txId: 42             │
                          │  _verified: true       │
                          └────────────────────────┘
```

### Example Audit Log

```json
{
  "eventType": "AAVA_VERIFICATION",
  "digitalAddress": "Tribhuvan_nath@home",
  "agentId": "AAVA001",
  "verificationStatus": "VERIFIED",
  "locationConfirmed": true,
  "oldConfidenceScore": 50.0,
  "newConfidenceScore": 80.0,
  "verifiedLatitude": 28.6139,
  "verifiedLongitude": 77.2090,
  "timestamp": "2025-12-02T14:30:00Z",
  "_txId": 58,
  "_verified": true
}
```

### How to Verify Integrity

Use the `/api/audit/verify-audit` endpoint to cryptographically verify any audit entry:

```http
POST /api/audit/verify-audit
{
  "auditKey": "address:create:Tribhuvan_nath@home:1733137800000"
}

✅ Response if verified:
{
  "verified": true,
  "message": "Data integrity verified - No tampering detected"
}

❌ Response if tampered:
{
  "verified": false,
  "tampered": true,
  "message": "WARNING: Data tampering detected!"
}
```

## 🔐 Security Features

### Multi-Layer Security Architecture

```
┌─────────────────────────────────────────────────────────┐
│ Layer 1: Transport Security (HTTPS/TLS)                │
├─────────────────────────────────────────────────────────┤
│ Layer 2: CORS Configuration (Allowed Origins)          │
├─────────────────────────────────────────────────────────┤
│ Layer 3: JWT Authentication (HMAC-SHA256)              │
├─────────────────────────────────────────────────────────┤
│ Layer 4: Spring Security Filter Chain                  │
├─────────────────────────────────────────────────────────┤
│ Layer 5: BCrypt Password & PIN Hashing                 │
├─────────────────────────────────────────────────────────┤
│ Layer 6: Ownership Validation (User Access Control)    │
├─────────────────────────────────────────────────────────┤
│ Layer 7: ImmuDB Audit (Tamper Detection)               │
├─────────────────────────────────────────────────────────┤

```

### 1. Authentication & Authorization

**JWT (JSON Web Tokens)**
- Algorithm: HMAC-SHA256
- Token Expiration: 343 hours (~14 days)
- Secret Key: 512-bit cryptographic key
- Claims: userId, username, email, authorities
- Stateless: No server-side session storage

**Spring Security**
- Custom JWT Authentication Filter (runs before UsernamePasswordAuthenticationFilter)
- SecurityFilterChain with HTTP Basic disabled
- CORS configuration for cross-origin requests
- UserDetailsService integration for user loading

**Role-Based Access Control**
- Users can only access their own resources
- Ownership validation on all sensitive operations
- AAVA agents have special verification privileges
- AIU systems have address resolution privileges

### 2. Data Protection

**Password Security**
- BCrypt hashing (strength: 10)
- Random salt per password
- No plain-text storage
- One-way hashing (cannot be reversed)

**Digital PIN Security**
- 6-digit PIN for consent verification
- BCrypt hashed (never stored plain)
- Verified on every address resolution
- Independent of user password

**PII Protection**
- Aadhaar numbers masked (only last 4 digits visible)
- Email and phone secured via JWT
- Address data accessible only with consent
- Audit logs contain minimal PII

### 3. Consent Management Security

**Token-Based Sharing**
- Unique consent tokens generated per address
- Token required for AIU address resolution
- Tokens can be revoked by deleting address
- Time-based expiry for TEMPORARY consents

**Consent Types**
- **PERMANENT**: No expiration, full trust
- **TEMPORARY**: Auto-expires after N days (1-365)
- Expired consents automatically deactivated
- Active consent tracking

### 4. Audit & Compliance

**ImmuDB Tamper-Proof Logging**
- All critical operations logged
- Immutable audit trail
- Cryptographic verification
- Tamper detection mechanism

**Compliance Features**
- Complete audit history
- Data integrity verification
- Traceability (transaction IDs)
- Non-repudiation (cannot deny actions)

### 5. Secure Verification Mechanisms

**Aadhaar Verification**
- Mock integration with 50+ test records
- Date of birth validation
- One-time verification per user
- Enables address creation

**AAVA Physical Verification**
- Government agent verification
- Location confirmation
- Identity verification
- Confidence score boost (50 → 80)
- Tamper-proof logging

### 6. API Security

**Input Validation**
- Jakarta Validation annotations
- Request body validation
- Parameter type checking
- SQL injection prevention (JPA)

**Error Handling**
- Generic error messages (no sensitive data leakage)
- Proper HTTP status codes
- Logging for debugging
- User-friendly error responses

### 7. Configuration Security

**Secure Properties**
```properties
# JWT Secret (512-bit)
jwt.secret=<long-random-string>
jwt.expiration=1234000000  # ~14 days

# Database Credentials (should be externalized in production)
spring.datasource.username=root
spring.datasource.password=<secure-password>

# ImmuDB Credentials
immudb.username=immudb
immudb.password=<secure-password>
```

**Best Practices**
- Use environment variables for secrets in production
- Rotate JWT secret regularly
- Use strong database passwords
- Enable HTTPS in production
- Regular security audits

### 8. Security Recommendations for Production

- ✅ Enable HTTPS/TLS
- ✅ Use environment variables for secrets
- ✅ Implement IP whitelisting for AAVA endpoints
- ✅ Set up WAF (Web Application Firewall)
- ✅ Regular security scanning (OWASP ZAP, SonarQube)
- ✅ Implement API gateway for centralized security
- ✅ Enable database encryption at rest
- ✅ Set up intrusion detection system (IDS)
- ✅ Regular penetration testing
- ✅ Implement security headers (CSP, HSTS, etc.)

## 🧮 DigiPin Algorithm

The system uses a proprietary **grid-based recursive subdivision algorithm** to generate unique, human-readable location codes for any coordinates in India.

### Algorithm Overview

```
┌─────────────────────────────────────────────────────────┐
│              4x4 Character Grid System                  │
├─────────────────────────────────────────────────────────┤
│                                                          │
│   ┌───┬───┬───┬───┐                                    │
│   │ F │ C │ 9 │ 8 │  Row 0 (North)                    │
│   ├───┼───┼───┼───┤                                    │
│   │ J │ 3 │ 2 │ 7 │  Row 1                            │
│   ├───┼───┼───┼───┤                                    │
│   │ K │ 4 │ 5 │ 6 │  Row 2                            │
│   ├───┼───┼───┼───┤                                    │
│   │ L │ M │ P │ T │  Row 3 (South)                    │
│   └───┴───┴───┴───┘                                    │
│    Col0 Col1 Col2 Col3                                 │
│    (West)      (East)                                  │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Coverage Area

- **Region**: India
- **Latitude**: 2.5°N to 38.5°N (36° range)
- **Longitude**: 63.5°E to 99.5°E (36° range)
- **Total Coverage**: ~36° × 36° bounding box

### Algorithm Steps

1. **Initialize**: Start with India's bounding box
2. **Divide**: Split into 4×4 grid (16 cells)
3. **Locate**: Find cell containing target coordinates
4. **Encode**: Append cell's character to DigiPin
5. **Subdivide**: Zoom into identified cell
6. **Repeat**: Perform 10 levels of subdivision
7. **Format**: Add hyphens at positions 3 and 6

### Output Format

```
Format: XXX-XXX-XXXX
Example: FC9-823-7654

Structure:
├─ Characters 1-3: Primary grid (large area)
├─ Characters 4-6: Secondary grid (medium area)
└─ Characters 7-10: Tertiary grid (precise location)
```

### Precision & Accuracy

| Level | Characters | Grid Size | Approximate Accuracy |
|-------|-----------|-----------|---------------------|
| 1 | F | 9° × 9° | ~1000 km |
| 2 | FC | 2.25° × 2.25° | ~250 km |
| 3 | FC9 | 0.56° × 0.56° | ~60 km |
| 6 | FC9-823 | ~3.4 km × 3.4 km | ~3 km |
| 10 | FC9-823-7654 | ~13 m × 13 m | **~10 meters** |

### Code Example

```java
public static String getDigiPin(double lat, double lon) {
    // Grid characters
    char[][] DIGIPIN_GRID = {
        {'F', 'C', '9', '8'},
        {'J', '3', '2', '7'},
        {'K', '4', '5', '6'},
        {'L', 'M', 'P', 'T'}
    };
    
    double minLat = 2.5, maxLat = 38.5;
    double minLon = 63.5, maxLon = 99.5;
    
    StringBuilder digiPin = new StringBuilder();
    
    for (int level = 1; level <= 10; level++) {
        // Calculate cell dimensions
        double latDiv = (maxLat - minLat) / 4.0;
        double lonDiv = (maxLon - minLon) / 4.0;
        
        // Find row and column
        int row = 3 - (int)((lat - minLat) / latDiv);
        int col = (int)((lon - minLon) / lonDiv);
        
        // Clamp to valid range
        row = Math.max(0, Math.min(row, 3));
        col = Math.max(0, Math.min(col, 3));
        
        // Append character
        digiPin.append(DIGIPIN_GRID[row][col]);
        
        // Add hyphens at positions 3 and 6
        if (level == 3 || level == 6) 
            digiPin.append('-');
        
        // Update bounds for next level (zoom in)
        maxLat = minLat + latDiv * (4 - row);
        minLat = minLat + latDiv * (3 - row);
        minLon = minLon + lonDiv * col;
        maxLon = minLon + lonDiv;
    }
    
    return digiPin.toString();
}
```

### Real-World Examples

| Location | Coordinates | Generated DigiPin |
|----------|-------------|-------------------|
| Connaught Place, Delhi | 28.6139°N, 77.2090°E | FC9-823-7654 |
| India Gate, Delhi | 28.6129°N, 77.2295°E | FC9-827-3245 |
| Gateway of India, Mumbai | 18.9220°N, 72.8347°E | J32-456-7890 |
| Bangalore Palace | 12.9980°N, 77.5926°E | K25-678-1234 |

### Advantages

✅ **Human-Readable**: Easy to remember and share
✅ **Consistent**: Same coordinates always generate same DigiPin
✅ **Hierarchical**: First characters indicate general area
✅ **Compact**: 12 characters (including hyphens) vs long coordinates
✅ **India-Specific**: Optimized for Indian geography
✅ **No External Dependencies**: Pure algorithmic generation

### Use Cases

1. **Address Sharing**: Share DigiPin instead of long coordinates
2. **Quick Lookup**: Enter DigiPin to get approximate location
3. **Hierarchical Search**: Search by prefix (e.g., all addresses starting with "FC9")
4. **Offline Maps**: Works without internet connectivity
5. **Emergency Services**: Quick location identification

## 📊 Database Schema

### MySQL Database (Primary Transactional Data)

#### 🧑 Users Table
```sql
CREATE TABLE users (
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_name            VARCHAR(50) UNIQUE NOT NULL,
    phone_number         VARCHAR(15) UNIQUE NOT NULL,
    email_id             VARCHAR(100) UNIQUE,
    password             VARCHAR(255) NOT NULL,      -- BCrypt hashed
    aadhaar_number       VARCHAR(12) UNIQUE,
    is_aadhaar_verified  BOOLEAN DEFAULT FALSE,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 🏠 Digital Addresses Table
```sql
CREATE TABLE digital_addresses (
    id                          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id                     BIGINT NOT NULL,
    digital_address             VARCHAR(100) UNIQUE NOT NULL,  -- username@suffix
    generated_digipin           VARCHAR(20) NOT NULL,          -- FC9-823-7654
    suffix                      VARCHAR(50),
    latitude                    DOUBLE NOT NULL,
    longitude                   DOUBLE NOT NULL,
    address                     TEXT,
    pin_code                    VARCHAR(10),
    active_consent_id           BIGINT,
    has_active_consent          BOOLEAN DEFAULT FALSE,
    
    -- Trust & Verification
    confidence_score            DOUBLE DEFAULT 50.0,           -- 0-100
    total_ratings               INT DEFAULT 0,
    average_rating              DOUBLE DEFAULT 0.0,
    total_fulfillments          INT DEFAULT 0,
    
    -- AAVA Verification
    requires_aava_verification  BOOLEAN DEFAULT FALSE,
    is_aava_verified            BOOLEAN DEFAULT FALSE,
    verification_type           ENUM('SELF','AAVA') DEFAULT 'SELF',
    aava_agent_id               VARCHAR(50),
    aava_verified_at            TIMESTAMP,
    aava_verification_notes     TEXT,
    verified_latitude           DOUBLE,
    verified_longitude          DOUBLE,
    
    created_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (active_consent_id) REFERENCES consents(id) ON DELETE SET NULL
);
```

#### 🔐 Consents Table
```sql
CREATE TABLE consents (
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id              BIGINT NOT NULL,
    digital_address_id   BIGINT NOT NULL,
    upi_pin_hash         VARCHAR(255) NOT NULL,      -- BCrypt hashed
    consent_token        VARCHAR(10) UNIQUE NOT NULL,
    consent_type         ENUM('PERMANENT','TEMPORARY') NOT NULL,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at           TIMESTAMP NULL,
    is_active            BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (digital_address_id) REFERENCES digital_addresses(id) ON DELETE CASCADE
);
```

#### 🆔 Aadhaar Mock Data Table (Testing)
```sql
CREATE TABLE aadhaar_mock_data (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    aadhaar_number   VARCHAR(12) UNIQUE NOT NULL,
    full_name        VARCHAR(100) NOT NULL,
    date_of_birth    DATE NOT NULL,
    gender           VARCHAR(10),
    address          TEXT,
    state            VARCHAR(50),
    pincode          VARCHAR(10),
    mobile_number    VARCHAR(15)
);

-- Contains 50 pre-loaded test records
-- Example: 234567890123, "Rajesh Kumar Sharma", 1990-05-15, ...
```

#### 📫 User Addresses Table (AIU Storage)
```sql
CREATE TABLE user_addresses (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    name             VARCHAR(100),
    phone_number     VARCHAR(15),
    digital_address  VARCHAR(100),
    upi_pin          VARCHAR(6),
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### ImmuDB Database (Tamper-Proof Audit Trail)

**Storage Type**: Key-Value Store (Cryptographically Verified)

#### Event Keys Format
```
<event_type>:<identifier>:<timestamp>

Examples:
- address:create:Tribhuvan_nath@home:1733137800000
- address:resolve:Tribhuvan_nath@home:1733138100000
- aava:verify:Tribhuvan_nath@home:1733140000000
- confidence:update:Tribhuvan_nath@home:1733141000000
```

#### Event Value Format (JSON)
```json
{
  "eventType": "ADDRESS_CREATED",
  "digitalAddress": "Tribhuvan_nath@home",
  "userId": 1,
  "latitude": 28.6139,
  "longitude": 77.2090,
  "generatedDigipin": "FC9-823-7654",
  "consentType": "PERMANENT",
  "timestamp": "2025-12-02T10:30:00Z",
  "_txId": 42,                    // ImmuDB transaction ID
  "_verified": true                // Cryptographic verification status
}
```

### Entity Relationships

```
┌─────────┐         ┌─────────────────┐         ┌──────────┐
│  User   │1      * │ DigitalAddress  │1      * │ Consent  │
│         ├────────→│                 ├────────→│          │
│ id      │         │ id              │         │ id       │
│userName │         │ userId (FK)     │         │ userId   │
│aadhaar  │         │ digitalAddress  │         │ token    │
└─────────┘         │ digipin         │         │ daPin    │
                    │ coordinates     │         │ type     │
                    │ confidenceScore │         │ expires  │
                    │ isAavaVerified  │         └──────────┘
                    └─────────────────┘
                            │
                            │1
                            │
                            │*
                    ┌───────────────┐
                    │   ImmuDB      │
                    │  Audit Logs   │
                    │               │
                    │ • Creation    │
                    │ • Resolution  │
                    │ • AAVA        │
                    │ • Confidence  │
                    └───────────────┘
```

### Indexes & Constraints

**Primary Keys**: All tables have auto-increment primary keys
**Unique Constraints**: 
- users.user_name, users.phone_number, users.email_id, users.aadhaar_number
- digital_addresses.digital_address
- consents.consent_token

**Foreign Keys**:
- digital_addresses.user_id → users.id
- consents.user_id → users.id
- consents.digital_address_id → digital_addresses.id

**Cascade Rules**:
- ON DELETE CASCADE: User deletion removes all addresses and consents
- ON DELETE SET NULL: Consent deletion sets active_consent_id to NULL

## 🎯 Use Cases & User Flows

### Use Case 1: E-Commerce Delivery 🛒

**Scenario**: Customer orders from Flipkart and shares digital address for delivery

```
1. Customer Registration
   └─→ POST /api/auth/register (Tribhuvan creates account)
   
2. Aadhaar Verification
   └─→ POST /api/auth/verify-aadhaar (KYC completed)
   
3. Create Digital Address
   └─→ POST /api/digital-address/create
       Input: {
         "suffix": "home",
         "latitude": 28.6139,
         "longitude": 77.2090,
         "daPin": "123456",
         "consentType": "PERMANENT"
       }
       Output: Tribhuvan_nath@home (DigiPin: FC9-823-7654)
   
4. Place Order on Flipkart
   └─→ Customer shares: Tribhuvan_nath@home + DA PIN (123456)
   
5. Flipkart (AIU) Resolves Address
   └─→ POST /api/aiu/resolve-with-consent
       Input: { "digitalAddress": "Tribhuvan_nath@home", "daPin": "123456" }
       Output: { lat: 28.6139, lon: 77.2090, address: "...", score: 50 }
       
6. Delivery Executed
   └─→ Delivery agent uses GPS coordinates
   
7. Flipkart Submits Feedback
   └─→ POST /api/aiu/feedback
       Input: { "fulfillmentStatus": "SUCCESSFUL" }
       Result: Confidence score: 50 → 52 ✅
   
8. Audit Logged
   └─→ All steps logged to ImmuDB (tamper-proof)
```

---

### Use Case 2: Government Welfare Scheme 🏛️

**Scenario**: Citizen applies for Pradhan Mantri Awas Yojana (housing scheme)

```
1. Citizen Creates Address
   └─→ Priya creates digital address: Priya_Singh@home
   
2. Government Requires AAVA Verification
   └─→ Scheme mandates physical verification for authenticity
   
3. Flag for AAVA
   └─→ POST /api/digital-address/flag-for-aava
       Input: { "reason": "Required for PM Awas Yojana" }
       Result: Address marked for AAVA verification
   
4. AAVA Agent Assignment
   └─→ Agent AAVA001 assigned to verify address
   
5. Physical Verification
   └─→ Agent visits location, verifies:
       ✓ Identity (Aadhaar)
       ✓ Location accuracy (GPS)
       ✓ Resident confirmation
   
6. Submit Verification
   └─→ POST /api/aava/aava-verify
       Input: {
         "agentId": "AAVA001",
         "verificationStatus": "VERIFIED",
         "locationConfirmed": true,
         "verifiedLatitude": 28.6139,
         "verifiedLongitude": 77.2090
       }
       Result: 
       - Confidence score: 50 → 80 🚀
       - Verification type: SELF → AAVA
       - AAVA verified: true
   
7. Government Approval
   └─→ GET /api/aava/aava-status/{address}
       Checks:
       - isAavaVerified: true ✅
       - confidenceScore: 80 ✅
       - approvedUseCases.governmentWelfare: true ✅
       
8. Benefit Disbursed
   └─→ Address approved for welfare scheme
   
9. Complete Audit Trail
   └─→ GET /api/audit/audit-history/{address}
       Shows:
       - Address creation
       - AAVA verification
       - Score updates
       - All with ImmuDB transaction IDs
```

---

### Use Case 3: Emergency Services 🚑

**Scenario**: User calls ambulance during medical emergency

```
1. Emergency Call
   └─→ User: "I need an ambulance at Tribhuvan_nath@home"
   
2. Emergency System Resolves Address
   └─→ POST /api/aiu/resolve-with-consent
       Input: { "digitalAddress": "Tribhuvan_nath@home", "daPin": "123456" }
       Output: Immediate GPS coordinates
       
3. Dispatch Ambulance
   └─→ Driver receives: 28.6139°N, 77.2090°E
   
4. Real-Time Navigation
   └─→ GPS guides to exact location (~10m accuracy)
   
5. Service Completed
   └─→ POST /api/aiu/feedback
       Input: { "fulfillmentStatus": "SUCCESSFUL" }
       
Note: Emergency services approved even without AAVA verification
      (confidenceScore >= 50 is sufficient)
```

---

### Use Case 4: Property Rental 🏡

**Scenario**: Landlord shares property address with tenant

```
1. Landlord Creates Address
   └─→ Amit creates: Amit_Patel@villa7
   
2. AAVA Verification (Optional)
   └─→ Increases trust for property records
   
3. Share with Tenant
   └─→ Landlord shares: Amit_Patel@villa7 + Temporary PIN
   
4. Create Temporary Consent
   └─→ PUT /api/digital-address/update
       Input: {
         "consentType": "TEMPORARY",
         "consentDurationDays": 30
       }
       Result: Consent expires in 30 days
   
5. Tenant Access
   └─→ Tenant uses address for:
       - Moving services
       - Utility setup
       - Delivery services
   
6. Auto-Expiry
   └─→ After 30 days, consent auto-deactivates
       Tenant can no longer resolve address
```

---

### Use Case 5: Food Delivery 🍕

**Scenario**: Customer orders food from Swiggy

```
1. Order Placement
   └─→ Customer shares: Sneha_Reddy@apartment + PIN
   
2. Restaurant Prepares Food
   └─→ Swiggy (AIU) resolves address in background
   
3. Delivery Partner Assignment
   └─→ GET coordinates: 17.4400°N, 78.3489°E
   
4. Real-Time Tracking
   └─→ Customer tracks delivery on app
   
5. Successful Delivery
   └─→ Swiggy submits: "SUCCESSFUL"
       Result: Score increases
   
6. Multiple Deliveries
   └─→ After 10 successful deliveries:
       Score: 50 → 70 (HIGH TRUST) 🌟
```

---

### Use Case 6: Audit Verification (Compliance) 📋

**Scenario**: Auditor verifies data integrity

```
1. Request Audit History
   └─→ GET /api/audit/audit-history/Tribhuvan_nath@home
       Output: Complete event log with timestamps
   
2. Verify Specific Entry
   └─→ POST /api/audit/verify-audit
       Input: { "auditKey": "address:create:..." }
       
3. Check Integrity
   └─→ Response: 
       {
         "verified": true,
         "_txId": 42,
         "message": "Data integrity verified - No tampering detected"
       }
   
4. Tamper Detection (If Applicable)
   └─→ If data tampered:
       {
         "verified": false,
         "tampered": true,
         "message": "WARNING: Data tampering detected!"
       }
   
5. Generate Compliance Report
   └─→ GET /api/audit/audit-stats
       Shows: Total events, verifications, health status
```

---

### Confidence Score Evolution 📈

```
Initial: 50.0 (SELF-verified)
    │
    ├─→ +2.0 (Successful delivery) → 52.0
    ├─→ +2.0 (Successful delivery) → 54.0
    ├─→ +2.0 (Successful delivery) → 56.0
    │
    ├─→ AAVA Verification → 80.0 🚀 (TRUSTED)
    │
    ├─→ +2.0 (Successful delivery) → 82.0
    ├─→ -5.0 (Failed delivery) → 77.0
    ├─→ +2.0 (Successful delivery) → 79.0
    │
    └─→ Final: 79.0 (HIGH TRUST)

Thresholds:
├─ 0-49:   Low Trust (Some services may reject)
├─ 50-69:  Medium Trust (Basic services)
├─ 70-79:  High Trust (Most services)
└─ 80-100: Very High Trust (All services, govt verified)
```

## 🛠️ Development

### Build Commands

```bash
# Clean and build
mvn clean install

# Run tests
mvn test

# Package application (creates JAR in target/)
mvn package

# Run application
mvn spring-boot:run

# Skip tests during build
mvn clean install -DskipTests

# Run specific test class
mvn test -Dtest=DigipinApplicationTests

# Generate project site with reports
mvn site
```

### Configuration Properties

Key configurations in `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/digipin
spring.datasource.username=root
spring.datasource.password=Reyansh
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=<512-bit-secret-key>
jwt.expiration=1234000000          # ~343 hours (~14 days)

# ImmuDB Configuration
immudb.host=localhost
immudb.port=3322                    # gRPC port
immudb.database=digipin_audit
immudb.username=immudb
immudb.password=immudb

# Logging
logging.level.com.hackathon.resolutionconsent.digipin=DEBUG
logging.level.org.springframework.web=INFO

# Application Name
spring.application.name=digipin

# AAVA Trusted Credentials (Demo)
aava.email=aava@trusted.gov.in
aava.password=123456
```

### Environment Variables (Production)

For production deployment, use environment variables instead of hardcoded values:

```bash
# Database
export MYSQL_HOST=production-db-host
export MYSQL_PORT=3306
export MYSQL_DATABASE=digipin
export MYSQL_USERNAME=digipin_user
export MYSQL_PASSWORD=<secure-password>

# JWT
export JWT_SECRET=<strong-512-bit-secret>
export JWT_EXPIRATION=1234000000

# ImmuDB
export IMMUDB_HOST=immudb-host
export IMMUDB_PORT=3322
export IMMUDB_DATABASE=digipin_audit
export IMMUDB_USERNAME=immudb
export IMMUDB_PASSWORD=<secure-password>

# Server
export SERVER_PORT=8080
```

Update `application.properties` to use environment variables:

```properties
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:digipin}
spring.datasource.username=${MYSQL_USERNAME:root}
spring.datasource.password=${MYSQL_PASSWORD:Reyansh}
jwt.secret=${JWT_SECRET:default-secret}
```

### Docker Compose Setup

The project includes a `docker-compose.yml` for running MySQL and ImmuDB:

```yaml
version: '3.8'

services:
  immudb:
    image: codenotary/immudb:latest
    container_name: digipin-immudb
    ports:
      - "3322:3322"   # gRPC API
      - "9497:9497"   # HTTP API
    environment:
      - IMMUDB_ADMIN_PASSWORD=immudb
    volumes:
      - immudb-data:/var/lib/immudb
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9497/api/v1/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  mysql:
    image: mysql:8.0
    container_name: digipin-mysql
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=Reyansh
      - MYSQL_DATABASE=digipin
    volumes:
      - mysql-data:/var/lib/mysql
      - ./aadhaar_mock_data.sql:/docker-entrypoint-initdb.d/aadhaar_mock_data.sql
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  immudb-data:
  mysql-data:
```

### Running with Docker

```bash
# Start services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Testing with Mock Data

The project includes 50 pre-loaded Aadhaar records in `aadhaar_mock_data.sql`:

```sql
-- Example records
('234567890123', 'Rajesh Kumar Sharma', '1990-05-15', ...)
('345678901234', 'Priya Singh', '1995-08-22', ...)
('456789012345', 'Amit Patel', '1988-12-10', ...)
...
```

Use these for testing Aadhaar verification:

```http
POST /api/auth/verify-aadhaar
{
  "aadhaarNumber": "234567890123",
  "dateOfBirth": "1990-05-15"
}
```

### IDE Setup

**IntelliJ IDEA:**
1. Import as Maven project
2. Enable annotation processing (Lombok)
   - Settings → Build → Compiler → Annotation Processors
   - Check "Enable annotation processing"
3. Install Lombok plugin
4. Configure JDK 21

**VS Code:**
1. Install Extension Pack for Java
2. Install Spring Boot Extension Pack
3. Install Lombok Annotations Support
4. Open folder and let Java extension activate

**Eclipse:**
1. Import as Existing Maven Project
2. Install Lombok (download lombok.jar and run)
3. Enable annotation processing
4. Configure Java 21 compliance

### Debugging

**Enable Debug Logging:**
```properties
logging.level.com.hackathon.resolutionconsent.digipin=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

**IntelliJ IDEA Debug:**
1. Set breakpoints in code
2. Run → Debug 'DigipinApplication'
3. Use Debug Console for expressions

**Remote Debugging:**
```bash
# Run with remote debug enabled
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Connect from IDE to localhost:5005
```

### Code Quality Tools

**Maven Plugins:**
```xml
<!-- Checkstyle -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
</plugin>

<!-- SpotBugs -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
</plugin>

<!-- JaCoCo (Code Coverage) -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
</plugin>
```

### Performance Monitoring

**Spring Boot Actuator** (Add dependency):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Endpoints:**
- `/actuator/health` - Application health
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application info

### Common Issues & Solutions

**Issue: Port 8080 already in use**
```bash
# Find process using port 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac

# Kill process or change port in application.properties
```

**Issue: MySQL connection failed**
```bash
# Check MySQL is running
docker-compose ps

# Check credentials in application.properties
# Verify database exists: CREATE DATABASE digipin;
```

**Issue: ImmuDB connection failed**
```bash
# Check ImmuDB is running
curl http://localhost:9497/api/v1/health

# Restart ImmuDB
docker-compose restart immudb
```

**Issue: Lombok not working**
```bash
# Rebuild project
mvn clean install

# Enable annotation processing in IDE
# Reinstall Lombok plugin
```

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

```
Copyright 2025 Tribhuvan-Web

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Contribution Guidelines

1. **Fork the repository**
   ```bash
   git clone https://github.com/Tribhuvan-Web/digipin-backend.git
   cd digipin-backend
   ```

2. **Create your feature branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```

3. **Make your changes**
   - Follow existing code style
   - Add tests for new features
   - Update documentation as needed
   - Ensure all tests pass: `mvn test`

4. **Commit your changes**
   ```bash
   git add .
   git commit -m 'Add some AmazingFeature'
   ```

5. **Push to the branch**
   ```bash
   git push origin feature/AmazingFeature
   ```

6. **Open a Pull Request**
   - Provide clear description of changes
   - Reference any related issues
   - Wait for review and feedback

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Keep methods small and focused
- Use Lombok annotations to reduce boilerplate
- Write unit tests for business logic

### Reporting Issues

Found a bug or have a feature request? Please open an issue:

1. Check if issue already exists
2. Use issue template if available
3. Provide detailed description
4. Include steps to reproduce (for bugs)
5. Add relevant logs or screenshots

## 📚 Documentation

- **[README.md](README.md)** - This file (Quick start & API reference)
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Detailed system architecture
- **[LICENSE](LICENSE)** - Apache 2.0 License

## 🔗 Links & Resources

### Project Links
- **GitHub Repository**: [https://github.com/Tribhuvan-Web/digipin-backend](https://github.com/Tribhuvan-Web/digipin-backend)
- **Issue Tracker**: [GitHub Issues](https://github.com/Tribhuvan-Web/digipin-backend/issues)
- **Pull Requests**: [GitHub PRs](https://github.com/Tribhuvan-Web/digipin-backend/pulls)

### Technology Documentation
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [MySQL 8.0 Reference](https://dev.mysql.com/doc/refman/8.0/en/)
- [ImmuDB Documentation](https://docs.immudb.io/)
- [JWT.io](https://jwt.io/)
- [Docker Documentation](https://docs.docker.com/)

### Related Projects
- [Aadhaar Authentication](https://uidai.gov.in/)
- [India Post PIN Code](https://www.indiapost.gov.in/)
- [Digital India Initiative](https://www.digitalindia.gov.in/)

## 🎓 Learning Resources

### Spring Boot & Security
- [Spring Boot Tutorial - Baeldung](https://www.baeldung.com/spring-boot)
- [Spring Security Tutorial](https://www.baeldung.com/security-spring)
- [JWT with Spring Boot](https://www.baeldung.com/spring-security-oauth-jwt)

### Database & JPA
- [JPA Tutorial](https://www.baeldung.com/learn-jpa-hibernate)
- [MySQL Best Practices](https://dev.mysql.com/doc/refman/8.0/en/optimization.html)
- [ImmuDB Getting Started](https://docs.immudb.io/master/quickstart.html)

## 📧 Contact & Support

### Project Maintainer
**Tribhuvan-Web**
- GitHub: [@Tribhuvan-Web](https://github.com/Tribhuvan-Web)
- Repository: [digipin-backend](https://github.com/Tribhuvan-Web/digipin-backend)

### Getting Help
- 📖 Check [Documentation](README.md)
- 🐛 Report [Issues](https://github.com/Tribhuvan-Web/digipin-backend/issues)
- 💬 Start a [Discussion](https://github.com/Tribhuvan-Web/digipin-backend/discussions)
- ⭐ Star the repository if you find it useful!

## 🙏 Acknowledgments

- **Spring Team** - For the amazing Spring Boot framework
- **ImmuDB Team** - For the tamper-proof database solution
- **Aaditya kumar** - For continuous support and contributions

## 🏆 Project Stats

```
├─ 5 Controllers (Auth, DigitalAddress, AAVA, AIU, Audit)
├─ 5 Services (Business logic)
├─ 5 Repositories (Data access)
├─ 5 Entities (Database models)
├─ 10+ DTOs (Data transfer objects)
├─ 2 Databases (MySQL + ImmuDB)
├─ 25+ API Endpoints
├─ 50 Mock Aadhaar Records
├─ Complete Audit Trail
└─ Tamper-Proof Logging
```

## 🚦 Project Status

- ✅ **Authentication & Authorization**: Complete
- ✅ **Digital Address Management**: Complete
- ✅ **Consent Management**: Complete
- ✅ **AAVA Verification**: Complete
- ✅ **AIU Integration**: Complete
- ✅ **Audit Trail (ImmuDB)**: Complete
- ✅ **Confidence Scoring**: Complete

## 🔮 Future Enhancements

### Planned Features
- [ ] **Mobile App** - iOS & Android applications
- [ ] **Real-time Notifications** - WebSocket integration
- [ ] **Multi-language Support** - i18n for regional languages
- [ ] **Advanced Analytics** - User behavior & usage patterns
- [ ] **AI/ML Integration** - Address validation & fraud detection
- [ ] **Blockchain Integration** - Additional tamper-proof layer
- [ ] **Multi-tenancy** - Support for multiple organizations
- [ ] **OAuth2 Integration** - Social login (Google, Facebook)
- [ ] **SMS/Email Notifications** - OTP verification
- [ ] **API Gateway** - Centralized API management
- [ ] **Microservices Architecture** - Service decomposition

### Performance Improvements
- [ ] Redis caching for frequently accessed data
- [ ] Database read replicas for load distribution
- [ ] CDN integration for static assets
- [ ] Connection pooling optimization
- [ ] Query optimization & indexing

### Security Enhancements
- [ ] Two-factor authentication (2FA)
- [ ] Biometric authentication
- [ ] IP whitelisting for AAVA endpoints
- [ ] API key management for AIU
- [ ] Enhanced rate limiting per endpoint
- [ ] Web Application Firewall (WAF)

---