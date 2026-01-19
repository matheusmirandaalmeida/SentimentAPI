# ETAPA 1 - BUILD DA APLICAÇÃO (MAVEN)
# Imagem com Maven + Java 17
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Diretório de trabalho dentro do container
WORKDIR /app

# Copia apenas o pom.xml primeiro
# Isso permite cache de dependências (build mais rápido)
COPY pom.xml .

# Baixa todas as dependências sem compilar o projeto
RUN mvn dependency:go-offline

# Copia o código-fonte
COPY src ./src

# Compila a aplicação e gera o .jar
# -DskipTests porque em produção não rodamos testes
RUN mvn clean package -DskipTests


#############################################
# ETAPA 2 - IMAGEM FINAL (APENAS JRE)

# Imagem bem mais leve apenas com Java Runtime
FROM eclipse-temurin:17-jre

# Diretório de trabalho
WORKDIR /app

# Copia o .jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Porta padrão do Spring Boot
EXPOSE 8080

# Comando de inicialização do container
ENTRYPOINT ["java", "-jar", "app.jar"]