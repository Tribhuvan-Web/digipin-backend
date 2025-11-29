# 🎯 DigiPin - Digital Address with Consent Management System

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)

> A secure and innovative digital address resolution system with UNIQUE PIN-based consent management for seamless address sharing.

## 🌟 Overview

DigiPin revolutionizes how digital addresses are created, managed, and shared. It combines location-based digital addressing with a secure consent mechanism, allowing users to share their addresses with authorized parties using a simple 6-digit UNIQUE PIN.

### Key Features

- 🗺️ **Location-Based Digital Addresses** - Generate unique digital addresses from GPS coordinates
- 🔐 **UNIQUE PIN Authentication** - Secure 6-digit PIN for address access control
- 📍 **DigiPin Generation** - Proprietary algorithm for creating memorable location codes
- 🔑 **Consent Management** - Granular control over who can access your address
- ⏰ **Temporary/Permanent Consents** - Flexible time-based access permissions
- 🛡️ **Aadhaar Verification** - KYC integration for trusted identity verification
- 🔒 **JWT Authentication** - Secure API access with token-based auth
- 🚀 **RESTful APIs** - Clean and well-documented API endpoints
- 🔐 **ImmuDB Integration** - Tamper-proof, cryptographically verifiable audit trail

## 🏗️ Architecture

### Technology Stack

**Backend Framework:**
- Spring Boot 3.5.8
- Spring Security
- Spring Data JPA
- Hibernate ORM

**Database:**
- MySQL (Primary transactional data)
- ImmuDB (Tamper-proof audit trail)

**Security:**
- JWT (JSON Web Tokens)
- BCrypt Password Encryption
- Spring Security
- ImmuDB Cryptographic Verification

**Additional Libraries:**
- Lombok (Code Generation)
- Jakarta Validation

### Project Structure (MVC Pattern)

```
digipin/
├── src/main/java/com/hackathon/resolutionconsent/digipin/
│   ├── config/                    # Configuration classes
│   │   ├── SecurityConfig.java
│   │   └── JwtAuthenticationFilter.java
│   │
│   ├── controller/                # REST Controllers (View Layer)
│   │   ├── AuthController.java
│   │   └── DigitalAddressController.java
│   │
│   ├── service/                   # Business Logic Layer
│   │   ├── AuthService.java
│   │   ├── ConsentService.java
│   │   ├── DigitalAddressService.java
│   │   └── CustomUserDetailsService.java
│   │
│   ├── models/                    # Entity Models (Model Layer)
│   │   ├── User.java
│   │   ├── DigitalAddress.java
│   │   ├── Consent.java
│   │   └── AadhaarMockData.java
│   │
│   ├── repository/                # Data Access Layer
│   │   ├── UserRepository.java
│   │   ├── DigitalAddressRepository.java
│   │   ├── ConsentRepository.java
│   │   └── AadhaarMockDataRepository.java
│   │
│   ├── dto/                       # Data Transfer Objects
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── CreateDigitalAddressRequest.java
│   │   ├── UpdateDigitalAddressRequest.java
│   │   ├── ConsentResponse.java
│   │   └── UserProfileResponse.java
│   │
│   └── util/                      # Utility Classes
│       ├── JwtUtil.java
│       └── UserDetailsImplement.java
│
└── src/main/resources/
    └── application.properties     # Application Configuration
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

> 📖 **For detailed ImmuDB setup and tamper-proof audit features, see [IMMUDB_SETUP.md](IMMUDB_SETUP.md)**

## 📡 API Endpoints

### Authentication APIs

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "userName": "Tribhuvan_nath",
  "phoneNumber": "9876543210",
  "emailId": "tribhuvan@example.com",
  "password": "securePassword123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "emailOrPhone": "tribhuvan@example.com",
  "password": "securePassword123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Verify Aadhaar
```http
POST /api/auth/verify-aadhaar
Authorization: Bearer <token>
Content-Type: application/json

{
  "aadhaarNumber": "123456789012",
  "dateOfBirth": "1990-01-15"
}
```

#### Get User Profile
```http
GET /api/auth/profile
Authorization: Bearer <token>

Response:
{
  "id": 1,
  "userName": "Tribhuvan_nath",
  "phoneNumber": "9876543210",
  "emailId": "tribhuvan@example.com",
  "aadhaarNumber": "********9012",
  "isAadhaarVerified": true
}
```

### Digital Address APIs

#### Create Digital Address
```http
POST /api/digital-address/create
Authorization: Bearer <token>
Content-Type: application/json

{
  "suffix": "home",
  "latitude": 28.6139,
  "longitude": 77.2090,
  "address": "New Delhi, India",
  "upiPin": "123456",
  "consentType": "PERMANENT",
  "consentDurationDays": 30
}

Response:
{
  "consentToken": "A7K9B2",
  "consentType": "PERMANENT",
  "expiresAt": "Never",
  "message": "Digital address created successfully with consent"
}
```

#### Update Digital Address
```http
PUT /api/digital-address/update
Authorization: Bearer <token>
Content-Type: application/json

{
  "latitude": 28.7041,
  "longitude": 77.1025,
  "address": "Updated Address",
  "upiPin": "123456",
  "consentType": "TEMPORARY",
  "consentDurationDays": 60
}
```

#### Get Digital Address by ID
```http
GET /api/digital-address/{id}
Authorization: Bearer <token>
```

#### Resolve Address with Consent (AIU Access)
```http
POST /api/digital-address/resolve-with-consent
Content-Type: application/json

{
  "digitalAddress": "Tribhuvan_nath@home",
  "consentToken": "123456"
}

Response:
{
  "digitalAddress": "Tribhuvan_nath@home",
  "generatedDigipin": "FC9-823-7654",
  "latitude": 28.6139,
  "longitude": 77.2090,
  "address": "New Delhi, India",
  "consentType": "PERMANENT",
  "consentExpiresAt": "Never"
}
```

### Audit Trail APIs (ImmuDB)

#### Get Audit History
```http
GET /api/digital-address/audit-history/{digitalAddress}
Authorization: Bearer <token>

Response:
{
  "digitalAddress": "Tribhuvan_nath@home",
  "auditHistory": [
    {
      "eventType": "ADDRESS_CREATED",
      "timestamp": "2025-11-30T10:30:00",
      "userId": 1,
      "_txId": 42,
      "_verified": true
    }
  ],
  "totalEvents": 1,
  "tamperProof": true,
  "cryptographicallyVerified": true
}
```

#### Verify Audit Entry
```http
POST /api/digital-address/verify-audit
Authorization: Bearer <token>
Content-Type: application/json

{
  "auditKey": "address:create:Tribhuvan_nath@home:1732960200000"
}

Response:
{
  "verified": true,
  "message": "Data integrity verified - No tampering detected"
}
```

## 🔐 Tamper-Proof Audit Trail

Every operation (address creation, updates, consent resolutions) is logged to **ImmuDB**, a cryptographically verifiable database:

- ✅ **Immutable**: Data cannot be modified or deleted
- ✅ **Cryptographically Verified**: Each entry is cryptographically signed
- ✅ **Complete History**: Full audit trail of all operations
- ✅ **Tamper Detection**: Immediate detection of any data manipulation attempts

See [IMMUDB_SETUP.md](IMMUDB_SETUP.md) for detailed documentation.

## 🔐 Security Features

### Authentication & Authorization
- **JWT Tokens**: Stateless authentication mechanism
- **BCrypt Hashing**: Secure password and UNIQUE PIN storage
- **Role-Based Access**: Protected endpoints based on user roles

### Consent Management
- **UNIQUE PIN Verification**: 6-digit PIN for address access
- **Token-Based Sharing**: Unique tokens for AIU access
- **Expiry Management**: Automatic deactivation of expired consents
- **Consent Types**:
  - `PERMANENT`: No expiration
  - `TEMPORARY`: Auto-expires after specified duration

### Data Protection
- Aadhaar numbers are masked (only last 4 digits visible)
- PINs are hashed using BCrypt (never stored in plain text)
- CORS configuration for secure cross-origin requests

## 🧮 DigiPin Algorithm

The system uses a proprietary grid-based algorithm to generate unique location codes:

```
Grid System (4x4):
F C 9 8
J 3 2 7
K 4 5 6
L M P T

Coverage: India (2.5°N to 38.5°N, 63.5°E to 99.5°E)
Format: XXX-XXX-XXXX (10 characters + 2 hyphens)
Example: FC9-823-7654
```

## 📊 Database Schema

### Key Entities

**User**
- id, userName, phoneNumber, emailId, password
- aadhaarNumber (masked), isAadhaarVerified

**DigitalAddress**
- id, userId, digitalAddress, generatedDigipin
- suffix, latitude, longitude, address
- activeConsentId, hasActiveConsent

**Consent**
- id, userId, digitalAddressId
- upiPinHash, consentToken, consentType
- createdAt, expiresAt, isActive

**AadhaarMockData**
- id, aadhaarNumber, dateOfBirth, fullName

## 🎯 Use Cases

### 1. E-Commerce Delivery
Customer creates a digital address and shares consent token with delivery partner for one-time location access.

### 2. Emergency Services
Share temporary consent with emergency responders without revealing permanent address details.

### 3. Ride-Sharing
Grant temporary location access to drivers that auto-expires after ride completion.

### 4. Property Rental
Landlords can share property addresses with verified tenants using consent-based access.

## 🛠️ Development

### Build Commands
```bash
# Clean build
mvn clean install

# Run tests
mvn test

# Package application
mvn package

# Run application
mvn spring-boot:run
```

### Configuration Properties

Key configurations in `application.properties`:
```properties
# Server Port
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/digipin_db
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000
```

## 📝 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

```
Copyright 2025 Tribhuvan-Web

Licensed under the Apache License, Version 2.0
```

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📧 Contact

**Project Maintainer:** Tribhuvan-Web

**Repository:** [https://github.com/Tribhuvan-Web/digipin-backend](https://github.com/Tribhuvan-Web/digipin-backend)

---

<div align="center">
  <strong>Built with ❤️ for Hackathon 2025</strong>
</div>