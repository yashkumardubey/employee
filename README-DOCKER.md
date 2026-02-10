# Employee Management System - Docker Setup

## Prerequisites
- Docker installed (version 20.10+)
- Docker Compose installed (version 2.0+)

## Quick Start

### Option 1: Using Docker Compose (Recommended)

1. **Build and run the entire stack** (PostgreSQL + Application):
   ```bash
   docker-compose up --build
   ```

2. **Run in detached mode** (background):
   ```bash
   docker-compose up -d --build
   ```

3. **View logs**:
   ```bash
   docker-compose logs -f app
   ```

4. **Stop the application**:
   ```bash
   docker-compose down
   ```

5. **Stop and remove volumes** (clears database):
   ```bash
   docker-compose down -v
   ```

### Option 2: Using Docker Only

1. **Build the image**:
   ```bash
   docker build -t employee-management:latest .
   ```

2. **Run the container** (requires external PostgreSQL):
   ```bash
   docker run -d \
     -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/postgresdb \
     -e SPRING_DATASOURCE_USERNAME=postgres \
     -e SPRING_DATASOURCE_PASSWORD=root \
     --name employee-app \
     employee-management:latest
   ```

## Access the Application

- **API Base URL**: http://localhost:8080/api
- **Home Page**: http://localhost:8080/
- **Database**: localhost:5432 (when using docker-compose)

## API Examples

### Add Employee
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "username: dipali" \
  -H "password: 1234" \
  -H "Content-Type: application/json" \
  -d '{
    "empid": "EMP001",
    "fname": "John Doe",
    "salary": 50000
  }'
```

### Get Employee by ID
```bash
curl -X GET http://localhost:8080/api/EMP001 \
  -H "username: dipali" \
  -H "password: 1234"
```

### Get All Employees (ID and Name)
```bash
curl -X GET http://localhost:8080/api
```

### Export to PDF
```bash
curl -X GET http://localhost:8080/api/export-to-pdf -o employees.pdf
```

### Export to Excel
```bash
curl -X GET http://localhost:8080/api/export-to-excel -o employees.xlsx
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| SPRING_DATASOURCE_URL | jdbc:postgresql://postgres:5432/postgresdb | Database connection URL |
| SPRING_DATASOURCE_USERNAME | postgres | Database username |
| SPRING_DATASOURCE_PASSWORD | root | Database password |
| SPRING_JPA_HIBERNATE_DDL_AUTO | update | Hibernate DDL auto mode |
| SPRING_JPA_SHOW_SQL | true | Show SQL in logs |
| JAVA_OPTS | -Xmx512m -Xms256m | JVM options |

## Troubleshooting

### Application won't start
```bash
# Check application logs
docker-compose logs app

# Check if PostgreSQL is ready
docker-compose logs postgres
```

### Database connection issues
```bash
# Restart the services
docker-compose restart

# Rebuild from scratch
docker-compose down -v
docker-compose up --build
```

### Port already in use
```bash
# Change ports in docker-compose.yml
# For app: "8081:8080" instead of "8080:8080"
# For postgres: "5433:5432" instead of "5432:5432"
```

## Production Considerations

⚠️ **Before deploying to production, fix these security issues:**

1. Remove hardcoded credentials from code
2. Use environment variables for all sensitive data
3. Implement proper authentication (Spring Security with JWT)
4. Use HTTPS/TLS
5. Enable CORS properly
6. Add rate limiting
7. Use secrets management (Docker Secrets, Kubernetes Secrets, etc.)
8. Change default database credentials
9. Add input validation
10. Implement proper logging and monitoring

## Notes

- The application uses WAR packaging, which is suitable for traditional servlet containers
- For cloud-native deployments, consider changing to JAR packaging
- Database data persists in a Docker volume named `postgres_data`
- The application automatically creates database schema on startup
