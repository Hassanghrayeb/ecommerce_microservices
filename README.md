# Hamster Mini Ecommerce — Backend

Spring Boot app using **Java 17**, **Maven**, **PostgreSQL**.

## Prerequisites
- Java 17 (JDK)
- Maven 3.8+
- PostgreSQL 13+

## Quick Start (from a zipped folder)
1. **Unzip & open terminal** in the project folder.
2. **Create DB** (example on Linux/macOS):
   ```sh
   psql -U postgres -c "CREATE DATABASE hamster;"
   psql -U postgres -c "CREATE USER hamster WITH PASSWORD 'hamster';"
   psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE hamster TO hamster;"

3. **Download and resolve dependencies**
    mvn spring-boot:run
4-**Build and run**
    mvn clean package -DskipTests
    java -jar target/*.war
