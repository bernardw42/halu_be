# Spring Boot Backend Setup Guide

This guide explains how to set up and run the Spring Boot backend and configure the database for this project.

---

## 1. Prerequisites

- **Java 17+** (or the version required by your project)
- **Maven** (or Gradle, depending on your build tool)
- **MySQL** (or MariaDB, or your preferred SQL database)
- **Git** (optional, for cloning the repository)

---

## 2. Clone the Repository

```sh
git clone <your-repo-url>
cd <your-project-folder>
```

---

## 3. Configure Database

You will need **two databases**:

- `halu_db` (main app database)
- `legacy_bank` (legacy customer database)

### 3.1 Create the main app database

```sql
CREATE DATABASE halu_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3.2 Create the legacy bank database

```sql
CREATE DATABASE legacy_bank;
USE legacy_bank;

CREATE TABLE legacy_customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    national_id VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
);

INSERT INTO legacy_customers (name, national_id)
VALUES ('buyer_bob', '1111111'),
       ('seller_anna', '1111112');
```

### 3.3 Update your Spring Boot `application.properties` or `application.yml` with your database credentials

```
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/halu_db
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

---

## 4. Create Database Tables

You can use the following SQL to create the required tables for the main app database (`halu_db`):

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('BUYER', 'SELLER') NOT NULL,
    profile_image_url VARCHAR(500) DEFAULT 'https://yourdomain.com/default-profile.png',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    national_id VARCHAR(20) UNIQUE AFTER username;
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    image_url TEXT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_id) REFERENCES users(id)
);

CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT,
    product_id BIGINT,
    quantity INT DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES carts(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, PAID, SHIPPED, CANCELLED
    total_price DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    FOREIGN KEY (buyer_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expiry_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

```

---

## 5. Build and Run the Spring Boot Application

### Using Maven

```sh
./mvnw spring-boot:run
```

or

```sh
mvn spring-boot:run
```

### Using Gradle

```sh
./gradlew bootRun
```

or

```sh
gradle bootRun
```

The backend will start on the port specified in your `application.properties` (default is `8080`).

---

## 6. API Endpoints

- The backend exposes RESTful endpoints for users, products, carts, and orders.
- You can test the endpoints using tools like Postman or via your frontend.

---

## 7. Notes

- Make sure your databases are running before starting the backend.
- If you use `spring.jpa.hibernate.ddl-auto=update`, Hibernate will auto-create/update tables, but for production use, prefer manual schema management.
- Adjust CORS settings in your Spring Boot config if you access the backend from a different frontend domain.

---

## 8. Troubleshooting

- **Database connection errors:** Check your DB credentials and that MySQL is running.
- **Port conflicts:** Change the `server.port` property in `application.properties` if needed.
- **Table/column errors:** Double-check your SQL schema and entity classes.

---

## 9. Useful Commands

- **Build the project:**  
  `mvn clean install` or `gradle build`
- **Run tests:**  
  `mvn test` or `gradle test`

---

## 10. Further Reading

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---
