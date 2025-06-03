# Etapa 1: build com Maven (tag válida: maven:3.8.8-openjdk-17-slim)
FROM maven:3.8.8-openjdk-17-slim AS build

WORKDIR /app

# Copia pom.xml e src
COPY pom.xml .
COPY src ./src

# Gera o JAR (pulando testes)
RUN mvn clean package -DskipTests

# Etapa 2: imagem enxuta para execução
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copia o JAR construído
COPY --from=build /app/target/*.jar app.jar

# O serviço de order usa a porta 8003 (conforme docker-compose)
EXPOSE 8003

ENTRYPOINT ["java", "-jar", "app.jar"]
