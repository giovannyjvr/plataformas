# Etapa 1: build com Maven (tag válida: maven:3.8.8-openjdk-17-slim)
FROM maven:3.8.8-openjdk-17-slim AS build

WORKDIR /app

# Copia pom.xml e parâmetros do código
COPY pom.xml .
COPY src ./src

# Gera o JAR (pulando testes)
RUN mvn clean package -DskipTests

# Etapa 2: runtime leve com OpenJDK
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/target/*.jar app.jar

# No docker-compose, product expõe a porta 8002
EXPOSE 8002

ENTRYPOINT ["java", "-jar", "app.jar"]
