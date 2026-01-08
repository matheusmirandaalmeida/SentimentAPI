# ============ BUILD STAGE ============
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar apenas os arquivos necessários primeiro (cache layers)
COPY pom.xml .
COPY src ./src

# Baixar dependências primeiro (cache layer)
RUN mvn dependency:go-offline -B

# Compilar e empacotar
RUN mvn clean package -DskipTests

# ============ RUNTIME STAGE ============
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Instalar curl para healthcheck
RUN apk add --no-cache curl

# Copiar JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Criar usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]