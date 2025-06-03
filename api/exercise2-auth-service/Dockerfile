# Etapa 1: build com Maven (tag válida: maven:3.8.8-openjdk-17-slim)
FROM maven:3.8.8-openjdk-17-slim AS build

WORKDIR /app

# Copia pom.xml e todo o código-fonte para dentro do container de build
COPY pom.xml .
COPY src ./src

# Gera o JAR (pulando testes)
RUN mvn clean package -DskipTests

# Etapa 2: runtime leve com OpenJDK
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copia o JAR gerado na fase de build
COPY --from=build /app/target/*.jar app.jar

# A porta configurada no docker-compose para auth é a 8001
EXPOSE 8001

ENTRYPOINT ["java", "-jar", "app.jar"]
