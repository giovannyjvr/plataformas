# Etapa 1: build com Maven (tag válida: maven:3.8.8-openjdk-17-slim)
FROM maven:3.8.8-jdk-17-slim AS build
WORKDIR /app

# Copia pom.xml e todo o código-fonte
COPY pom.xml .
COPY src ./src

# Gera o JAR (sem testes)
RUN mvn clean package -DskipTests

# Etapa 2: runtime leve
FROM openjdk:17-slim
WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/target/*.jar app.jar

# O portal roda em 8004 (conforme docker-compose)
EXPOSE 8004

ENTRYPOINT ["java", "-jar", "app.jar"]
