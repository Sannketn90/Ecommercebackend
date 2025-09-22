**🛒 Ecommerce Backend**

📌 Description
This is the backend of an Ecommerce Application built using **Spring Boot 3.5.3. It provides RESTful APIs for managing **products, users, orders, payments, and carts**, with **JWT authentication**, **role-based access control**, and **Redis caching** for performance optimization. The application uses **PostgreSQL** as the primary database and **Flyway** for schema versioning.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

 🚀 Features

- ✅ User registration and login with JWT authentication  
- 🔐 Role-based authorization (Admin/User)  
- 🛍️ CRUD operations for products  
- 🛒 Cart and Order management  
- 💳 Payment API integration via Spring WebClient  
- 🧠 Redis caching for frequent queries  
- 🧪 Unit and integration tests for service layers  
- 🧰 Global exception handling  
- 📜 API documentation with Swagger UI  
- 🛡️ Secure configuration (no sensitive data exposed)  
- 🗃️ Flyway-based DB schema versioning  

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

🧰 Technologies Used

| Category        | Tools / Technologies |
|-----------------|--------------------|
| Language        | Java 17            |
| Framework       | Spring Boot 3.5.3    |
| Security        | Spring Security + JWT |
| Database        | PostgreSQL         |
| ORM             | Spring Data JPA    |
| Caching         | Redis              |
| API Docs        | Swagger (springdoc-openapi) |
| Testing         | JUnit, Mockito     |
| Migration       | Flyway             |
| HTTP Client     | Spring WebClient   |
| Build Tool      | Maven              |
| Utilities       | Lombok             |

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

🧑‍💻 Getting Started

**🔧 Prerequisites**

- Java 17+  
- Maven  
- PostgreSQL  
- Redis (optional but recommended)  
- Git  

### ⚙️ Setup Instructions
```bash
# Clone the repo
git clone https://github.com/Sannketn90/Ecommercebackend.git
cd Ecommercebackend

# Configure DB and Redis in application.yml
# Run migrations via Flyway (auto-applied on startup)

# Build and run the app
mvn clean install
mvn spring-boot:run
