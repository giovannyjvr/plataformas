# Etapa 1: build com Maven
#   trocamos para um tag que existe: maven:3.8.8-jdk-17-slim
FROM maven:3.8.8-jdk-17-slim AS build

WORKDIR /app

# Copia pom.xml e parâmetros do código
COPY pom.xml .
COPY src ./src

# Gera o JAR (pulando testes)
RUN mvn clean package -DskipTests

# Etapa 2: runtime leve com OpenJDK
#   trocamos para openjdk:17-slim (que é o tag válido)
FROM openjdk:17-slim

WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8002

ENTRYPOINT ["java", "-jar", "app.jar"]
